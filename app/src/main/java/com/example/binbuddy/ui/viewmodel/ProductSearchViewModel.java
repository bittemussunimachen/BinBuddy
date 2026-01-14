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

public class ProductSearchViewModel extends AndroidViewModel {
    private final ExecutorService networkExecutor = Executors.newSingleThreadExecutor();
    
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

        java.net.HttpURLConnection connection = null;
        try {
            java.net.URL url = new java.net.URL(urlBuilder.toString());
            connection = (java.net.HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(8000);
            connection.setReadTimeout(8000);
            
            int status = connection.getResponseCode();
            StringBuilder builder = new StringBuilder();
            java.io.InputStream rawStream = (status >= 200 && status < 300)
                    ? connection.getInputStream()
                    : connection.getErrorStream();
            if (rawStream == null) {
                rawStream = java.io.InputStream.nullInputStream();
            }
            
            try (java.io.InputStream stream = rawStream;
                 java.io.BufferedReader reader = new java.io.BufferedReader(
                         new java.io.InputStreamReader(stream, java.nio.charset.StandardCharsets.UTF_8))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    builder.append(line);
                }
            }
            return parseProducts(builder.toString(), germanyOnly);
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
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
                                        quantity, new ArrayList<>(), "", "", imageUrl);
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
