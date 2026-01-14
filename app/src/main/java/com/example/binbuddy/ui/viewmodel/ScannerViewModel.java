package com.example.binbuddy.ui.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

public class ScannerViewModel extends AndroidViewModel {
    private final MutableLiveData<Boolean> isScanning = new MutableLiveData<>(true);
    private final MutableLiveData<String> scanResult = new MutableLiveData<>();
    private final MutableLiveData<String> error = new MutableLiveData<>();

    public ScannerViewModel(@NonNull Application application) {
        super(application);
    }

    public LiveData<Boolean> getIsScanning() {
        return isScanning;
    }

    public LiveData<String> getScanResult() {
        return scanResult;
    }

    public LiveData<String> getError() {
        return error;
    }

    public void startScanning() {
        isScanning.setValue(true);
        scanResult.setValue(null);
    }

    public void stopScanning() {
        isScanning.setValue(false);
    }

    public void processBarcode(String barcode) {
        if (barcode != null && !barcode.isEmpty()) {
            stopScanning();
            scanResult.setValue(barcode);
        } else {
            error.setValue("Ungültiger Barcode");
        }
    }

    public void clearError() {
        error.setValue(null);
    }
}
