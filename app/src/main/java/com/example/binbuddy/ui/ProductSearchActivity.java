package com.example.binbuddy.ui;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.binbuddy.R;
import com.example.binbuddy.databinding.ActivitySearchBinding;
import com.example.binbuddy.domain.model.Product;
import com.example.binbuddy.ui.viewmodel.ProductSearchViewModel;

public class ProductSearchActivity extends AppCompatActivity {

    private ActivitySearchBinding binding;
    private ProductSearchViewModel viewModel;
    private ProductAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySearchBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setupViewModel();
        setupRecyclerView();
        setupClickListeners();
        observeViewModel();
    }

    private void setupViewModel() {
        viewModel = new ViewModelProvider(this).get(ProductSearchViewModel.class);
    }

    private void setupRecyclerView() {
        adapter = new ProductAdapter(new java.util.ArrayList<>());
        adapter.setOnItemClickListener(product -> {
            navigateToProductDetailIfValid(product != null ? product.getBarcode() : null);
        });
        
        binding.rvProducts.setLayoutManager(new LinearLayoutManager(this));
        binding.rvProducts.setAdapter(adapter);
    }

    private void setupClickListeners() {
        binding.btnBack.setOnClickListener(v -> finish());

        binding.btnSearch.setOnClickListener(v -> startSearch());

        binding.etSearch.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                startSearch();
                return true;
            }
            return false;
        });
    }

    private void observeViewModel() {
        viewModel.getSearchResults().observe(this, products -> {
            if (products != null) {
                adapter.updateData(products);
                binding.tvEmpty.setVisibility(products.isEmpty() ? View.VISIBLE : View.GONE);
            }
        });

        viewModel.getIsLoading().observe(this, isLoading -> {
            binding.progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
        });

        viewModel.getError().observe(this, error -> {
            if (error != null && !error.isEmpty()) {
                Toast.makeText(this, error, Toast.LENGTH_LONG).show();
            }
        });
    }

    private void startSearch() {
        String term = binding.etSearch.getText() != null
                ? binding.etSearch.getText().toString().trim()
                : "";
        if (TextUtils.isEmpty(term)) {
            Toast.makeText(this, getString(R.string.search_enter_term), Toast.LENGTH_SHORT).show();
            return;
        }
        boolean germanyOnly = binding.checkGermany.isChecked();
        viewModel.searchProducts(term, germanyOnly);
    }

    private void navigateToProductDetailIfValid(String barcode) {
        if (TextUtils.isEmpty(barcode)) {
            return;
        }
        navigateToProductDetail(barcode.trim());
    }

    private void navigateToProductDetail(String barcode) {
        Intent intent = new Intent(this, ProductDetailActivity.class);
        intent.putExtra(ProductDetailActivity.EXTRA_BARCODE, barcode);
        startActivity(intent);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding = null;
    }
}
