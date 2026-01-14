package com.example.binbuddy.ui;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;

import com.example.binbuddy.R;
import com.example.binbuddy.databinding.ActivityScannerBinding;
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

public class ScannerActivity extends AppCompatActivity {

    private static final String TAG = "ScannerActivity";
    private static final int CAMERA_PERMISSION_REQUEST_CODE = 100;
    private static final long FINISH_DELAY_MS = 1500L;
    private static final long CAMERA_EXECUTOR_SHUTDOWN_TIMEOUT_SECONDS = 2L;

    private ActivityScannerBinding binding;
    private ScannerViewModel viewModel;
    private ExecutorService cameraExecutor;
    private BarcodeScanner barcodeScanner;
    private ActivityResultLauncher<Intent> manualEntryLauncher;
    private final Handler finishHandler = new Handler(Looper.getMainLooper());
    private final Runnable finishRunnable = () -> {
        if (!isFinishing() && !isDestroyed()) {
            finish();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityScannerBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

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
        viewModel.getScanResult().observe(this, barcode -> {
            if (barcode != null && !barcode.isEmpty()) {
                handleBarcodeResult(barcode);
            }
        });

        viewModel.getError().observe(this, error -> {
            if (error != null) {
                Toast.makeText(this, error, Toast.LENGTH_SHORT).show();
                viewModel.clearError();
            }
        });

        viewModel.getIsScanning().observe(this, isScanning -> {
            boolean scanning = Boolean.TRUE.equals(isScanning);
            binding.progressBar.setVisibility(scanning ? View.GONE : View.VISIBLE);
        });
    }

    private void setupClickListeners() {
        binding.toolbar.setNavigationOnClickListener(v -> finish());
        binding.btnManualEntry.setOnClickListener(v -> openManualEntry());
    }

    private void setupActivityResultLaunchers() {
        manualEntryLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        String barcode = result.getData().getStringExtra(ProductDetailActivity.EXTRA_BARCODE);
                        if (barcode != null && !barcode.isEmpty()) {
                            viewModel.processBarcode(barcode);
                        }
                    }
                }
        );
    }

    private void openManualEntry() {
        Intent intent = new Intent(this, ManualEntryActivity.class);
        manualEntryLauncher.launch(intent);
    }

    private void checkCameraPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_GRANTED) {
            startCamera();
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.CAMERA},
                    CAMERA_PERMISSION_REQUEST_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == CAMERA_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startCamera();
            } else {
                Toast.makeText(this, getString(R.string.scanner_permission_required), Toast.LENGTH_LONG).show();
                finish();
            }
        }
    }

    private void startCamera() {
        ListenableFuture<ProcessCameraProvider> cameraProviderFuture =
                ProcessCameraProvider.getInstance(this);

        cameraProviderFuture.addListener(() -> {
            try {
                ProcessCameraProvider cameraProvider = cameraProviderFuture.get();

                Preview preview = new Preview.Builder().build();
                preview.setSurfaceProvider(binding.previewView.getSurfaceProvider());

                ImageAnalysis imageAnalysis = new ImageAnalysis.Builder()
                        .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                        .build();

                imageAnalysis.setAnalyzer(cameraExecutor, imageProxy -> {
                    Boolean isScanning = viewModel.getIsScanning().getValue();
                    if (isScanning == null || !isScanning) {
                        imageProxy.close();
                        return;
                    }

                    processImageProxy(imageProxy);
                });

                CameraSelector cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA;

                cameraProvider.bindToLifecycle(
                        this,
                        cameraSelector,
                        preview,
                        imageAnalysis
                );

            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                Log.e(TAG, "Interrupted starting camera", e);
                runOnUiThread(() -> {
                    Toast.makeText(this, getString(R.string.scanner_error_camera), Toast.LENGTH_SHORT).show();
                    finish();
                });
            } catch (ExecutionException e) {
                Log.e(TAG, "Error starting camera", e);
                runOnUiThread(() -> {
                    Toast.makeText(this, getString(R.string.scanner_error_camera), Toast.LENGTH_SHORT).show();
                    finish();
                });
            }
        }, ContextCompat.getMainExecutor(this));
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
                    Boolean isScanning = viewModel.getIsScanning().getValue();
                    if (isScanning == null || !isScanning) {
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

        Intent resultIntent = new Intent();
        resultIntent.putExtra(ProductDetailActivity.EXTRA_BARCODE, barcode);
        setResult(RESULT_OK, resultIntent);

        finishHandler.removeCallbacks(finishRunnable);
        finishHandler.postDelayed(finishRunnable, FINISH_DELAY_MS);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        finishHandler.removeCallbacks(finishRunnable);
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
