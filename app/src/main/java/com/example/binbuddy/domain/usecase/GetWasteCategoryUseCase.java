package com.example.binbuddy.domain.usecase;

import com.example.binbuddy.domain.model.Product;
import com.example.binbuddy.domain.model.WasteCategory;
import com.example.binbuddy.domain.repository.WasteCategoryRepository;
import com.example.binbuddy.domain.service.WasteClassificationService;

/**
 * Use case for determining waste category for a product.
 * Encapsulates the business logic for waste classification.
 */
public class GetWasteCategoryUseCase {

    private final WasteClassificationService wasteClassificationService;
    private final WasteCategoryRepository wasteCategoryRepository;

    public GetWasteCategoryUseCase(
            WasteClassificationService wasteClassificationService,
            WasteCategoryRepository wasteCategoryRepository) {
        this.wasteClassificationService = wasteClassificationService;
        this.wasteCategoryRepository = wasteCategoryRepository;
    }

    /**
     * Execute the use case to determine waste category for a product.
     * 
     * @param product Product to classify
     * @return WasteCategory for the product
     */
    public WasteCategory execute(Product product) {
        if (product == null) {
            android.util.Log.w("GetWasteCategoryUseCase", "Attempted to classify null product");
            return null;
        }

        // Use WasteClassificationService to determine category
        // This service contains the classification rules/logic
        return wasteClassificationService.determineWasteCategory(product);
    }
}
