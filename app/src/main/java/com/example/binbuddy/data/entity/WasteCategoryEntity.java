package com.example.binbuddy.data.entity;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.util.UUID;

@Entity(tableName = "waste_categories")
public class WasteCategoryEntity {
    @PrimaryKey
    @NonNull
    @ColumnInfo(name = "id")
    public String id;

    @ColumnInfo(name = "name_de")
    public String nameDe;

    @ColumnInfo(name = "name_en")
    public String nameEn;

    @ColumnInfo(name = "description_de")
    public String descriptionDe;

    @ColumnInfo(name = "description_en")
    public String descriptionEn;

    @ColumnInfo(name = "icon_name")
    public String iconName;

    @ColumnInfo(name = "color_hex")
    public String colorHex;

    @ColumnInfo(name = "sort_order")
    public Integer sortOrder;

    public WasteCategoryEntity() {
        this.id = UUID.randomUUID().toString();
    }
}
