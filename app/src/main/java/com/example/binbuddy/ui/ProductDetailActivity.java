package com.example.binbuddy.ui;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;
import android.graphics.drawable.PictureDrawable;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.binbuddy.R;
import com.example.binbuddy.databinding.ActivityProductDetailBinding;
import com.example.binbuddy.domain.model.EnvironmentInfo;
import com.example.binbuddy.domain.model.PfandInfo;
import com.example.binbuddy.domain.model.Product;
import com.example.binbuddy.domain.model.WasteCategory;
import com.example.binbuddy.domain.service.PfandService;
import com.example.binbuddy.domain.service.WasteClassificationService;
import com.example.binbuddy.ui.viewmodel.ProductDetailViewModel;
import com.caverock.androidsvg.SVG;
import java.util.Locale;
import java.util.ArrayList;
import java.util.List;

public class ProductDetailActivity extends AppCompatActivity {

    public static final String EXTRA_BARCODE = "barcode";
    public static final String EXTRA_PRODUCT_NAME = "product_name";

    private ActivityProductDetailBinding binding;
    private ProductDetailViewModel viewModel;
    private WasteClassificationService wasteClassificationService;
    private PfandService pfandService;
    private String barcode;
    private Product product;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityProductDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        barcode = getIntent().getStringExtra(EXTRA_BARCODE);
        String productName = getIntent().getStringExtra(EXTRA_PRODUCT_NAME);

        wasteClassificationService = new WasteClassificationService();
        pfandService = new PfandService();
        viewModel = new ViewModelProvider(this).get(ProductDetailViewModel.class);

        setupToolbar();
        setupClickListeners();
        observeViewModel();
        displayProductInfo(productName);

