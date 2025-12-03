package com.example.binbuddy.ui;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.Camera;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.binbuddy.R;
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

    private static final int CAMERA_PERMISSION_REQUEST_CODE = 100;
    private PreviewView previewView;
    private View overlay;
    private View scanFrame;
    private TextView tvInstruction;
    private ProgressBar progressBar;
    private TextView tvManualEntry;

    private ExecutorService cameraExecutor;
    private BarcodeScanner barcodeScanner;
    private boolean isScanning = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            setContentView(R.layout.activity_scanner);
            initViews();
            setupBarcodeScanner();
            checkCameraPermission();
            setupClickListeners();
        } catch (Exception e) {
            android.util.Log.e("ScannerActivity", "Error in onCreate", e);
            Toast.makeText(this, "Fehler beim Initialisieren des Scanners", Toast.LENGTH_LONG).show();
            finish();
        }
    }

    private void initViews() {
        try {
            previewView = findViewById(R.id.previewView);
            overlay = findViewById(R.id.overlay);
            scanFrame = findViewById(R.id.scanFrame);
            tvInstruction = findViewById(R.id.tvInstruction);
            progressBar = findViewById(R.id.progressBar);
            tvManualEntry = findViewById(R.id.tvManualEntry);
            
            if (previewView == null) {
                throw new NullPointerException("previewView is null");
            }
        } catch (Exception e) {
            android.util.Log.e("ScannerActivity", "Error initializing views", e);
            throw e;
        }
    }

    private void setupBarcodeScanner() {
        try {
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
            if (barcodeScanner == null) {
                throw new NullPointerException("BarcodeScanner is null");
            }

            cameraExecutor = Executors.newSingleThreadExecutor();
            if (cameraExecutor == null) {
                throw new NullPointerException("cameraExecutor is null");
            }
        } catch (Exception e) {
            android.util.Log.e("ScannerActivity", "Error setting up barcode scanner", e);
            Toast.makeText(this, "Fehler beim Initialisieren des Barcode-Scanners", Toast.LENGTH_LONG).show();
            finish();
        }
    }

    private void setupClickListeners() {
        try {
            View btnBack = findViewById(R.id.btnBack);
            if (btnBack != null) {
                btnBack.setOnClickListener(v -> finish());
            }

            if (tvManualEntry != null) {
                tvManualEntry.setOnClickListener(v -> {
                    try {
                        Toast.makeText(this, "Manuelle Eingabe wird noch implementiert", Toast.LENGTH_SHORT).show();
                    } catch (Exception e) {
                        android.util.Log.e("ScannerActivity", "Error in manual entry click", e);
                    }
                });
            }
        } catch (Exception e) {
            android.util.Log.e("ScannerActivity", "Error setting up click listeners", e);
        }
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
                Toast.makeText(this, "Kamera-Berechtigung benötigt", Toast.LENGTH_LONG).show();
                finish();
            }
        }
    }

    private void startCamera() {
        if (previewView == null) {
            android.util.Log.e("ScannerActivity", "previewView is null, cannot start camera");
            Toast.makeText(this, "Kamera-Vorschau konnte nicht initialisiert werden", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        if (cameraExecutor == null || cameraExecutor.isShutdown()) {
            android.util.Log.e("ScannerActivity", "cameraExecutor is null or shutdown");
            return;
        }

        try {
            ListenableFuture<ProcessCameraProvider> cameraProviderFuture =
                    ProcessCameraProvider.getInstance(this);

            cameraProviderFuture.addListener(() -> {
                try {
                    ProcessCameraProvider cameraProvider = cameraProviderFuture.get();
                    if (cameraProvider == null) {
                        throw new NullPointerException("CameraProvider is null");
                    }

                    if (previewView == null) {
                        android.util.Log.e("ScannerActivity", "previewView became null");
                        return;
                    }

                    Preview preview = new Preview.Builder().build();
                    preview.setSurfaceProvider(previewView.getSurfaceProvider());

                    ImageAnalysis imageAnalysis = new ImageAnalysis.Builder()
                            .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                            .build();

                    imageAnalysis.setAnalyzer(cameraExecutor, imageProxy -> {
                        try {
                            if (imageProxy == null) {
                                return;
                            }
                            
                            if (!isScanning) {
                                imageProxy.close();
                                return;
                            }

                            processImageProxy(imageProxy);
                        } catch (Exception e) {
                            android.util.Log.e("ScannerActivity", "Error in image analyzer", e);
                            try {
                                if (imageProxy != null) {
                                    imageProxy.close();
                                }
                            } catch (Exception closeException) {
                                android.util.Log.e("ScannerActivity", "Error closing imageProxy", closeException);
                            }
                        }
                    });

                    CameraSelector cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA;

                    Camera camera = cameraProvider.bindToLifecycle(
                            this,
                            cameraSelector,
                            preview,
                            imageAnalysis
                    );

                } catch (ExecutionException | InterruptedException e) {
                    android.util.Log.e("ScannerActivity", "Error starting camera", e);
                    runOnUiThread(() -> {
                        Toast.makeText(this, "Fehler beim Starten der Kamera", Toast.LENGTH_SHORT).show();
                        finish();
                    });
                } catch (Exception e) {
                    android.util.Log.e("ScannerActivity", "Unexpected error starting camera", e);
                    runOnUiThread(() -> {
                        Toast.makeText(this, "Unerwarteter Fehler beim Starten der Kamera", Toast.LENGTH_SHORT).show();
                        finish();
                    });
                }
            }, ContextCompat.getMainExecutor(this));
        } catch (Exception e) {
            android.util.Log.e("ScannerActivity", "Error getting camera provider", e);
            Toast.makeText(this, "Kamera-Service nicht verfügbar", Toast.LENGTH_LONG).show();
            finish();
        }
    }

    private void processImageProxy(androidx.camera.core.ImageProxy imageProxy) {
        if (imageProxy == null) {
            android.util.Log.w("ScannerActivity", "imageProxy is null");
            return;
        }

        if (barcodeScanner == null) {
            android.util.Log.w("ScannerActivity", "barcodeScanner is null");
            imageProxy.close();
            return;
        }

        try {
            android.media.Image mediaImage = imageProxy.getImage();
            if (mediaImage == null) {
                android.util.Log.w("ScannerActivity", "mediaImage is null");
                imageProxy.close();
                return;
            }

            int rotationDegrees = 0;
            if (imageProxy.getImageInfo() != null) {
                rotationDegrees = imageProxy.getImageInfo().getRotationDegrees();
            }

            InputImage image = InputImage.fromMediaImage(mediaImage, rotationDegrees);

            barcodeScanner.process(image)
                    .addOnSuccessListener(barcodes -> {
                        try {
                            if (!isScanning) {
                                imageProxy.close();
                                return;
                            }

                            if (barcodes != null) {
                                for (Barcode barcode : barcodes) {
                                    if (barcode != null) {
                                        String rawValue = barcode.getRawValue();
                                        if (rawValue != null && !rawValue.isEmpty()) {
                                            isScanning = false;
                                            handleBarcodeResult(rawValue);
                                            break;
                                        }
                                    }
                                }
                            }
                        } catch (Exception e) {
                            android.util.Log.e("ScannerActivity", "Error processing barcodes", e);
                        } finally {
                            try {
                                imageProxy.close();
                            } catch (Exception e) {
                                android.util.Log.e("ScannerActivity", "Error closing imageProxy in success", e);
                            }
                        }
                    })
                    .addOnFailureListener(e -> {
                        android.util.Log.e("ScannerActivity", "Error processing image", e);
                        try {
                            imageProxy.close();
                        } catch (Exception closeException) {
                            android.util.Log.e("ScannerActivity", "Error closing imageProxy in failure", closeException);
                        }
                    });
        } catch (Exception e) {
            android.util.Log.e("ScannerActivity", "Error creating InputImage", e);
            try {
                imageProxy.close();
            } catch (Exception closeException) {
                android.util.Log.e("ScannerActivity", "Error closing imageProxy in catch", closeException);
            }
        }
    }

    private void handleBarcodeResult(String barcode) {
        if (barcode == null || barcode.isEmpty()) {
            android.util.Log.w("ScannerActivity", "Barcode is null or empty");
            return;
        }

        try {
            runOnUiThread(() -> {
                try {
                    if (progressBar != null) {
                        progressBar.setVisibility(View.VISIBLE);
                    }
                    
                    if (tvInstruction != null) {
                        tvInstruction.setText("Barcode erkannt: " + barcode);
                    }

                    Intent resultIntent = new Intent();
                    resultIntent.putExtra("barcode", barcode);
                    setResult(RESULT_OK, resultIntent);

                    android.os.Handler handler = new android.os.Handler(android.os.Looper.getMainLooper());
                    handler.postDelayed(() -> {
                        try {
                            if (!isFinishing() && !isDestroyed()) {
                                finish();
                            }
                        } catch (Exception e) {
                            android.util.Log.e("ScannerActivity", "Error finishing activity", e);
                        }
                    }, 1500);
                } catch (Exception e) {
                    android.util.Log.e("ScannerActivity", "Error in handleBarcodeResult UI thread", e);
                }
            });
        } catch (Exception e) {
            android.util.Log.e("ScannerActivity", "Error scheduling handleBarcodeResult", e);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        isScanning = false;
        
        try {
            if (barcodeScanner != null) {
                barcodeScanner.close();
                barcodeScanner = null;
            }
        } catch (Exception e) {
            android.util.Log.e("ScannerActivity", "Error closing barcodeScanner", e);
        }
        
        try {
            if (cameraExecutor != null && !cameraExecutor.isShutdown()) {
                cameraExecutor.shutdown();
                try {
                    if (!cameraExecutor.awaitTermination(2, java.util.concurrent.TimeUnit.SECONDS)) {
                        cameraExecutor.shutdownNow();
                    }
                } catch (InterruptedException e) {
                    cameraExecutor.shutdownNow();
                    Thread.currentThread().interrupt();
                }
            }
        } catch (Exception e) {
            android.util.Log.e("ScannerActivity", "Error shutting down cameraExecutor", e);
        }
    }
}

