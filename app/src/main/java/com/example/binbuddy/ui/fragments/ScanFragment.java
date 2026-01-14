package com.example.binbuddy.ui.fragments;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import dagger.hilt.android.AndroidEntryPoint;

import com.example.binbuddy.R;
import com.example.binbuddy.databinding.FragmentScanBinding;
import com.example.binbuddy.ui.ManualEntryActivity;
import com.example.binbuddy.ui.ProductDetailActivity;
import com.example.binbuddy.ui.viewmodel.ScannerViewModel;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.mlkit.vision.barcode.BarcodeScanner;
import com.google.mlkit.vision.barcode.BarcodeScannerOptions;
import com.google.mlkit.vision.barcode.BarcodeScanning;
import com.google.mlkit.vision.barcode.common.Barcode;
import com.google.mlkit.vision.common.InputImage;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;

@AndroidEntryPoint
public class ScanFragment extends Fragment {

    private static final String TAG = "ScanFragment";
    private static final long CAMERA_EXECUTOR_SHUTDOWN_TIMEOUT_SECONDS = 2L;

    private FragmentScanBinding binding;
    private ScannerViewModel viewModel;
    private ExecutorService cameraExecutor;
    private BarcodeScanner barcodeScanner;
    private ActivityResultLauncher<Intent> manualEntryLauncher;
    private ActivityResultLauncher<String> cameraPermissionLauncher;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentScanBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel = new ViewModelProvider(this).get(ScannerViewModel.class);
        
