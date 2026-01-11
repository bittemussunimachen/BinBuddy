package com.example.binbuddy.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.binbuddy.R;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.snackbar.Snackbar;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONArray;

public class MainActivity extends AppCompatActivity {

    private static final int SCAN_REQUEST_CODE = 100;
    private static final String OFF_BASE_URL = "https://world.openfoodfacts.org/api/v0/product/";
    private final ExecutorService networkExecutor = Executors.newSingleThreadExecutor();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            setContentView(R.layout.activity_main);
            setupClickListeners();
        } catch (Exception e) {
            e.printStackTrace();
            android.util.Log.e("MainActivity", "Error in onCreate", e);
        }
    }

    private void setupClickListeners() {
        View tvAvatar = findViewById(R.id.tvAvatar);
        if (tvAvatar != null) {
            tvAvatar.setOnClickListener(v -> {
                Toast.makeText(this, "Profil wird geöffnet...", Toast.LENGTH_SHORT).show();
            });
        }

        MaterialCardView cardScan = findViewById(R.id.cardScan);
        if (cardScan != null) {
            cardScan.setOnClickListener(v -> openScanner());
        }

        Button btnScanNow = findViewById(R.id.btnScanNow);
        if (btnScanNow != null) {
            btnScanNow.setOnClickListener(v -> openScanner());
        }

        MaterialCardView cardWhatBin = findViewById(R.id.cardWhatBin);
        if (cardWhatBin != null) {
            cardWhatBin.setOnClickListener(v -> {
                Toast.makeText(this, "Bin Guide wird geöffnet...", Toast.LENGTH_SHORT).show();
            });
        }

        MaterialCardView cardPfand = findViewById(R.id.cardPfand);
        if (cardPfand != null) {
            cardPfand.setOnClickListener(v -> {
                Toast.makeText(this, "Pfand-Info wird geöffnet...", Toast.LENGTH_SHORT).show();
            });
        }

        MaterialCardView cardCalendar = findViewById(R.id.cardCalendar);
        if (cardCalendar != null) {
            cardCalendar.setOnClickListener(v -> {
                Toast.makeText(this, "Waste Calendar wird geöffnet...", Toast.LENGTH_SHORT).show();
            });
        }

        MaterialCardView cardRecentProduct = findViewById(R.id.cardRecentProduct);
        if (cardRecentProduct != null) {
            cardRecentProduct.setOnClickListener(v -> {
                Toast.makeText(this, "Produktdetails werden geöffnet...", Toast.LENGTH_SHORT).show();
            });
        }

        TextView tvViewAll = findViewById(R.id.tvViewAll);
        if (tvViewAll != null) {
            tvViewAll.setOnClickListener(v -> {
                Toast.makeText(this, "Scan-Historie wird geöffnet...", Toast.LENGTH_SHORT).show();
            });
        }
        try {
            com.google.android.material.bottomnavigation.BottomNavigationView bottomNav = 
                findViewById(R.id.bottomNav);
            if (bottomNav != null) {
                bottomNav.setOnItemSelectedListener(item -> {
                    try {
                        int itemId = item.getItemId();
                        if (itemId == R.id.nav_home) {
                            return true;
                        } else if (itemId == R.id.nav_scan) {
                            openScanner();
                            return true;
                        } else if (itemId == R.id.nav_map) {
                            Toast.makeText(this, "Karte wird geöffnet...", Toast.LENGTH_SHORT).show();
                            return true;
                        } else if (itemId == R.id.nav_search) {
                            openSearch();
                            return true;
                        }
                    } catch (Exception e) {
                        android.util.Log.e("MainActivity", "Error in bottom nav listener", e);
                    }
                    return false;
                });
            }
        } catch (Exception e) {
            android.util.Log.e("MainActivity", "Error setting up bottom navigation", e);
        }
    }

    private void openScanner() {
        try {
            Intent intent = new Intent(MainActivity.this, ScannerActivity.class);
            if (intent.resolveActivity(getPackageManager()) != null) {
                startActivityForResult(intent, SCAN_REQUEST_CODE);
            } else {
                Toast.makeText(this, "Scanner konnte nicht geöffnet werden", Toast.LENGTH_SHORT).show();
                android.util.Log.e("MainActivity", "ScannerActivity not found");
            }
        } catch (Exception e) {
            android.util.Log.e("MainActivity", "Error opening scanner", e);
            Toast.makeText(this, "Fehler beim Öffnen des Scanners", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {
            if (requestCode == SCAN_REQUEST_CODE && resultCode == RESULT_OK) {
                if (data != null) {
                    String barcode = data.getStringExtra("barcode");
                    if (barcode != null && !barcode.isEmpty()) {
                        fetchProductDetails(barcode);
                    } else {
                        android.util.Log.w("MainActivity", "Barcode is null or empty");
                    }
                } else {
                    android.util.Log.w("MainActivity", "Intent data is null");
                }
            }
        } catch (Exception e) {
            android.util.Log.e("MainActivity", "Error in onActivityResult", e);
        }
    }

    private void fetchProductDetails(String ean) {
        Toast.makeText(this, "Frage Produktdaten ab...", Toast.LENGTH_SHORT).show();

        networkExecutor.execute(() -> {
            try {
                ProductInfo product = fetchFromOpenFoodFacts(ean);

                runOnUiThread(() -> {
                    showProductResult(product);
                });
            } catch (Exception e) {
                android.util.Log.e("MainActivity", "Fehler bei Produktsuche", e);
                runOnUiThread(() -> Toast.makeText(this, "Fehler bei der Produktsuche", Toast.LENGTH_LONG).show());
            }
        });
    }

    private ProductInfo fetchFromOpenFoodFacts(String ean) throws IOException, JSONException {
        String urlString = OFF_BASE_URL + URLEncoder.encode(ean, StandardCharsets.UTF_8) + ".json";
        HttpURLConnection connection = null;
        StringBuilder builder = new StringBuilder();
        try {
            URL url = new URL(urlString);
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(8000);
            connection.setReadTimeout(8000);

            int status = connection.getResponseCode();
            InputStream rawStream = (status >= 200 && status < 300)
                    ? connection.getInputStream()
                    : connection.getErrorStream();
            if (rawStream == null) {
                rawStream = InputStream.nullInputStream();
            }
            try (InputStream stream = rawStream;
                 BufferedReader reader = new BufferedReader(new InputStreamReader(stream, StandardCharsets.UTF_8))) {
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

        JSONObject root = new JSONObject(builder.toString());
        int status = root.optInt("status", 0);
        if (status != 1) {
            String err = root.optString("status_verbose", "Produkt nicht gefunden");
            throw new IOException("OFF lookup failed: " + err);
        }

        JSONObject product = root.getJSONObject("product");
        String name = product.optString("product_name", "");
        String brand = product.optString("brands", "");
        String quantity = product.optString("quantity", "");
        String categories = product.optString("categories", "");
        String packaging = product.optString("packaging", product.optString("packaging_tags", ""));
        String generic = product.optString("generic_name", "");
        String labels = product.optString("labels", "");

        List<String> ingredients = new ArrayList<>();
        JSONArray ingredientsArray = product.optJSONArray("ingredients");
        if (ingredientsArray != null) {
            for (int i = 0; i < ingredientsArray.length(); i++) {
                JSONObject ing = ingredientsArray.optJSONObject(i);
                if (ing != null) {
                    String text = ing.optString("text", "");
                    if (!text.isEmpty()) {
                        ingredients.add(text);
                    }
                }
            }
        }

        return new ProductInfo(name, brand, quantity, categories, packaging, generic, labels, ingredients);
    }

    private void showProductResult(ProductInfo product) {
        if (product == null || product.name.isEmpty()) {
            Toast.makeText(this, "Keine Produktdaten gefunden", Toast.LENGTH_LONG).show();
            return;
        }

        StringBuilder msg = new StringBuilder();
        msg.append(product.name);
        if (!product.brand.isEmpty()) msg.append(" (").append(product.brand).append(")");
        if (!product.generic.isEmpty()) msg.append("\n").append(product.generic);
        if (!product.quantity.isEmpty()) msg.append("\nMenge: ").append(product.quantity);
        if (!product.categories.isEmpty()) msg.append("\nKategorien: ").append(product.categories);
        if (!product.packaging.isEmpty()) msg.append("\nVerpackung: ").append(product.packaging);
        if (!product.labels.isEmpty()) msg.append("\nLabels: ").append(product.labels);

        Toast.makeText(this, msg.toString(), Toast.LENGTH_LONG).show();

        View root = findViewById(android.R.id.content);
        if (root != null && !product.ingredients.isEmpty()) {
            String ingredients = String.join(", ", product.ingredients);
            Snackbar.make(root, "Zutaten: " + ingredients, Snackbar.LENGTH_LONG).show();
        }
    }

    private void openSearch() {
        try {
            Intent intent = new Intent(MainActivity.this, ProductSearchActivity.class);
            startActivity(intent);
        } catch (Exception e) {
            android.util.Log.e("MainActivity", "Error opening search", e);
            Toast.makeText(this, "Suche konnte nicht geöffnet werden", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            networkExecutor.shutdownNow();
        } catch (Exception e) {
            android.util.Log.e("MainActivity", "Error shutting down networkExecutor", e);
        }
    }

    private static class ProductInfo {
        final String name;
        final String brand;
        final String quantity;
        final String categories;
        final String packaging;
        final String generic;
        final String labels;
        final List<String> ingredients;

        ProductInfo(String name,
                    String brand,
                    String quantity,
                    String categories,
                    String packaging,
                    String generic,
                    String labels,
                    List<String> ingredients) {
            this.name = name == null ? "" : name;
            this.brand = brand == null ? "" : brand;
            this.quantity = quantity == null ? "" : quantity;
            this.categories = categories == null ? "" : categories;
            this.packaging = packaging == null ? "" : packaging;
            this.generic = generic == null ? "" : generic;
            this.labels = labels == null ? "" : labels;
            this.ingredients = ingredients == null ? Collections.emptyList() : ingredients;
        }
    }
}
