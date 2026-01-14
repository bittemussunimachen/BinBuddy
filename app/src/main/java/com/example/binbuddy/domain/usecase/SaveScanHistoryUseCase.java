package com.example.binbuddy.domain.usecase;

import com.example.binbuddy.domain.model.ScanHistory;
import com.example.binbuddy.domain.repository.ScanHistoryRepository;

/**
 * Use case for saving scan history.
 * Encapsulates the business logic for recording product scans.
 */
public class SaveScanHistoryUseCase {

    private final ScanHistoryRepository scanHistoryRepository;

    public SaveScanHistoryUseCase(ScanHistoryRepository scanHistoryRepository) {
        this.scanHistoryRepository = scanHistoryRepository;
    }

    /**
     * Execute the use case to save a scan to history.
     * 
     * @param scan ScanHistory entry to save
     */
    public void execute(ScanHistory scan) {
        if (scan == null) {
            android.util.Log.w("SaveScanHistoryUseCase", "Attempted to save null scan");
            return;
        }

        if (scan.getBarcode() == null || scan.getBarcode().trim().isEmpty()) {
            android.util.Log.w("SaveScanHistoryUseCase", "Attempted to save scan with empty barcode");
            return;
        }

        // Save the scan via repository
        scanHistoryRepository.saveScan(scan);
    }

    /**
     * Execute the use case to save a scan with barcode and product.
     * Convenience method that creates ScanHistory internally.
     * 
     * @param barcode Product barcode
     * @param product Product (can be null)
     * @param location Location string (can be null)
     */
    public void execute(String barcode, com.example.binbuddy.domain.model.Product product, String location) {
        ScanHistory scan = new ScanHistory.Builder()
                .setBarcode(barcode)
                .setProduct(product)
                .setTimestamp(System.currentTimeMillis())
                .setLocation(location)
                .build();
        execute(scan);
    }
}
