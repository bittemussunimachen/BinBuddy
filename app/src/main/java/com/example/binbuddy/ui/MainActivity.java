package com.example.binbuddy.ui;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.binbuddy.R;
import com.google.android.material.card.MaterialCardView;

public class MainActivity extends AppCompatActivity {

    private static final int SCAN_REQUEST_CODE = 100;

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
                            Toast.makeText(this, "Suche wird geöffnet...", Toast.LENGTH_SHORT).show();
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
                        Toast.makeText(this, "Barcode gescannt: " + barcode, Toast.LENGTH_LONG).show();
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
}
