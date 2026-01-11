package com.example.binbuddy.ui;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.binbuddy.R;
import com.google.android.material.textfield.TextInputEditText;
import android.widget.CheckBox;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ProductSearchActivity extends AppCompatActivity {

    private TextInputEditText etSearch;
    private CheckBox checkGermany;
    private RecyclerView rvProducts;
    private ProgressBar progressBar;
    private TextView tvEmpty;
    private ProductAdapter adapter;
    private final ExecutorService networkExecutor = Executors.newSingleThreadExecutor();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        View btnBack = findViewById(R.id.btnBack);
        if (btnBack != null) {
            btnBack.setOnClickListener(v -> finish());
        }

        etSearch = findViewById(R.id.etSearch);
        checkGermany = findViewById(R.id.checkGermany);
        rvProducts = findViewById(R.id.rvProducts);
        progressBar = findViewById(R.id.progressBar);
        tvEmpty = findViewById(R.id.tvEmpty);

        adapter = new ProductAdapter(new ArrayList<>());
        if (rvProducts != null) {
            rvProducts.setLayoutManager(new LinearLayoutManager(this));
            rvProducts.setAdapter(adapter);
        } else {
            Toast.makeText(this, "Listenansicht nicht gefunden", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        Button btnSearch = findViewById(R.id.btnSearch);
        if (btnSearch != null) {
            btnSearch.setOnClickListener(v -> startSearch());
        }

        if (etSearch != null) {
            etSearch.setOnEditorActionListener((v, actionId, event) -> {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    startSearch();
                    return true;
                }
                return false;
            });
        }
    }

    private void startSearch() {
        String term = etSearch != null ? etSearch.getText().toString().trim() : "";
        if (TextUtils.isEmpty(term)) {
            Toast.makeText(this, "Suchbegriff eingeben", Toast.LENGTH_SHORT).show();
            return;
        }
        boolean germanyOnly = checkGermany != null && checkGermany.isChecked();
        showLoading(true);
        networkExecutor.execute(() -> {
            try {
                List<ProductItem> results = searchOpenFoodFacts(term, germanyOnly);
                runOnUiThread(() -> {
                    showLoading(false);
                    adapter.updateData(results);
                    tvEmpty.setVisibility(results.isEmpty() ? View.VISIBLE : View.GONE);
                });
            } catch (Exception e) {
                android.util.Log.e("ProductSearch", "Search error", e);
                runOnUiThread(() -> {
                    showLoading(false);
                    Toast.makeText(this, "Fehler bei der Suche", Toast.LENGTH_LONG).show();
                });
            }
        });
    }

    private void showLoading(boolean loading) {
        if (progressBar != null) {
            progressBar.setVisibility(loading ? View.VISIBLE : View.GONE);
        }
    }

    private List<ProductItem> searchOpenFoodFacts(String term, boolean germanyOnly) throws IOException, JSONException {
        String encoded = URLEncoder.encode(term, StandardCharsets.UTF_8);
        StringBuilder urlBuilder = new StringBuilder("https://world.openfoodfacts.org/cgi/search.pl?");
        urlBuilder.append("search_terms=").append(encoded);
        urlBuilder.append("&search_simple=1&action=process&json=1&page_size=25");
        if (germanyOnly) {
            urlBuilder.append("&countries=Germany");
        }

        HttpURLConnection connection = null;
        try {
            URL url = new URL(urlBuilder.toString());
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(8000);
            connection.setReadTimeout(8000);
            int status = connection.getResponseCode();
            StringBuilder builder = new StringBuilder();
            try (InputStream stream = (status >= 200 && status < 300)
                    ? connection.getInputStream()
                    : connection.getErrorStream();
                 BufferedReader reader = new BufferedReader(new InputStreamReader(stream, StandardCharsets.UTF_8))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    builder.append(line);
                }
            }
            return parseProducts(builder.toString(), germanyOnly);
        } finally {
            if (connection != null) connection.disconnect();
        }
    }

    private List<ProductItem> parseProducts(String rawJson, boolean germanyOnly) throws JSONException {
        JSONObject root = new JSONObject(rawJson);
        JSONArray products = root.optJSONArray("products");
        List<ProductItem> items = new ArrayList<>();
        if (products == null) return items;

        for (int i = 0; i < products.length(); i++) {
            JSONObject obj = products.optJSONObject(i);
            if (obj == null) continue;

            if (germanyOnly && !isGerman(obj)) {
                continue;
            }

            String name = obj.optString("product_name", "");
            if (TextUtils.isEmpty(name)) name = obj.optString("generic_name", "");
            String brand = obj.optString("brands", "");
            String categories = obj.optString("categories", "");
            String packaging = obj.optString("packaging", obj.optString("packaging_tags", ""));
            String code = obj.optString("code", "");
            String quantity = obj.optString("quantity", "");

            if (TextUtils.isEmpty(name)) continue;
            items.add(new ProductItem(name, brand, categories, packaging, code, quantity));
        }

        return items;
    }

    private boolean isGerman(JSONObject obj) {
        JSONArray countryTags = obj.optJSONArray("countries_tags");
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
    protected void onDestroy() {
        super.onDestroy();
        try {
            networkExecutor.shutdownNow();
        } catch (Exception e) {
            android.util.Log.e("ProductSearch", "Error shutting down executor", e);
        }
    }

    static class ProductItem {
        final String name;
        final String brand;
        final String categories;
        final String packaging;
        final String code;
        final String quantity;

        ProductItem(String name, String brand, String categories, String packaging, String code, String quantity) {
            this.name = name;
            this.brand = brand;
            this.categories = categories;
            this.packaging = packaging;
            this.code = code;
            this.quantity = quantity;
        }
    }
}

