package com.example.binbuddy.ui;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.binbuddy.R;
import com.example.binbuddy.databinding.ActivityMainBinding;
import com.example.binbuddy.ui.viewmodel.MainViewModel;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    private ActivityMainBinding binding;
    private MainViewModel viewModel;
    private ActivityResultLauncher<Intent> scannerLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setupViewModel();
        setupClickListeners();
        setupActivityResultLauncher();
        observeViewModel();
    }

    private void setupViewModel() {
        viewModel = new ViewModelProvider(this).get(MainViewModel.class);
    }

    private void setupClickListeners() {
        binding.tvAvatar.setOnClickListener(v -> {
            showToast(R.string.main_open_profile);
        });

        binding.cardScan.setOnClickListener(v -> openScanner());
        binding.btnScanNow.setOnClickListener(v -> openScanner());

        binding.cardWhatBin.setOnClickListener(v -> {
            showToast(R.string.main_open_bin_guide);
        });

        binding.cardPfand.setOnClickListener(v -> {
            showToast(R.string.main_open_pfand_info);
        });

        binding.cardCalendar.setOnClickListener(v -> {
            showToast(R.string.main_open_waste_calendar);
        });

        binding.cardRecentProduct.setOnClickListener(v -> {
            showToast(R.string.main_open_product_details);
        });

        binding.tvViewAll.setOnClickListener(v -> {
            showToast(R.string.main_open_scan_history);
        });

        binding.bottomNav.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.nav_home) {
                return true;
            } else if (itemId == R.id.nav_scan) {
                openScanner();
                return true;
            } else if (itemId == R.id.nav_map) {
                showToast(R.string.main_open_map);
                return true;
            } else if (itemId == R.id.nav_search) {
                openSearch();
                return true;
            }
            return false;
        });
    }

    private void setupActivityResultLauncher() {
        scannerLauncher = registerForActivityResult(
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

    private void observeViewModel() {
        viewModel.getProduct().observe(this, product -> {
            if (product != null) {
                navigateToProductDetailIfValid(product.getBarcode());
            }
        });

        viewModel.getRecentScans().observe(this, scans -> {
            // TODO: Update UI with recent scans
        });

        viewModel.getFavorites().observe(this, favorites -> {
            // TODO: Update UI with favorites
        });

        viewModel.getIsLoading().observe(this, isLoading -> {
            // TODO: Show/hide loading indicator
        });

        viewModel.getError().observe(this, error -> {
            if (error != null && !error.isEmpty()) {
                Toast.makeText(this, error, Toast.LENGTH_LONG).show();
            }
        });
    }

    private void openScanner() {
        try {
            Intent intent = new Intent(MainActivity.this, ScannerActivity.class);
            if (intent.resolveActivity(getPackageManager()) != null) {
                scannerLauncher.launch(intent);
            } else {
                showToast(R.string.main_error_open_scanner);
                Log.e(TAG, "ScannerActivity not found");
            }
        } catch (Exception e) {
            Log.e(TAG, "Error opening scanner", e);
            showToast(R.string.main_error_open_scanner_failed);
        }
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