        setupBarcodeScanner();
        setupActivityResultLaunchers();
        setupObservers();
        setupClickListeners();
        checkCameraPermission();
    }

    private void setupBarcodeScanner() {
        BarcodeScannerOptions options = new BarcodeScannerOptions.Builder()
                .setBarcodeFormats(
                        Barcode.FORMAT_EAN_13,
                        Barcode.FORMAT_EAN_8,
                        Barcode.FORMAT_UPC_A,
                        Barcode.FORMAT_UPC_E,
                        Barcode.FORMAT_CODE_128,
                        Barcode.FORMAT_CODE_39,
                        Barcode.FORMAT_QR_CODE)
                .build();

        barcodeScanner = BarcodeScanning.getClient(options);
        cameraExecutor = Executors.newSingleThreadExecutor();
    }

    private void setupObservers() {
        viewModel.getScanResult().observe(getViewLifecycleOwner(), barcode -> {
            if (barcode != null && !barcode.isEmpty()) {
                handleBarcodeResult(barcode);
            }
        });

        viewModel.getError().observe(getViewLifecycleOwner(), error -> {
            if (error != null) {
                Toast.makeText(requireContext(), error, Toast.LENGTH_SHORT).show();
                viewModel.clearError();
            }
        });

        viewModel.getIsScanning().observe(getViewLifecycleOwner(), isScanning -> {
            boolean scanning = Boolean.TRUE.equals(isScanning);
            binding.progressBar.setVisibility(scanning ? View.GONE : View.VISIBLE);
        });
    }

    private void setupClickListeners() {
        binding.toolbar.setNavigationOnClickListener(v -> {
            NavController navController = Navigation.findNavController(requireView());
            navController.navigateUp();
        });
        binding.btnManualEntry.setOnClickListener(v -> openManualEntry());
    }

    private void setupActivityResultLaunchers() {
        manualEntryLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                        String barcode = result.getData().getStringExtra(ProductDetailActivity.EXTRA_BARCODE);
                        if (barcode != null && !barcode.isEmpty()) {
                            viewModel.processBarcode(barcode);
                        }
                    }
                }
        );

        cameraPermissionLauncher = registerForActivityResult(
                new ActivityResultContracts.RequestPermission(),
                isGranted -> {
                    if (isGranted) {
                        startCamera();
                    } else {
                        Toast.makeText(requireContext(), getString(R.string.scanner_permission_required), Toast.LENGTH_LONG).show();
                        NavController navController = Navigation.findNavController(requireView());
                        navController.navigateUp();
                    }
                }
        );
    }

    private void openManualEntry() {
        Intent intent = new Intent(requireContext(), ManualEntryActivity.class);
        manualEntryLauncher.launch(intent);
    }

    private void checkCameraPermission() {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_GRANTED) {
            startCamera();
        } else {
            cameraPermissionLauncher.launch(Manifest.permission.CAMERA);
        }
    }

    private void startCamera() {
        ListenableFuture<ProcessCameraProvider> cameraProviderFuture =
                ProcessCameraProvider.getInstance(requireContext());

        cameraProviderFuture.addListener(() -> {
            try {
                ProcessCameraProvider cameraProvider = cameraProviderFuture.get();

                Preview preview = new Preview.Builder().build();
                preview.setSurfaceProvider(binding.previewView.getSurfaceProvider());

                ImageAnalysis imageAnalysis = new ImageAnalysis.Builder()
                        .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                        .build();

                imageAnalysis.setAnalyzer(cameraExecutor, imageProxy -> {
                    boolean isScanning = Boolean.TRUE.equals(viewModel.getIsScanning().getValue());
                    if (!isScanning) {
                        imageProxy.close();
                        return;
                    }

                    processImageProxy(imageProxy);
                });

                CameraSelector cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA;

                cameraProvider.bindToLifecycle(
                        getViewLifecycleOwner(),
                        cameraSelector,
                        preview,
                        imageAnalysis
                );

            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                Log.e(TAG, "Interrupted starting camera", e);
                Toast.makeText(requireContext(), getString(R.string.scanner_error_camera), Toast.LENGTH_SHORT).show();
            } catch (ExecutionException e) {
                Log.e(TAG, "Error starting camera", e);
                Toast.makeText(requireContext(), getString(R.string.scanner_error_camera), Toast.LENGTH_SHORT).show();
            }
        }, ContextCompat.getMainExecutor(requireContext()));
    }

    private void processImageProxy(androidx.camera.core.ImageProxy imageProxy) {
        android.media.Image mediaImage = imageProxy.getImage();
        if (mediaImage == null) {
            imageProxy.close();
            return;
        }

        int rotationDegrees = imageProxy.getImageInfo() != null 
            ? imageProxy.getImageInfo().getRotationDegrees() 
            : 0;

        InputImage image = InputImage.fromMediaImage(mediaImage, rotationDegrees);

        barcodeScanner.process(image)
                .addOnSuccessListener(barcodes -> {
                    boolean isScanning = Boolean.TRUE.equals(viewModel.getIsScanning().getValue());
                    if (!isScanning) {
                        imageProxy.close();
                        return;
                    }

                    if (barcodes != null) {
                        for (Barcode barcode : barcodes) {
                            if (barcode != null) {
                                String rawValue = barcode.getRawValue();
                                if (rawValue != null && !rawValue.isEmpty()) {
                                    viewModel.processBarcode(rawValue);
                                    imageProxy.close();
                                    return;
                                }
                            }
                        }
                    }
                    imageProxy.close();
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error processing image", e);
                    imageProxy.close();
                });
    }

    private void handleBarcodeResult(String barcode) {
        binding.progressBar.setVisibility(View.VISIBLE);
        binding.tvInstruction.setText(getString(R.string.scanner_barcode_detected, barcode));

        Intent intent = new Intent(requireContext(), ProductDetailActivity.class);
        intent.putExtra(ProductDetailActivity.EXTRA_BARCODE, barcode);
        startActivity(intent);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        viewModel.stopScanning();
        
        if (barcodeScanner != null) {
            barcodeScanner.close();
        }
        
        if (cameraExecutor != null && !cameraExecutor.isShutdown()) {
            cameraExecutor.shutdown();
            try {
                if (!cameraExecutor.awaitTermination(CAMERA_EXECUTOR_SHUTDOWN_TIMEOUT_SECONDS, java.util.concurrent.TimeUnit.SECONDS)) {
                    cameraExecutor.shutdownNow();
                }
            } catch (InterruptedException e) {
                cameraExecutor.shutdownNow();
                Thread.currentThread().interrupt();
            }
        }
        
        binding = null;
    }
}