package com.example.binbuddy.data.entity;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;

import java.util.List;
import java.util.UUID;

@Entity(
    tableName = "products",
    indices = {@Index(value = {"barcode"}, unique = true)}
)
public class ProductEntity {
    @PrimaryKey
    @ColumnInfo(name = "id")
    public String id;

    @ColumnInfo(name = "barcode")
    public String barcode;

    @ColumnInfo(name = "name")
    public String name;

    @ColumnInfo(name = "brand")
    public String brand;

    @ColumnInfo(name = "categories")
    public List<String> categories;

    @ColumnInfo(name = "packaging")
    public String packaging;

    @ColumnInfo(name = "quantity")
    public String quantity;

    @ColumnInfo(name = "waste_category_id")
    public String wasteCategoryId;

    @ColumnInfo(name = "image_url")
    public String imageUrl;

    @ColumnInfo(name = "generic_name")
    public String genericName;

    @ColumnInfo(name = "labels")
    public String labels;

    @ColumnInfo(name = "ingredients")
    public List<String> ingredients;

    @ColumnInfo(name = "created_at")
    public Long createdAt;

    @ColumnInfo(name = "updated_at")
    public Long updatedAt;

    public ProductEntity() {
        this.id = UUID.randomUUID().toString();
        this.createdAt = System.currentTimeMillis();
        this.updatedAt = System.currentTimeMillis();
    }
}
