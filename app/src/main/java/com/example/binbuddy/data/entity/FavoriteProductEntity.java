package com.example.binbuddy.data.entity;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(
    tableName = "favorite_products",
    foreignKeys = {
        @ForeignKey(
            entity = ProductEntity.class,
            parentColumns = "id",
            childColumns = "product_id",
            onDelete = ForeignKey.CASCADE
        )
    },
    indices = {@Index(value = {"product_id"}, unique = true)}
)
public class FavoriteProductEntity {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    public Long id;

    @ColumnInfo(name = "product_id")
    public String productId;

    @ColumnInfo(name = "timestamp")
    public Long timestamp;

    public FavoriteProductEntity() {
        this.timestamp = System.currentTimeMillis();
    }
}
