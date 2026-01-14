package com.example.binbuddy.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.binbuddy.R;
import com.example.binbuddy.databinding.ActivityProductDetailBinding;
import com.example.binbuddy.domain.model.Product;
import com.example.binbuddy.domain.model.WasteCategory;

public class ProductDetailActivity extends AppCompatActivity {

    public static final String EXTRA_BARCODE = "barcode";
    public static final String EXTRA_PRODUCT_NAME = "product_name";

    private ActivityProductDetailBinding binding;
    private String barcode;
    private Product product;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityProductDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        barcode = getIntent().getStringExtra(EXTRA_BARCODE);
        String productName = getIntent().getStringExtra(EXTRA_PRODUCT_NAME);

        setupClickListeners();
        displayProductInfo(productName);
    }

    private void setupClickListeners() {
        binding.btnBack.setOnClickListener(v -> finish());

        binding.btnAddFavorite.setOnClickListener(v -> {
            Toast.makeText(this, getString(R.string.add_to_favorites), Toast.LENGTH_SHORT).show();
            // TODO: Implement add to favorites functionality
        });

        binding.btnShare.setOnClickListener(v -> {
            shareProduct();
        });
    }

    private void displayProductInfo(String productName) {
        if (productName != null && !productName.isEmpty()) {
            binding.tvProductName.setText(productName);
        } else {
            binding.tvProductName.setText(getString(R.string.no_product_data));
        }

        // TODO: Load full product details from API/Repository
        // For now, display basic info from intent
        if (barcode != null) {
            binding.tvBarcode.setText(barcode);
        }

        // TODO: Determine waste category and Pfand info
        // This will be implemented when Repository/UseCase layers are ready
        displayWasteCategory(null);
        displayPfandInfo(null);
    }

    private void displayWasteCategory(WasteCategory category) {
        if (category != null) {
            binding.cardWasteCategory.setVisibility(View.VISIBLE);
            binding.tvWasteCategoryName.setText(category.getNameDe());
            binding.tvWasteCategoryDescription.setText(category.getDescriptionDe());
            // TODO: Set icon and color based on category
        } else {
            binding.cardWasteCategory.setVisibility(View.GONE);
        }
    }

    private void displayPfandInfo(String pfandInfo) {
        if (pfandInfo != null && !pfandInfo.isEmpty()) {
            binding.cardPfand.setVisibility(View.VISIBLE);
            binding.tvPfandAmount.setText(pfandInfo);
        } else {
            binding.cardPfand.setVisibility(View.GONE);
        }
    }

    private void shareProduct() {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        String shareText = getString(R.string.product_detail_title);
        if (product != null && product.getName() != null) {
            shareText += ": " + product.getName();
        }
        shareIntent.putExtra(Intent.EXTRA_TEXT, shareText);
        startActivity(Intent.createChooser(shareIntent, getString(R.string.share)));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding = null;
    }
}
