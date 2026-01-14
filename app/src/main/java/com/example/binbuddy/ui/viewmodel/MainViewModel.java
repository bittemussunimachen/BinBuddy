package com.example.binbuddy.ui.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.binbuddy.domain.model.Product;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainViewModel extends AndroidViewModel {
    private final ExecutorService networkExecutor = Executors.newSingleThreadExecutor();
    
    private final MutableLiveData<Product> product = new MutableLiveData<>();
    private final MutableLiveData<List<Product>> recentScans = new MutableLiveData<>();
    private final MutableLiveData<List<Product>> favorites = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>(false);
    private final MutableLiveData<String> error = new MutableLiveData<>();

    public MainViewModel(@NonNull Application application) {
        super(application);
        loadRecentScans();
        loadFavorites();
    }

    public LiveData<Product> getProduct() {
        return product;
    }

    public LiveData<List<Product>> getRecentScans() {
        return recentScans;
    }

    public LiveData<List<Product>> getFavorites() {
        return favorites;
    }

    public LiveData<Boolean> getIsLoading() {
        return isLoading;
    }

    public LiveData<String> getError() {
        return error;
    }

    public void loadProduct(String barcode) {
        isLoading.setValue(true);
        error.setValue(null);
        
        networkExecutor.execute(() -> {
            try {
                Product fetchedProduct = fetchProductFromApi(barcode);
                product.postValue(fetchedProduct);
                isLoading.postValue(false);
            } catch (Exception e) {
                android.util.Log.e("MainViewModel", "Error fetching product", e);
                error.postValue("Fehler bei der Produktsuche: " + e.getMessage());
                isLoading.postValue(false);
            }
        });
    }

    public void loadRecentScans() {
        // TODO: Load from database when repository is implemented
        recentScans.setValue(new ArrayList<>());
    }

    public void loadFavorites() {
        // TODO: Load from database when repository is implemented
        favorites.setValue(new ArrayList<>());
    }

    private Product fetchProductFromApi(String barcode) throws Exception {
        // Temporary implementation - will be moved to repository later
        String urlString = "https://world.openfoodfacts.org/api/v0/product/" + 
                          java.net.URLEncoder.encode(barcode, java.nio.charset.StandardCharsets.UTF_8) + ".json";
        
        java.net.HttpURLConnection connection = null;
        StringBuilder builder = new StringBuilder();
        
        try {
            java.net.URL url = new java.net.URL(urlString);
            connection = (java.net.HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(8000);
            connection.setReadTimeout(8000);

            int status = connection.getResponseCode();
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
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }

        org.json.JSONObject root = new org.json.JSONObject(builder.toString());
        int status = root.optInt("status", 0);
        if (status != 1) {
            String err = root.optString("status_verbose", "Produkt nicht gefunden");
            throw new Exception("OFF lookup failed: " + err);
        }

        org.json.JSONObject productJson = root.getJSONObject("product");
        String name = productJson.optString("product_name", "");
        String brand = productJson.optString("brands", "");
        String quantity = productJson.optString("quantity", "");
        String categoriesStr = productJson.optString("categories", "");
        String packaging = productJson.optString("packaging", productJson.optString("packaging_tags", ""));
        String generic = productJson.optString("generic_name", "");
        String labels = productJson.optString("labels", "");
        String imageUrl = productJson.optString("image_url", "");
        String ecoGrade = productJson.optString("ecoscore_grade", "");
        int ecoScoreRaw = productJson.optInt("ecoscore_score", -1);
        Integer ecoScore = ecoScoreRaw >= 0 ? ecoScoreRaw : null;

        List<String> categories = new ArrayList<>();
        if (!categoriesStr.isEmpty()) {
            String[] catArray = categoriesStr.split(",");
            for (String cat : catArray) {
                if (!cat.trim().isEmpty()) {
                    categories.add(cat.trim());
                }
            }
        }

        List<String> ingredients = new ArrayList<>();
        org.json.JSONArray ingredientsArray = productJson.optJSONArray("ingredients");
        if (ingredientsArray != null) {
            for (int i = 0; i < ingredientsArray.length(); i++) {
                org.json.JSONObject ing = ingredientsArray.optJSONObject(i);
                if (ing != null) {
                    String text = ing.optString("text", "");
                    if (!text.isEmpty()) {
                        ingredients.add(text);
                    }
                }
            }
        }

        Product product = new Product(barcode, name, brand, categories, packaging,
                                     quantity, ingredients, labels, generic, imageUrl,
                                     ecoGrade, ecoScore);
        product.setId(barcode);
        return product;
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        networkExecutor.shutdownNow();
    }
}
