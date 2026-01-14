package com.example.binbuddy.data.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Transaction;
import androidx.room.Update;

import com.example.binbuddy.data.entity.ProductEntity;

import java.util.List;

@Dao
public interface ProductDao {
    @Query("SELECT * FROM products WHERE barcode = :barcode LIMIT 1")
    ProductEntity getProduct(String barcode);

    @Query("SELECT * FROM products WHERE name LIKE '%' || :query || '%' OR brand LIKE '%' || :query || '%'")
    List<ProductEntity> searchProducts(String query);

    @Query("SELECT * FROM products WHERE waste_category_id = :categoryId")
    List<ProductEntity> getProductsByWasteCategory(String categoryId);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertProduct(ProductEntity product);

    @Update
    void updateProduct(ProductEntity product);

    @Query("SELECT * FROM products ORDER BY updated_at DESC LIMIT :limit")
    List<ProductEntity> getRecentProducts(int limit);
}
