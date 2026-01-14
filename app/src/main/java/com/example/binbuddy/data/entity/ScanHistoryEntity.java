package com.example.binbuddy.data.entity;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(
    tableName = "scan_history",
    foreignKeys = {
        @ForeignKey(
            entity = ProductEntity.class,
            parentColumns = "id",
            childColumns = "product_id",
            onDelete = ForeignKey.SET_NULL
        )
    },
    indices = {@Index(value = {"product_id"}), @Index(value = {"timestamp"})}
)
public class ScanHistoryEntity {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    public Long id;

    @ColumnInfo(name = "barcode")
    public String barcode;

    @ColumnInfo(name = "product_id")
    public String productId;

    @ColumnInfo(name = "timestamp")
    public Long timestamp;

    @ColumnInfo(name = "location")
    public String location;

    public ScanHistoryEntity() {
        this.timestamp = System.currentTimeMillis();
    }
}
