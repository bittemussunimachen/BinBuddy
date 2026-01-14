package com.example.binbuddy.domain.usecase;

import com.example.binbuddy.domain.repository.FavoriteProductRepository;

/**
 * Use case for adding a product to favorites.
 * Encapsulates the business logic for favoriting products.
 */
public class AddFavoriteUseCase {

    private final FavoriteProductRepository favoriteProductRepository;

    public AddFavoriteUseCase(FavoriteProductRepository favoriteProductRepository) {
        this.favoriteProductRepository = favoriteProductRepository;
    }

    /**
     * Execute the use case to add a product to favorites.
     * 
     * @param productId Product ID to add to favorites
     */
    public void execute(String productId) {
        if (productId == null || productId.trim().isEmpty()) {
            android.util.Log.w("AddFavoriteUseCase", "Attempted to add favorite with empty productId");
            return;
        }

        // Add to favorites via repository
        favoriteProductRepository.addFavorite(productId);
    }
}
