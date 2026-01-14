package com.example.binbuddy.data.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.example.binbuddy.data.entity.WasteCategoryEntity;

import java.util.List;

@Dao
public interface WasteCategoryDao {
    @Query("SELECT * FROM waste_categories ORDER BY sort_order ASC")
    List<WasteCategoryEntity> getAllCategories();

    @Query("SELECT * FROM waste_categories WHERE id = :id LIMIT 1")
    WasteCategoryEntity getCategory(String id);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertCategory(WasteCategoryEntity category);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertCategories(List<WasteCategoryEntity> categories);
}
