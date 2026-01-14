package com.example.binbuddy.ui;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import androidx.lifecycle.ViewModelProvider;

import com.example.binbuddy.R;
import com.example.binbuddy.databinding.ActivityPfandInfoBinding;
import com.example.binbuddy.ui.viewmodel.PfandInfoViewModel;

import java.util.ArrayList;

public class PfandInfoActivity extends AppCompatActivity {
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1001;
    
    private ActivityPfandInfoBinding binding;
    private PfandInfoViewModel viewModel;
    private ReturnLocationAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPfandInfoBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        viewModel = new ViewModelProvider(this).get(PfandInfoViewModel.class);

        setupRecyclerView();
        setupObservers();
        setupClickListeners();
        checkLocationPermission();
    }

    private void setupRecyclerView() {
        adapter = new ReturnLocationAdapter(new ArrayList<>());
        binding.recyclerViewReturnLocations.setLayoutManager(new LinearLayoutManager(this));
        binding.recyclerViewReturnLocations.setAdapter(adapter);
    }

    private void setupObservers() {
        viewModel.getReturnLocations().observe(this, locations -> {
            if (locations != null && !locations.isEmpty()) {
                adapter.updateLocations(locations);
                binding.recyclerViewReturnLocations.setVisibility(View.VISIBLE);
                binding.textViewNoLocations.setVisibility(View.GONE);
            } else {
                binding.recyclerViewReturnLocations.setVisibility(View.GONE);
                binding.textViewNoLocations.setVisibility(View.VISIBLE);
            }
        });

        viewModel.getIsLoading().observe(this, isLoading -> {
            binding.progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
        });

        viewModel.getError().observe(this, error -> {
            if (error != null && !error.isEmpty()) {
                binding.textViewError.setText(error);
                binding.textViewError.setVisibility(View.VISIBLE);
            } else {
                binding.textViewError.setVisibility(View.GONE);
            }
        });
    }

    private void setupClickListeners() {
        binding.imageViewBack.setOnClickListener(v -> finish());
        
        binding.buttonEnableLocation.setOnClickListener(v -> {
            requestLocationPermission();
        });
    }

    private void checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) 
                == PackageManager.PERMISSION_GRANTED) {
            viewModel.requestLocation();
            binding.buttonEnableLocation.setVisibility(View.GONE);
        } else {
            binding.buttonEnableLocation.setVisibility(View.VISIBLE);
        }
    }

    private void requestLocationPermission() {
        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                LOCATION_PERMISSION_REQUEST_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                viewModel.requestLocation();
                binding.buttonEnableLocation.setVisibility(View.GONE);
            } else {
                binding.textViewError.setText(getString(R.string.location_permission_required));
                binding.textViewError.setVisibility(View.VISIBLE);
            }
        }
    }
}
