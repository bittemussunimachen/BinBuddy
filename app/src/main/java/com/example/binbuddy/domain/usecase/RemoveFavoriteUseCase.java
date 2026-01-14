package com.example.binbuddy.domain.usecase;

import com.example.binbuddy.domain.repository.FavoriteProductRepository;

/**
 * Use case for removing a product from favorites.
 * Encapsulates the business logic for unfavoriting products.
 */
public class RemoveFavoriteUseCase {

    private final FavoriteProductRepository favoriteProductRepository;

    public RemoveFavoriteUseCase(FavoriteProductRepository favoriteProductRepository) {
        this.favoriteProductRepository = favoriteProductRepository;
    }

    /**
     * Execute the use case to remove a product from favorites.
     * 
     * @param productId Product ID to remove from favorites
     */
    public void execute(String productId) {
        if (productId == null || productId.trim().isEmpty()) {
            android.util.Log.w("RemoveFavoriteUseCase", "Attempted to remove favorite with empty productId");
            return;
        }

        // Remove from favorites via repository
        favoriteProductRepository.removeFavorite(productId);
    }
}
