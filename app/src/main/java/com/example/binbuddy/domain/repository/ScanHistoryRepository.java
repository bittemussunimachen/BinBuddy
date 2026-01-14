package com.example.binbuddy.domain.repository;

import com.example.binbuddy.domain.model.ScanHistory;

import java.util.List;

import kotlinx.coroutines.flow.Flow;

/**
 * Repository interface for scan history operations.
 */
public interface ScanHistoryRepository {

    /**
     * Get all scan history entries.
     * 
     * @return Flow emitting list of scan history entries
     */
    Flow<List<ScanHistory>> getScanHistory();

    /**
     * Get recent scan history entries.
     * 
     * @param limit Maximum number of entries to return
     * @return Flow emitting list of recent scan history entries
     */
    Flow<List<ScanHistory>> getRecentScans(int limit);

    /**
     * Save a scan to history.
     * 
     * @param scan ScanHistory entry to save
     */
    void saveScan(ScanHistory scan);

    /**
     * Delete a scan from history.
     * 
     * @param id Scan history entry ID
     */
    void deleteScan(long id);
}
