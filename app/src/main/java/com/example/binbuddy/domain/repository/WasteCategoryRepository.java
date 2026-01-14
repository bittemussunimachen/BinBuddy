package com.example.binbuddy.domain.repository;

import com.example.binbuddy.domain.model.WasteCategory;

import java.util.List;

import kotlinx.coroutines.flow.Flow;

/**
 * Repository interface for waste category operations.
 */
public interface WasteCategoryRepository {

    /**
     * Get all waste categories.
     * 
     * @return Flow emitting list of all waste categories
     */
    Flow<List<WasteCategory>> getAllCategories();

    /**
     * Get a specific waste category by ID.
     * 
     * @param id Waste category ID
     * @return Flow emitting WasteCategory or null if not found
     */
    Flow<WasteCategory> getCategory(String id);
}
