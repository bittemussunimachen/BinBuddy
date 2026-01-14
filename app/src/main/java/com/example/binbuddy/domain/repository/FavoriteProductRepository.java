package com.example.binbuddy.domain.repository;

import com.example.binbuddy.domain.model.Product;

import java.util.List;

import kotlinx.coroutines.flow.Flow;

/**
 * Repository interface for favorite product operations.
 */
public interface FavoriteProductRepository {

    /**
     * Get all favorite products.
     * 
     * @return Flow emitting list of favorite products
     */
    Flow<List<Product>> getFavorites();

    /**
     * Check if a product is favorited.
     * 
     * @param productId Product ID
     * @return Flow emitting true if favorited, false otherwise
     */
    Flow<Boolean> isFavorite(String productId);

    /**
     * Add a product to favorites.
     * 
     * @param productId Product ID to add
     */
    void addFavorite(String productId);

    /**
     * Remove a product from favorites.
     * 
     * @param productId Product ID to remove
     */
    void removeFavorite(String productId);
}
