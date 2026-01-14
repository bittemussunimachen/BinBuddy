package com.example.binbuddy.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.binbuddy.databinding.ActivityBinGuideBinding;
import com.example.binbuddy.domain.model.WasteCategory;
import com.example.binbuddy.ui.viewmodel.BinGuideViewModel;

import java.util.ArrayList;
import java.util.List;

public class BinGuideActivity extends AppCompatActivity {
    private ActivityBinGuideBinding binding;
    private BinGuideViewModel viewModel;
    private WasteCategoryAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityBinGuideBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        viewModel = new ViewModelProvider(this).get(BinGuideViewModel.class);

        setupRecyclerView();
        setupObservers();
        setupClickListeners();
    }

    private void setupRecyclerView() {
        adapter = new WasteCategoryAdapter(new ArrayList<>(), category -> {
            viewModel.selectCategory(category.getId());
            // Show category details (could navigate to detail screen or show bottom sheet)
        });
        binding.recyclerViewCategories.setLayoutManager(new LinearLayoutManager(this));
        binding.recyclerViewCategories.setAdapter(adapter);
    }

    private void setupObservers() {
        viewModel.getWasteCategories().observe(this, categories -> {
            if (categories != null && !categories.isEmpty()) {
                adapter.updateCategories(categories);
                binding.recyclerViewCategories.setVisibility(View.VISIBLE);
                binding.textViewEmpty.setVisibility(View.GONE);
            } else {
                binding.recyclerViewCategories.setVisibility(View.GONE);
                binding.textViewEmpty.setVisibility(View.VISIBLE);
            }
        });

        viewModel.getIsLoading().observe(this, isLoading -> {
            binding.progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
        });

        viewModel.getError().observe(this, error -> {
            if (error != null && !error.isEmpty()) {
                binding.textViewError.setText(error);
                binding.textViewError.setVisibility(View.VISIBLE);
            } else {
                binding.textViewError.setVisibility(View.GONE);
            }
        });

        viewModel.getSelectedCategory().observe(this, category -> {
            if (category != null) {
                // Show category details - could be a bottom sheet or detail activity
                showCategoryDetails(category);
            }
        });
    }

    private void setupClickListeners() {
        setSupportActionBar(binding.toolbar);
        binding.toolbar.setNavigationOnClickListener(v -> finish());
    }

    private void showCategoryDetails(WasteCategory category) {
        // For now, show details in a simple way
        // Could be enhanced with a bottom sheet or detail activity
        Intent intent = new Intent(this, WasteCategoryDetailActivity.class);
        intent.putExtra("category_id", category.getId());
        startActivity(intent);
    }
}
