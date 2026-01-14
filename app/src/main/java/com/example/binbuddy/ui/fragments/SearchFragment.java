package com.example.binbuddy.ui.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.binbuddy.R;
import com.example.binbuddy.databinding.FragmentSearchBinding;
import com.example.binbuddy.domain.model.Product;
import com.example.binbuddy.ui.ProductAdapter;
import com.example.binbuddy.ui.ProductDetailActivity;
import com.example.binbuddy.ui.viewmodel.ProductSearchViewModel;

import java.util.ArrayList;

public class SearchFragment extends Fragment {

    private FragmentSearchBinding binding;
    private ProductSearchViewModel viewModel;
    private ProductAdapter adapter;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentSearchBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setupViewModel();
        setupRecyclerView();
        setupClickListeners();
        observeViewModel();
    }

    private void setupViewModel() {
        viewModel = new ViewModelProvider(this).get(ProductSearchViewModel.class);
    }

    private void setupRecyclerView() {
        adapter = new ProductAdapter(new ArrayList<>());
        adapter.setOnItemClickListener(product -> {
            navigateToProductDetailIfValid(product);
        });

        binding.rvProducts.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.rvProducts.setAdapter(adapter);
    }

    private void setupClickListeners() {
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
        viewModel.getSearchResults().observe(getViewLifecycleOwner(), products -> {
            if (products != null) {
                adapter.updateData(products);
                binding.tvEmpty.setVisibility(products.isEmpty() ? View.VISIBLE : View.GONE);
            }
        });

        viewModel.getIsLoading().observe(getViewLifecycleOwner(), isLoading -> {
            binding.progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
        });

        viewModel.getError().observe(getViewLifecycleOwner(), error -> {
            if (error != null && !error.isEmpty()) {
                Toast.makeText(requireContext(), error, Toast.LENGTH_LONG).show();
            }
        });
    }

    private void startSearch() {
        String term = binding.etSearch.getText() != null
                ? binding.etSearch.getText().toString().trim()
                : "";
        if (TextUtils.isEmpty(term)) {
            Toast.makeText(requireContext(), getString(R.string.search_enter_term), Toast.LENGTH_SHORT).show();
            return;
        }
        boolean germanyOnly = binding.checkGermany.isChecked();
        binding.tvEmpty.setVisibility(View.GONE);
        viewModel.searchProducts(term, germanyOnly);
    }

    private void navigateToProductDetailIfValid(Product product) {
        String barcode = product != null ? product.getBarcode() : null;
        if (TextUtils.isEmpty(barcode)) {
            return;
        }
        String name = product != null ? product.getName() : null;
        navigateToProductDetail(barcode.trim(), name);
    }

    private void navigateToProductDetail(String barcode, String productName) {
        Intent intent = new Intent(requireContext(), ProductDetailActivity.class);
        intent.putExtra(ProductDetailActivity.EXTRA_BARCODE, barcode);
        if (!TextUtils.isEmpty(productName)) {
            intent.putExtra(ProductDetailActivity.EXTRA_PRODUCT_NAME, productName);
        }
        startActivity(intent);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
