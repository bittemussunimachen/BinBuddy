package com.example.binbuddy.data.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.example.binbuddy.data.entity.ScanHistoryEntity;

import java.util.List;

@Dao
public interface ScanHistoryDao {
    @Query("SELECT * FROM scan_history ORDER BY timestamp DESC")
    List<ScanHistoryEntity> getScanHistory();

    @Query("SELECT * FROM scan_history ORDER BY timestamp DESC LIMIT :limit")
    List<ScanHistoryEntity> getRecentScans(int limit);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void saveScan(ScanHistoryEntity scan);

    @Delete
    void deleteScan(ScanHistoryEntity scan);

    @Query("DELETE FROM scan_history WHERE id = :id")
    void deleteScanById(Long id);
}
