package com.example.binbuddy.ui.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.binbuddy.domain.model.Product;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class ProductSearchViewModel extends AndroidViewModel {
    private final ExecutorService networkExecutor = Executors.newSingleThreadExecutor();
    private final OkHttpClient httpClient = new OkHttpClient.Builder()
            .connectTimeout(15, TimeUnit.SECONDS)
            .readTimeout(20, TimeUnit.SECONDS)
            .callTimeout(25, TimeUnit.SECONDS)
            .build();
    
    private final MutableLiveData<List<Product>> searchResults = new MutableLiveData<>(new ArrayList<>());
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>(false);
    private final MutableLiveData<String> error = new MutableLiveData<>();

    public ProductSearchViewModel(@NonNull Application application) {
        super(application);
    }

    public LiveData<List<Product>> getSearchResults() {
        return searchResults;
    }

    public LiveData<Boolean> getIsLoading() {
        return isLoading;
    }

    public LiveData<String> getError() {
        return error;
    }

    public void searchProducts(String query, boolean germanyOnly) {
        if (query == null || query.trim().isEmpty()) {
            error.setValue("Suchbegriff eingeben");
            return;
        }

        isLoading.setValue(true);
        error.setValue(null);
        
        networkExecutor.execute(() -> {
            try {
                List<Product> results = searchOpenFoodFacts(query.trim(), germanyOnly);
                searchResults.postValue(results);
                isLoading.postValue(false);
            } catch (Exception e) {
                android.util.Log.e("ProductSearchViewModel", "Search error", e);
                error.postValue("Fehler bei der Suche: " + e.getMessage());
                isLoading.postValue(false);
            }
        });
    }

    private List<Product> searchOpenFoodFacts(String term, boolean germanyOnly) throws Exception {
        String encoded = java.net.URLEncoder.encode(term, java.nio.charset.StandardCharsets.UTF_8);
        StringBuilder urlBuilder = new StringBuilder("https://world.openfoodfacts.org/cgi/search.pl?");
        urlBuilder.append("search_terms=").append(encoded);
        urlBuilder.append("&search_simple=1&action=process&json=1&page_size=25");
        if (germanyOnly) {
            urlBuilder.append("&countries_tags_en=Germany");
        }

        Request request = new Request.Builder()
                .url(urlBuilder.toString())
                .get()
                .header("Accept", "application/json")
                .header("User-Agent", "BinBuddy/1.0 (Android)")
                .build();

        try (Response response = httpClient.newCall(request).execute()) {
            String body = response.body() != null ? response.body().string() : "";
            if (!response.isSuccessful()) {
                throw new java.io.IOException("Search request failed: HTTP " + response.code());
            }
            return parseProducts(body, germanyOnly);
        }
    }

    private List<Product> parseProducts(String rawJson, boolean germanyOnly) throws org.json.JSONException {
        org.json.JSONObject root = new org.json.JSONObject(rawJson);
        org.json.JSONArray products = root.optJSONArray("products");
        List<Product> items = new ArrayList<>();
        if (products == null) {
            return items;
        }

        for (int i = 0; i < products.length(); i++) {
            org.json.JSONObject obj = products.optJSONObject(i);
            if (obj == null) {
                continue;
            }

            if (germanyOnly && !isGerman(obj)) {
                continue;
            }

            String name = obj.optString("product_name", "");
            if (android.text.TextUtils.isEmpty(name)) {
                name = obj.optString("generic_name", "");
            }
            String brand = obj.optString("brands", "");
            String categoriesStr = obj.optString("categories", "");
            String packaging = obj.optString("packaging", obj.optString("packaging_tags", ""));
            String code = obj.optString("code", "");
            String quantity = obj.optString("quantity", "");
            String imageUrl = obj.optString("image_url", "");
            String ecoGrade = obj.optString("ecoscore_grade", "");
            int ecoScoreRaw = obj.optInt("ecoscore_score", -1);
            Integer ecoScore = ecoScoreRaw >= 0 ? ecoScoreRaw : null;

            if (android.text.TextUtils.isEmpty(name)) {
                continue;
            }

            List<String> categories = new ArrayList<>();
            if (!categoriesStr.isEmpty()) {
                String[] catArray = categoriesStr.split(",");
                for (String cat : catArray) {
                    if (!cat.trim().isEmpty()) {
                        categories.add(cat.trim());
                    }
                }
            }

            Product product = new Product(code, name, brand, categories, packaging,
                                        quantity, new ArrayList<>(), "", "", imageUrl,
                                        ecoGrade, ecoScore);
            product.setId(code);
            items.add(product);
        }

        return items;
    }

    private boolean isGerman(org.json.JSONObject obj) {
        org.json.JSONArray countryTags = obj.optJSONArray("countries_tags");
        if (countryTags != null) {
            for (int i = 0; i < countryTags.length(); i++) {
                String tag = countryTags.optString(i, "").toLowerCase();
                if (tag.contains("germany") || tag.contains("deutschland")) {
                    return true;
                }
            }
        }
        String countries = obj.optString("countries", "").toLowerCase();
        return countries.contains("germany") || countries.contains("deutschland");
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        networkExecutor.shutdownNow();
    }
}
