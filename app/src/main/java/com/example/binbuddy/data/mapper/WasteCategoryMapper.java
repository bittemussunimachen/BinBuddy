package com.example.binbuddy.data.mapper;

import com.example.binbuddy.data.entity.WasteCategoryEntity;
import com.example.binbuddy.domain.model.WasteCategory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Mapper class to convert between WasteCategory Entity and Domain model.
 */
public class WasteCategoryMapper {

    /**
     * Convert WasteCategoryEntity to WasteCategory domain model.
     * 
     * @param entity WasteCategoryEntity from database
     * @return WasteCategory domain model
     */
    public WasteCategory toDomain(WasteCategoryEntity entity) {
        if (entity == null) {
            return null;
        }
        return new WasteCategory.Builder()
                .setId(entity.id)
                .setNameDe(entity.nameDe)
                .setNameEn(entity.nameEn)
                .setDescriptionDe(entity.descriptionDe)
                .setDescriptionEn(entity.descriptionEn)
                .setIconName(entity.iconName)
                .setColorHex(entity.colorHex)
                .build();
    }

    /**
     * Convert WasteCategory domain model to WasteCategoryEntity.
     * 
     * @param wasteCategory WasteCategory domain model
     * @return WasteCategoryEntity for database storage
     */
    public WasteCategoryEntity toEntity(WasteCategory wasteCategory) {
        if (wasteCategory == null) {
            return null;
        }
        WasteCategoryEntity entity = new WasteCategoryEntity();
        entity.id = wasteCategory.getId();
        entity.nameDe = wasteCategory.getNameDe();
        entity.nameEn = wasteCategory.getNameEn();
        entity.descriptionDe = wasteCategory.getDescriptionDe();
        entity.descriptionEn = wasteCategory.getDescriptionEn();
        entity.iconName = wasteCategory.getIconName();
        entity.colorHex = wasteCategory.getColorHex();
        return entity;
    }

    /**
     * Convert list of WasteCategoryEntity to list of WasteCategory domain models.
     * 
     * @param entities List of WasteCategoryEntity
     * @return List of WasteCategory domain models
     */
    public List<WasteCategory> toDomainList(List<WasteCategoryEntity> entities) {
        if (entities == null || entities.isEmpty()) {
            return Collections.emptyList();
        }
        List<WasteCategory> categories = new ArrayList<>();
        for (WasteCategoryEntity entity : entities) {
            try {
                WasteCategory category = toDomain(entity);
                if (category != null) {
                    categories.add(category);
                }
            } catch (Exception e) {
                // Skip invalid categories
                android.util.Log.w("WasteCategoryMapper", "Failed to map waste category", e);
            }
        }
        return categories;
    }
}
