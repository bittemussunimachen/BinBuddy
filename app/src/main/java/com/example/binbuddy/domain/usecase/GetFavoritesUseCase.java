package com.example.binbuddy.domain.usecase;

import com.example.binbuddy.domain.model.Product;
import com.example.binbuddy.domain.repository.FavoriteProductRepository;

import java.util.List;

import kotlinx.coroutines.flow.Flow;

/**
 * Use case for getting favorite products.
 * Encapsulates the business logic for retrieving favorites.
 */
public class GetFavoritesUseCase {

    private final FavoriteProductRepository favoriteProductRepository;

    public GetFavoritesUseCase(FavoriteProductRepository favoriteProductRepository) {
        this.favoriteProductRepository = favoriteProductRepository;
    }

    /**
     * Execute the use case to get all favorite products.
     * 
     * @return Flow emitting list of favorite products
     */
    public Flow<List<Product>> execute() {
        // TODO: Implement once Flow is properly set up
        // This will:
        // 1. Call favoriteProductRepository.getFavorites()
        // 2. Optionally sort by timestamp (newest first)
        // 3. Return Flow<List<Product>>
        
        throw new UnsupportedOperationException(
            "GetFavoritesUseCase.execute() not yet fully implemented. " +
            "Requires kotlinx-coroutines-core for Flow support."
        );
    }
}
