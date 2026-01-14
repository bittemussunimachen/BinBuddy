package com.example.binbuddy.ui;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import androidx.lifecycle.ViewModelProvider;

import com.example.binbuddy.databinding.ActivityWasteCategoryDetailBinding;
import com.example.binbuddy.domain.model.WasteCategory;
import com.example.binbuddy.ui.viewmodel.BinGuideViewModel;

import java.util.Locale;

public class WasteCategoryDetailActivity extends AppCompatActivity {
    private ActivityWasteCategoryDetailBinding binding;
    private BinGuideViewModel viewModel;
    private String categoryId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityWasteCategoryDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        categoryId = getIntent().getStringExtra("category_id");
        if (categoryId == null) {
            finish();
            return;
        }

        viewModel = new ViewModelProvider(this).get(BinGuideViewModel.class);
        setupObservers();
        setupClickListeners();

        viewModel.loadCategories();
        if (categoryId != null) {
            viewModel.selectCategory(categoryId);
        }
    }

    private void setupObservers() {
        viewModel.getWasteCategories().observe(this, categories -> {
            if (categories != null && categoryId != null) {
                for (WasteCategory category : categories) {
                    if (category.getId().equals(categoryId)) {
                        displayCategory(category);
                        break;
                    }
                }
            }
        });

        viewModel.getSelectedCategory().observe(this, category -> {
            if (category != null) {
                displayCategory(category);
            }
        });
    }

    private void setupClickListeners() {
        setSupportActionBar(binding.toolbar);
        binding.toolbar.setNavigationOnClickListener(v -> finish());
    }

    private void displayCategory(WasteCategory category) {
        String languageCode = Locale.getDefault().getLanguage();
        
        binding.textViewCategoryName.setText(category.getName(languageCode));
        binding.textViewCategoryDescription.setText(category.getDescription(languageCode));

        // Set color theme
        try {
            int color = Color.parseColor(category.getColorHex());
            binding.viewColorIndicator.setBackgroundColor(color);
            binding.cardViewHeader.setCardBackgroundColor(color);
        } catch (IllegalArgumentException e) {
            binding.viewColorIndicator.setBackgroundColor(Color.GRAY);
        }

        binding.scrollViewContent.setVisibility(View.VISIBLE);
    }
}
