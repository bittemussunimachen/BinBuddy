package com.example.binbuddy.ui;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.binbuddy.R;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.regex.Pattern;

/**
 * Activity for manually entering a barcode.
 * Validates barcode format and returns it to the caller via Activity result.
 */
public class ManualEntryActivity extends AppCompatActivity {

    private TextInputLayout tilBarcode;
    private TextInputEditText etBarcode;
    private TextView tvError;
    private MaterialButton btnSubmit;

    // Barcode format patterns
    private static final Pattern EAN_13_PATTERN = Pattern.compile("^\\d{13}$");
    private static final Pattern EAN_8_PATTERN = Pattern.compile("^\\d{8}$");
    private static final Pattern UPC_A_PATTERN = Pattern.compile("^\\d{12}$");
    private static final Pattern UPC_E_PATTERN = Pattern.compile("^\\d{8}$");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manual_entry);

        initViews();
        setupClickListeners();
        setupTextWatcher();
    }

    private void initViews() {
        tilBarcode = findViewById(R.id.tilBarcode);
        etBarcode = findViewById(R.id.etBarcode);
        tvError = findViewById(R.id.tvError);
        btnSubmit = findViewById(R.id.btnSubmit);

        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(v -> finish());
    }

    private void setupClickListeners() {
        btnSubmit.setOnClickListener(v -> validateAndSubmit());

        etBarcode.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == android.view.inputmethod.EditorInfo.IME_ACTION_DONE) {
                validateAndSubmit();
                return true;
            }
            return false;
        });
    }

    private void setupTextWatcher() {
        etBarcode.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Clear errors when user starts typing
                clearErrors();
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    private void validateAndSubmit() {
        String barcode = etBarcode.getText() != null ? etBarcode.getText().toString().trim() : "";

        if (TextUtils.isEmpty(barcode)) {
            showError(getString(R.string.error_barcode_empty));
            return;
        }

        // Remove any non-digit characters
        barcode = barcode.replaceAll("[^\\d]", "");

        if (TextUtils.isEmpty(barcode)) {
            showError(getString(R.string.invalid_barcode));
            return;
        }

        // Validate barcode format
        BarcodeValidationResult validation = validateBarcode(barcode);
        if (!validation.isValid) {
            showError(validation.errorMessage);
            return;
        }

        // Use normalized barcode if available (for padded formats)
        String finalBarcode = validation.normalizedBarcode != null 
                ? validation.normalizedBarcode 
                : barcode;

        // Return to caller (e.g., ScannerActivity)
        returnBarcodeResult(finalBarcode);
    }

    private BarcodeValidationResult validateBarcode(String barcode) {
        if (barcode.length() < 8) {
            return new BarcodeValidationResult(false, getString(R.string.barcode_too_short));
        }

        if (barcode.length() > 13) {
            return new BarcodeValidationResult(false, getString(R.string.barcode_too_long));
        }

        // Check for supported formats
        boolean isValidFormat = EAN_13_PATTERN.matcher(barcode).matches() ||
                EAN_8_PATTERN.matcher(barcode).matches() ||
                UPC_A_PATTERN.matcher(barcode).matches() ||
                UPC_E_PATTERN.matcher(barcode).matches();

        if (!isValidFormat) {
            // For EAN-13, try padding with leading zeros if it's 12 digits (UPC-A)
            if (barcode.length() == 12) {
                String paddedBarcode = "0" + barcode;
                if (EAN_13_PATTERN.matcher(paddedBarcode).matches()) {
                    return new BarcodeValidationResult(true, null, paddedBarcode);
                }
            }
            // For EAN-8, try padding if it's 7 digits
            if (barcode.length() == 7) {
                String paddedBarcode = "0" + barcode;
                if (EAN_8_PATTERN.matcher(paddedBarcode).matches()) {
                    return new BarcodeValidationResult(true, null, paddedBarcode);
                }
            }
            return new BarcodeValidationResult(false, getString(R.string.barcode_invalid_format));
        }

        return new BarcodeValidationResult(true, null, barcode);
    }

    private void returnBarcodeResult(String barcode) {
        Intent intent = new Intent();
        intent.putExtra(ProductDetailActivity.EXTRA_BARCODE, barcode);
        setResult(RESULT_OK, intent);
        finish();
    }

    private void showError(String message) {
        tvError.setText(message);
        tvError.setVisibility(View.VISIBLE);
        tilBarcode.setError(message);
    }

    private void clearErrors() {
        tvError.setVisibility(View.GONE);
        tilBarcode.setError(null);
    }

    /**
     * Result of barcode validation
     */
    private static class BarcodeValidationResult {
        final boolean isValid;
        final String errorMessage;
        final String normalizedBarcode;

        BarcodeValidationResult(boolean isValid, String errorMessage) {
            this(isValid, errorMessage, null);
        }

        BarcodeValidationResult(boolean isValid, String errorMessage, String normalizedBarcode) {
            this.isValid = isValid;
            this.errorMessage = errorMessage;
            this.normalizedBarcode = normalizedBarcode;
        }
    }
}
