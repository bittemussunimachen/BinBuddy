package com.example.binbuddy.ui.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.binbuddy.R;
import com.example.binbuddy.databinding.FragmentAccountBinding;
import com.example.binbuddy.ui.ScanHistoryActivity;

public class AccountFragment extends Fragment {

    private FragmentAccountBinding binding;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentAccountBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setupClickListeners();
        loadUserProfile();
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
    }

    private void loadUserProfile() {
        // TODO: Load actual user data from ViewModel/Repository
        binding.textViewUserName.setText("Bin Buddy User");
        binding.textViewUserEmail.setText("user@example.com");
        binding.textViewUserLevel.setText("Level 4");
        binding.textViewUserCoins.setText("240 coins");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
