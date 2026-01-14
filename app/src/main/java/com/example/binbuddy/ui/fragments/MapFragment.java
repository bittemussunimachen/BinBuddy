package com.example.binbuddy.ui.fragments;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.binbuddy.R;
import com.example.binbuddy.databinding.FragmentMapBinding;
import com.example.binbuddy.ui.PfandInfoActivity;
import com.example.binbuddy.ui.ReturnLocationAdapter;
import com.example.binbuddy.ui.viewmodel.PfandInfoViewModel;

import java.util.ArrayList;

public class MapFragment extends Fragment {

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1001;

    private FragmentMapBinding binding;
    private PfandInfoViewModel viewModel;
    private ReturnLocationAdapter adapter;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentMapBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel = new ViewModelProvider(this).get(PfandInfoViewModel.class);

        setupRecyclerView();
        setupObservers();
        setupClickListeners();
        checkLocationPermission();
    }

    private void setupRecyclerView() {
        adapter = new ReturnLocationAdapter(new ArrayList<>());
        binding.recyclerViewReturnLocations.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.recyclerViewReturnLocations.setAdapter(adapter);
    }

    private void setupObservers() {
        viewModel.getReturnLocations().observe(getViewLifecycleOwner(), locations -> {
            if (locations != null && !locations.isEmpty()) {
                adapter.updateLocations(locations);
                binding.recyclerViewReturnLocations.setVisibility(View.VISIBLE);
                binding.textViewNoLocations.setVisibility(View.GONE);
            } else {
                binding.recyclerViewReturnLocations.setVisibility(View.GONE);
                binding.textViewNoLocations.setVisibility(View.VISIBLE);
            }
        });

        viewModel.getIsLoading().observe(getViewLifecycleOwner(), isLoading -> {
            binding.progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
        });

        viewModel.getError().observe(getViewLifecycleOwner(), error -> {
            if (error != null && !error.isEmpty()) {
                binding.textViewError.setText(error);
                binding.textViewError.setVisibility(View.VISIBLE);
            } else {
                binding.textViewError.setVisibility(View.GONE);
            }
        });
    }

    private void setupClickListeners() {
        binding.buttonEnableLocation.setOnClickListener(v -> {
            requestLocationPermission();
        });

        binding.buttonViewPfandInfo.setOnClickListener(v -> {
            Intent intent = new Intent(requireContext(), PfandInfoActivity.class);
            startActivity(intent);
        });
    }

    private void checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            viewModel.requestLocation();
            binding.buttonEnableLocation.setVisibility(View.GONE);
        } else {
            binding.buttonEnableLocation.setVisibility(View.VISIBLE);
        }
    }

    private void requestLocationPermission() {
        ActivityCompat.requestPermissions(requireActivity(),
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

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