        if (barcode != null && !barcode.trim().isEmpty()) {
            viewModel.loadProduct(barcode.trim());
        } else {
            showStatus(getString(R.string.error_invalid_input));
        }
    }

    private void setupClickListeners() {
        binding.btnAddFavorite.setOnClickListener(v ->
                Toast.makeText(this, getString(R.string.add_to_favorites), Toast.LENGTH_SHORT).show()
        );

        binding.btnShare.setOnClickListener(v -> shareProduct());
    }

    private void setupToolbar() {
        binding.toolbar.setNavigationOnClickListener(v -> finish());
    }

    private void displayProductInfo(String productName) {
        if (productName != null && !productName.isEmpty()) {
            binding.tvProductName.setText(productName);
        } else {
            binding.tvProductName.setText(getString(R.string.no_product_data));
        }

        if (barcode != null) {
            binding.tvBarcode.setText(barcode);
        }
    }

    private void observeViewModel() {
        viewModel.getProduct().observe(this, product -> {
            this.product = product;
            if (product != null) {
                renderProduct(product);
            }
        });

        viewModel.getIsLoading().observe(this, isLoading -> {
            boolean loading = Boolean.TRUE.equals(isLoading);
            binding.progressBar.setVisibility(loading ? View.VISIBLE : View.GONE);
        });

        viewModel.getError().observe(this, error -> {
            if (error != null && !error.isEmpty()) {
                showStatus(error);
            } else {
                binding.tvStatus.setVisibility(View.GONE);
            }
        });
    }

    private void renderProduct(Product product) {
        binding.tvStatus.setVisibility(View.GONE);
        binding.tvProductName.setText(product.getName() != null ? product.getName() : getString(R.string.no_product_data));
        binding.tvBrand.setText(product.getBrand() != null ? product.getBrand() : getString(R.string.no_brand));
        binding.tvBarcode.setText(product.getBarcode() != null ? product.getBarcode() : "");
        binding.tvCategories.setText(formatList(product.getCategories(), getString(R.string.no_categories)));
        binding.tvPackaging.setText(product.getPackaging() != null && !product.getPackaging().isEmpty()
                ? product.getPackaging()
                : getString(R.string.packaging_unknown));
        binding.tvIngredients.setText(formatList(product.getIngredients(), getString(R.string.no_product_data)));

        displayGreenScore(product);
        displayEnvironment(product.getEnvironmentInfo());

        WasteCategory category = wasteClassificationService.determineWasteCategory(product);
        displayWasteCategory(category);

        PfandInfo pfandInfo = pfandService.checkPfand(product);
        displayPfandInfo(pfandInfo);
    }

    private void displayGreenScore(Product product) {
        if (product == null) {
            binding.greenScoreRow.setVisibility(View.GONE);
            return;
        }
        String grade = product.getEcoscoreGrade();
        Integer score = product.getEcoscoreScore();

        // Fallback to environment info if direct fields are missing (e.g., when coming from search result)
        if ((grade == null || grade.trim().isEmpty()) && product.getEnvironmentInfo() != null) {
            grade = product.getEnvironmentInfo().getEcoScoreGrade();
        }
        if (score == null && product.getEnvironmentInfo() != null) {
            score = product.getEnvironmentInfo().getEcoScoreScore();
        }

        if (grade == null || grade.trim().isEmpty()) {
            binding.greenScoreRow.setVisibility(View.GONE);
            return;
        }

        String normalizedGrade = grade.trim().toUpperCase(Locale.getDefault());
        binding.greenScoreRow.setVisibility(View.VISIBLE);
        binding.tvGreenScoreGrade.setText(getString(R.string.green_score_grade_label, normalizedGrade));
        if (score != null) {
            binding.tvGreenScoreScore.setText(getString(R.string.green_score_score_label, score));
        } else {
            binding.tvGreenScoreScore.setText(getString(R.string.green_score_missing));
        }

        PictureDrawable drawable = loadGreenScoreDrawable(normalizedGrade);
        if (drawable != null) {
            binding.ivGreenScore.setVisibility(View.VISIBLE);
            binding.ivGreenScore.setImageDrawable(drawable);
            // Required for PictureDrawable to render properly
            binding.ivGreenScore.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        } else {
            binding.ivGreenScore.setVisibility(View.GONE);
        }
    }

    private void displayEnvironment(EnvironmentInfo info) {
        binding.cardEnvironment.setVisibility(View.VISIBLE);
        clearEnvironmentRows();

        if (info == null) {
            showEnvironmentFallback(getString(R.string.environment_info_missing));
            return;
        }

        boolean ecoVisible = bindEcoRow(info);
        boolean packagingVisible = bindPackagingRow(info);
        boolean palmVisible = bindPalmRow(info);
        boolean carbonVisible = bindCarbonRow(info);

        if (!ecoVisible && !packagingVisible && !palmVisible && !carbonVisible) {
            showEnvironmentFallback(getString(R.string.environment_info_missing));
        } else {
            binding.tvEnvironmentFallback.setVisibility(View.GONE);
        }
    }

    private void clearEnvironmentRows() {
        binding.rowEcoScore.setVisibility(View.GONE);
        binding.rowPackaging.setVisibility(View.GONE);
        binding.rowPalmOil.setVisibility(View.GONE);
        binding.rowCarbon.setVisibility(View.GONE);
        binding.tvEnvironmentFallback.setVisibility(View.GONE);

        binding.tvEcoScoreTitle.setText("");
        binding.tvEcoScoreValue.setText("");
        binding.tvPackagingTitle.setText("");
        binding.tvPackagingValue.setText("");
        binding.tvPalmTitle.setText("");
        binding.tvPalmValue.setText("");
        binding.tvCarbonTitle.setText("");
        binding.tvCarbonValue.setText("");
    }

    private boolean bindEcoRow(EnvironmentInfo info) {
        String grade = info.getEcoScoreGrade();
        Integer score = info.getEcoScoreScore();
        if (score != null && score < 0) {
            score = null;
        }
        if ((grade == null || grade.trim().isEmpty()) && score == null) {
            binding.rowEcoScore.setVisibility(View.GONE);
            return false;
        }

        String normalizedGrade = grade != null ? grade.trim().toUpperCase(Locale.getDefault()) : "";
        String description;
        if (!normalizedGrade.isEmpty() && score != null) {
            description = getString(R.string.environment_eco_score_value, normalizedGrade, score);
        } else if (!normalizedGrade.isEmpty()) {
            description = getString(R.string.environment_eco_score_grade_only, normalizedGrade);
        } else {
            description = getString(R.string.environment_eco_score_missing_short);
        }

        binding.rowEcoScore.setVisibility(View.VISIBLE);
        binding.iconEcoScore.setImageResource(IconRegistry.getIconRes(IconRegistry.ICON_ECO_SCORE));
        binding.tvEcoScoreTitle.setText(getString(R.string.environment_eco_score));
        binding.tvEcoScoreValue.setText(description);
        return true;
    }

    private boolean bindPackagingRow(EnvironmentInfo info) {
        List<String> details = new ArrayList<>();
        if (!info.getPackagingParts().isEmpty()) {
            details.add(getString(R.string.environment_packaging_parts, TextUtils.join(", ", info.getPackagingParts())));
        }
        if (!info.getPackagingMaterials().isEmpty()) {
            details.add(getString(R.string.environment_packaging_materials, TextUtils.join(", ", info.getPackagingMaterials())));
        }
        if (!TextUtils.isEmpty(info.getPackagingWarning())) {
            details.add(getString(R.string.environment_packaging_warning_prefix, info.getPackagingWarning()));
        }

        if (details.isEmpty()) {
            binding.rowPackaging.setVisibility(View.GONE);
            return false;
        }

        binding.rowPackaging.setVisibility(View.VISIBLE);
        binding.iconPackaging.setImageResource(IconRegistry.getIconRes(IconRegistry.ICON_PACKAGING));
        binding.tvPackagingTitle.setText(getString(R.string.environment_packaging));
        binding.tvPackagingValue.setText(TextUtils.join("\n", details));
        return true;
    }

    private boolean bindPalmRow(EnvironmentInfo info) {
        boolean hasPalm = info.isContainsPalmOil();
        boolean hasThreat = !TextUtils.isEmpty(info.getThreatenedSpeciesIngredient());

        if (!hasPalm && !hasThreat) {
            binding.rowPalmOil.setVisibility(View.GONE);
            return false;
        }

        List<String> descriptions = new ArrayList<>();
        if (hasPalm) {
            descriptions.add(getString(R.string.environment_palm_oil_present));
        }
        if (hasThreat) {
            descriptions.add(getString(R.string.environment_threatened_species_value, info.getThreatenedSpeciesIngredient()));
        }

        binding.rowPalmOil.setVisibility(View.VISIBLE);
        String iconKey = hasPalm ? IconRegistry.ICON_PALM_OIL : IconRegistry.ICON_WARNING;
        binding.iconPalmOil.setImageResource(IconRegistry.getIconRes(iconKey));
        binding.tvPalmTitle.setText(getString(R.string.environment_palm_oil_title));
        binding.tvPalmValue.setText(TextUtils.join("\n", descriptions));
        return true;
    }

    private boolean bindCarbonRow(EnvironmentInfo info) {
        Double co2 = info.getCo2Per100g();
        if (co2 == null) {
            binding.rowCarbon.setVisibility(View.GONE);
            return false;
        }

        binding.rowCarbon.setVisibility(View.VISIBLE);
        binding.iconCarbon.setImageResource(IconRegistry.getIconRes(IconRegistry.ICON_CARBON));
        binding.tvCarbonTitle.setText(getString(R.string.environment_carbon));
        binding.tvCarbonValue.setText(getString(R.string.environment_carbon_value, co2));
        return true;
    }

    private void showEnvironmentFallback(String message) {
        binding.tvEnvironmentFallback.setVisibility(View.VISIBLE);
        binding.tvEnvironmentFallback.setText(message);
    }

    private PictureDrawable loadGreenScoreDrawable(String grade) {
        String assetName = mapGradeToAsset(grade);
        try (java.io.InputStream inputStream = getAssets().open("greenscore/" + assetName)) {
            SVG svg = SVG.getFromInputStream(inputStream);
            return new PictureDrawable(svg.renderToPicture());
        } catch (Exception e) {
            Log.w("ProductDetailActivity", "GreenScore asset missing: " + assetName, e);
            return null;
        }
    }

    private String mapGradeToAsset(String grade) {
        String normalized = grade.toLowerCase(Locale.getDefault());
        switch (normalized) {
            case "a+":
            case "a_plus":
                return "greenscore_a_plus.svg";
            case "a":
                return "greenscore_a.svg";
            case "b":
                return "greenscore_b.svg";
            case "c":
                return "greenscore_c.svg";
            case "d":
                return "greenscore_d.svg";
            case "e":
                return "greenscore_e.svg";
            case "f":
                return "greenscore_f.svg";
            default:
                return "greenscore_unknown.svg";
        }
    }

    private void displayWasteCategory(WasteCategory category) {
        if (category != null) {
            binding.cardWasteCategory.setVisibility(View.VISIBLE);
            binding.tvWasteCategoryName.setText(category.getNameDe());
            binding.tvWasteCategoryDescription.setText(category.getDescriptionDe());
        } else {
            binding.cardWasteCategory.setVisibility(View.GONE);
        }
    }

    private void displayPfandInfo(PfandInfo info) {
        if (info != null && info.hasPfand()) {
            binding.cardPfand.setVisibility(View.VISIBLE);
            Double amount = info.getAmount();
            String pfandText = amount != null
                    ? getString(R.string.pfand_amount) + ": " + String.format(java.util.Locale.getDefault(), "€%.2f", amount)
                    : getString(R.string.pfand_info);
            binding.tvPfandAmount.setText(pfandText);
        } else {
            binding.cardPfand.setVisibility(View.GONE);
        }
    }

    private void shareProduct() {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        String shareText = getString(R.string.product_detail_title);
        if (product != null && product.getName() != null && !product.getName().isEmpty()) {
            shareText += ": " + product.getName();
        }
        if (product != null && product.getBarcode() != null && !product.getBarcode().isEmpty()) {
            shareText += " (" + product.getBarcode() + ")";
        }
        shareIntent.putExtra(Intent.EXTRA_TEXT, shareText);
        startActivity(Intent.createChooser(shareIntent, getString(R.string.share)));
    }

    private String formatList(java.util.List<String> values, String fallback) {
        if (values == null || values.isEmpty()) {
            return fallback;
        }
        return android.text.TextUtils.join(", ", values);
    }

    private void showStatus(String message) {
        binding.tvStatus.setVisibility(View.VISIBLE);
        binding.tvStatus.setText(message);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding = null;
    }
}
