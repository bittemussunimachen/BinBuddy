package com.example.binbuddy.domain.repository;

import com.example.binbuddy.domain.model.Product;
import com.example.binbuddy.domain.model.Result;

import java.util.List;

import kotlinx.coroutines.flow.Flow;

/**
 * Repository interface for product-related operations.
 * Provides a clean abstraction for data access with proper error handling.
 */
public interface ProductRepository {

    /**
     * Get a product by barcode.
     * Checks cache → database → API → saves to database.
     * Supports offline mode with cached data fallback.
     * 
     * @param barcode Product barcode
     * @return Flow emitting Result<Product> (success with data, or error)
     */
    Flow<Result<Product>> getProduct(String barcode);

    /**
     * Search for products by query string.
     * Supports offline mode with cached search results.
     * 
     * @param query Search query
     * @param germanyOnly If true, filter results to Germany only
     * @return Flow emitting Result<List<Product>> (success with data, or error)
     */
    Flow<Result<List<Product>>> searchProducts(String query, boolean germanyOnly);

    /**
     * Save a product to the database cache.
     * 
     * @param product Product to save
     */
    void saveProduct(Product product);

    /**
     * Get products by waste category.
     * 
     * @param wasteCategoryId Waste category ID
     * @return Flow emitting Result<List<Product>> (success with data, or error)
     */
    Flow<Result<List<Product>>> getProductsByWasteCategory(String wasteCategoryId);
}
