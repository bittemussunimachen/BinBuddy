package com.example.binbuddy.ui;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.binbuddy.R;
import com.example.binbuddy.domain.model.ScanHistory;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.List;

/**
 * Activity to display scan history.
 * Shows a list of previously scanned products with swipe-to-delete functionality.
 */
public class ScanHistoryActivity extends AppCompatActivity {

    private RecyclerView rvScanHistory;
    private LinearLayout emptyState;
    private ScanHistoryAdapter adapter;
    
    // Temporary in-memory storage (will be replaced with ViewModel/Repository later)
    private List<ScanHistory> scanHistoryList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan_history);

        initViews();
        setupRecyclerView();
        setupSwipeToDelete();
        loadScanHistory();
    }

    private void initViews() {
        rvScanHistory = findViewById(R.id.rvScanHistory);
        emptyState = findViewById(R.id.emptyState);

        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(v -> finish());
    }

    private void setupRecyclerView() {
        adapter = new ScanHistoryAdapter();
        adapter.setOnItemClickListener(scanHistory -> {
            // Navigate to ProductDetailActivity
            navigateToProductDetailIfValid(scanHistory != null ? scanHistory.getBarcode() : null);
        });

        rvScanHistory.setLayoutManager(new LinearLayoutManager(this));
        rvScanHistory.setAdapter(adapter);
    }

    private void navigateToProductDetailIfValid(String barcode) {
        if (TextUtils.isEmpty(barcode)) {
            return;
        }
        Intent intent = new Intent(ScanHistoryActivity.this, ProductDetailActivity.class);
        intent.putExtra(ProductDetailActivity.EXTRA_BARCODE, barcode.trim());
        startActivity(intent);
    }

    private void setupSwipeToDelete() {
        ItemTouchHelper.SimpleCallback swipeCallback = new ItemTouchHelper.SimpleCallback(
                0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder,
                                  RecyclerView.ViewHolder target) {
                return false; // No drag and drop
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                int position = viewHolder.getAdapterPosition();
                ScanHistory deletedItem = adapter.getItem(position);
                
                // Remove from list
                adapter.removeItem(position);
                
                // Show undo snackbar
                Snackbar snackbar = Snackbar.make(
                        rvScanHistory,
                        getString(R.string.scan_history_deleted),
                        Snackbar.LENGTH_LONG);
                snackbar.setAction(getString(R.string.undo), v -> {
                    // Restore item
                    if (deletedItem != null && scanHistoryList != null) {
                        scanHistoryList.add(position, deletedItem);
                        adapter.updateData(scanHistoryList);
                    }
                });
                snackbar.show();
                
                updateEmptyState();
            }
        };

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(swipeCallback);
        itemTouchHelper.attachToRecyclerView(rvScanHistory);
    }

    private void loadScanHistory() {
        // TODO: Replace with ViewModel/Repository call
        // For now, use empty list or sample data
        scanHistoryList = new ArrayList<>();
        
        // Sample data for testing (remove when ViewModel is implemented)
        // Uncomment below for testing:
        /*
        scanHistoryList.add(new ScanHistory.Builder()
                .setId(1L)
                .setBarcode("1234567890123")
                .setProduct(new Product.Builder()
                        .setName("Sample Product")
                        .setBarcode("1234567890123")
                        .build())
                .setTimestamp(System.currentTimeMillis())
                .build());
        */
        
        adapter.updateData(scanHistoryList);
        updateEmptyState();
    }

    private void updateEmptyState() {
        if (adapter.getItemCount() == 0) {
            rvScanHistory.setVisibility(View.GONE);
            emptyState.setVisibility(View.VISIBLE);
        } else {
            rvScanHistory.setVisibility(View.VISIBLE);
            emptyState.setVisibility(View.GONE);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Reload data when returning to this activity
        loadScanHistory();
    }
}
