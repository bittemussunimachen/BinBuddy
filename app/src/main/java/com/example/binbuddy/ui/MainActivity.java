package com.example.binbuddy.ui;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.example.binbuddy.R;
import com.example.binbuddy.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    private ActivityMainBinding binding;
    private ActivityResultLauncher<Intent> barcodeLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setupActivityResultLauncher();
        setupToolbar();
        setupClickListeners();
    }

    private void setupClickListeners() {
        binding.btnScan.setOnClickListener(v -> openScanner());
        binding.btnSearch.setOnClickListener(v -> openSearch());
        binding.btnManualEntry.setOnClickListener(v -> openManualEntry());
    }

    private void setupToolbar() {
        binding.toolbar.setNavigationOnClickListener(v -> openBinGuide());
    }

    private void setupActivityResultLauncher() {
        barcodeLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        navigateToProductDetailIfValid(
                                result.getData().getStringExtra(ProductDetailActivity.EXTRA_BARCODE)
                        );
                    }
                }
        );
    }

    private void openScanner() {
        try {
            Intent intent = new Intent(MainActivity.this, ScannerActivity.class);
            if (intent.resolveActivity(getPackageManager()) != null) {
                barcodeLauncher.launch(intent);
            } else {
                showToast(R.string.main_error_open_scanner);
                Log.e(TAG, "ScannerActivity not found");
            }
        } catch (Exception e) {
            Log.e(TAG, "Error opening scanner", e);
            showToast(R.string.main_error_open_scanner_failed);
        }
    }

    private void openManualEntry() {
        Intent intent = new Intent(this, ManualEntryActivity.class);
        barcodeLauncher.launch(intent);
    }

    private void openBinGuide() {
        Intent intent = new Intent(this, BinGuideActivity.class);
        startActivity(intent);
    }

    private void navigateToProductDetailIfValid(String barcode) {
        if (barcode == null || barcode.trim().isEmpty()) {
            return;
        }
        navigateToProductDetail(barcode.trim());
    }

    private void navigateToProductDetail(String barcode) {
        Intent intent = new Intent(this, ProductDetailActivity.class);
        intent.putExtra(ProductDetailActivity.EXTRA_BARCODE, barcode);
        startActivity(intent);
    }

    private void openSearch() {
        try {
            Intent intent = new Intent(MainActivity.this, ProductSearchActivity.class);
            startActivity(intent);
        } catch (Exception e) {
            Log.e(TAG, "Error opening search", e);
            showToast(R.string.main_error_open_search);
        }
    }

    private void showToast(int resId) {
        Toast.makeText(this, getString(resId), Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding = null;
    }
}
