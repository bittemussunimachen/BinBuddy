package com.example.binbuddy.data.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.example.binbuddy.data.entity.FavoriteProductEntity;

import java.util.List;

@Dao
public interface FavoriteProductDao {
    @Query("SELECT * FROM favorite_products ORDER BY timestamp DESC")
    List<FavoriteProductEntity> getFavorites();

    @Query("SELECT * FROM favorite_products WHERE product_id = :productId LIMIT 1")
    FavoriteProductEntity getFavoriteByProductId(String productId);

    @Query("SELECT COUNT(*) > 0 FROM favorite_products WHERE product_id = :productId")
    boolean isFavorite(String productId);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void addFavorite(FavoriteProductEntity favorite);

    @Delete
    void removeFavorite(FavoriteProductEntity favorite);

    @Query("DELETE FROM favorite_products WHERE product_id = :productId")
    void removeFavoriteByProductId(String productId);
}
