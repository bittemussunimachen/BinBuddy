package com.example.binbuddy.ui.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import dagger.hilt.android.AndroidEntryPoint;

import com.example.binbuddy.R;
import com.example.binbuddy.databinding.FragmentAccountBinding;
import com.example.binbuddy.ui.ScanHistoryActivity;
import com.example.binbuddy.ui.viewmodel.UserProgressViewModel;

import java.util.Locale;

@AndroidEntryPoint
public class AccountFragment extends Fragment {

    private FragmentAccountBinding binding;
    private UserProgressViewModel progressViewModel;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentAccountBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setupViewModel();
        setupClickListeners();
        loadUserProfile();
    }
    
    private void setupViewModel() {
        progressViewModel = new ViewModelProvider(this).get(UserProgressViewModel.class);
        
        // Observe coin changes
        progressViewModel.getCoins().observe(getViewLifecycleOwner(), coins -> {
            int safeCoins = getOrDefault(coins, 0);
            binding.textViewUserCoins.setText(String.format(Locale.getDefault(), "%d coins", safeCoins));
        });
        
        // Observe level changes
        progressViewModel.getLevel().observe(getViewLifecycleOwner(), level -> {
            int safeLevel = getOrDefault(level, 1);
            binding.textViewUserLevel.setText(String.format(Locale.getDefault(), "Level %d", safeLevel));
        });
    }

    private void setupClickListeners() {
        binding.cardProfile.setOnClickListener(v -> {
            // TODO: Navigate to profile edit screen
            Toast.makeText(requireContext(), "Profile editing coming soon", Toast.LENGTH_SHORT).show();
        });

        binding.cardScanHistory.setOnClickListener(v -> {
            Intent intent = new Intent(requireContext(), ScanHistoryActivity.class);
            startActivity(intent);
        });

        binding.cardSettings.setOnClickListener(v -> {
            // TODO: Navigate to settings screen
            Toast.makeText(requireContext(), "Settings coming soon", Toast.LENGTH_SHORT).show();
        });

        binding.cardAbout.setOnClickListener(v -> {
            // TODO: Navigate to about screen
            Toast.makeText(requireContext(), "About coming soon", Toast.LENGTH_SHORT).show();
        });

        binding.cardHelp.setOnClickListener(v -> {
            // TODO: Navigate to help screen
            Toast.makeText(requireContext(), "Help coming soon", Toast.LENGTH_SHORT).show();
        });

        binding.buttonLogout.setOnClickListener(v -> {
            // TODO: Implement logout
            Toast.makeText(requireContext(), "Logout coming soon", Toast.LENGTH_SHORT).show();
        });
        
        // Make coin display clickable
        binding.textViewUserCoins.setOnClickListener(v -> showCoinDetails());
    }
    
    private void showCoinDetails() {
        int coins = getOrDefault(progressViewModel.getCoins().getValue(), 0);
        Toast.makeText(requireContext(),
                String.format(Locale.getDefault(), "You have %d coins! Keep completing quests to earn more.", coins),
                Toast.LENGTH_SHORT).show();
    }

    private void loadUserProfile() {
        // TODO: Load actual user data from ViewModel/Repository
        binding.textViewUserName.setText("Bin Buddy User");
        binding.textViewUserEmail.setText("user@example.com");
        
        // Level and coins are now loaded from ViewModel via observers
        // But set initial values here as fallback
        int level = getOrDefault(progressViewModel.getLevel().getValue(), 1);
        int coins = getOrDefault(progressViewModel.getCoins().getValue(), 0);
        binding.textViewUserLevel.setText(String.format(Locale.getDefault(), "Level %d", level));
        binding.textViewUserCoins.setText(String.format(Locale.getDefault(), "%d coins", coins));
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private int getOrDefault(Integer value, int defaultValue) {
        return value != null ? value : defaultValue;
    }
}
