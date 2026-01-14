package com.example.binbuddy.ui.viewmodel;

import android.app.Application;
import android.location.Location;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.binbuddy.domain.model.PfandInfo;
import com.example.binbuddy.domain.service.PfandService;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnSuccessListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PfandInfoViewModel extends AndroidViewModel {
    private final PfandService pfandService;
    private final FusedLocationProviderClient locationClient;
    
    private final MutableLiveData<List<String>> returnLocations = new MutableLiveData<>();
    private final MutableLiveData<Location> currentLocation = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>(false);
    private final MutableLiveData<String> error = new MutableLiveData<>();

    public PfandInfoViewModel(@NonNull Application application) {
        super(application);
        this.pfandService = new PfandService();
        this.locationClient = LocationServices.getFusedLocationProviderClient(application);
        loadReturnLocations();
    }

    public LiveData<List<String>> getReturnLocations() {
        return returnLocations;
    }

    public LiveData<Location> getCurrentLocation() {
        return currentLocation;
    }

    public LiveData<Boolean> getIsLoading() {
        return isLoading;
    }

    public LiveData<String> getError() {
        return error;
    }

    public PfandService getPfandService() {
        return pfandService;
    }

    public void loadReturnLocations() {
        isLoading.setValue(true);
        error.setValue(null);

        // TODO: Load from location service when implemented
        // For now, return sample locations
        List<String> locations = new ArrayList<>();
        locations.add("Supermarkt XYZ - Hauptstraße 123");
        locations.add("Getränkemarkt ABC - Bahnhofstraße 45");
        locations.add("Rewe - Marktplatz 1");
        locations.add("Edeka - Lindenstraße 78");

        returnLocations.setValue(locations);
        isLoading.setValue(false);
    }

    public void requestLocation() {
        try {
            locationClient.getLastLocation()
                    .addOnSuccessListener(new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location location) {
                            if (location != null) {
                                currentLocation.setValue(location);
                                // Load nearby return locations based on location
                                loadNearbyReturnLocations(location);
                            } else {
                                error.setValue("Standort nicht verfügbar");
                            }
                        }
                    });
        } catch (SecurityException e) {
            error.setValue("Standortberechtigung erforderlich");
        }
    }

    private void loadNearbyReturnLocations(Location location) {
        // TODO: Implement API call to get nearby return locations
        // For now, use sample data
        loadReturnLocations();
    }

    public PfandInfo checkPfandForProduct(com.example.binbuddy.domain.model.Product product) {
        return pfandService.checkPfand(product);
    }
}
