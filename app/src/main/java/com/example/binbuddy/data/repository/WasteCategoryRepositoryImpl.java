package com.example.binbuddy.data.repository;

import com.example.binbuddy.data.dao.WasteCategoryDao;
import com.example.binbuddy.data.entity.WasteCategoryEntity;
import com.example.binbuddy.data.mapper.WasteCategoryMapper;
import com.example.binbuddy.domain.model.WasteCategory;
import com.example.binbuddy.domain.repository.WasteCategoryRepository;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import kotlinx.coroutines.flow.Flow;
import kotlinx.coroutines.flow.MutableStateFlow;
import kotlinx.coroutines.flow.StateFlowKt;

/**
 * Implementation of WasteCategoryRepository.
 */
@Singleton
public class WasteCategoryRepositoryImpl implements WasteCategoryRepository {

    private final WasteCategoryDao wasteCategoryDao;
    private final WasteCategoryMapper mapper;

    @Inject
    public WasteCategoryRepositoryImpl(WasteCategoryDao wasteCategoryDao, WasteCategoryMapper mapper) {
        this.wasteCategoryDao = wasteCategoryDao;
        this.mapper = mapper;
    }

    @Override
    public Flow<List<WasteCategory>> getAllCategories() {
        // TODO: Convert Flow<List<WasteCategoryEntity>> to Flow<List<WasteCategory>>
        // For now, return empty flow
        List<WasteCategory> categories = new ArrayList<>();
        MutableStateFlow<List<WasteCategory>> flow = StateFlowKt.MutableStateFlow(categories);
        return flow;
    }

    @Override
    public Flow<WasteCategory> getCategory(String id) {
        // TODO: Implement proper Flow conversion
        MutableStateFlow<WasteCategory> flow = StateFlowKt.MutableStateFlow(null);
        return flow;
    }
}
