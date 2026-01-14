package com.example.binbuddy.ui.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.binbuddy.domain.model.Product;

import java.util.ArrayList;
import java.util.List;

public class ScanHistoryViewModel extends AndroidViewModel {
    private final MutableLiveData<List<Product>> scanHistory = new MutableLiveData<>(new ArrayList<>());
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>(false);

    public ScanHistoryViewModel(@NonNull Application application) {
        super(application);
    }

    public LiveData<List<Product>> getScanHistory() {
        return scanHistory;
    }

    public LiveData<Boolean> getIsLoading() {
        return isLoading;
    }

    public void loadHistory() {
        isLoading.setValue(true);
        // TODO: Load from database when repository is implemented
        scanHistory.setValue(new ArrayList<>());
        isLoading.setValue(false);
    }

    public void deleteScan(long id) {
        // TODO: Delete from database when repository is implemented
        List<Product> current = scanHistory.getValue();
        if (current != null) {
            scanHistory.setValue(new ArrayList<>(current));
        }
    }
}
