package com.example.binbuddy.ui;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.example.binbuddy.R;
import com.example.binbuddy.domain.model.ScanHistory;
import com.google.android.material.card.MaterialCardView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Adapter for displaying scan history items.
 * Uses DiffUtil for efficient list updates.
 */
public class ScanHistoryAdapter extends RecyclerView.Adapter<ScanHistoryAdapter.ScanHistoryViewHolder> {

    private List<ScanHistory> items;
    private OnItemClickListener itemClickListener;
    private final SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault());

    public interface OnItemClickListener {
        void onItemClick(ScanHistory scanHistory);
    }

    public ScanHistoryAdapter() {
        this.items = new ArrayList<>();
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.itemClickListener = listener;
    }

    @NonNull
    @Override
    public ScanHistoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_scan_history, parent, false);
        return new ScanHistoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ScanHistoryViewHolder holder, int position) {
        holder.bind(items.get(position));
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    /**
     * Update the list using DiffUtil for efficient updates
     */
    public void updateData(List<ScanHistory> newItems) {
        DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(
                new ScanHistoryDiffCallback(this.items, newItems));
        this.items = newItems != null ? new ArrayList<>(newItems) : new ArrayList<>();
        diffResult.dispatchUpdatesTo(this);
    }

    /**
     * Remove an item at the given position
     */
    public void removeItem(int position) {
        if (position >= 0 && position < items.size()) {
            List<ScanHistory> newItems = new ArrayList<>(items);
            newItems.remove(position);
            updateData(newItems);
        }
    }

    /**
     * Get item at position
     */
    public ScanHistory getItem(int position) {
        if (position >= 0 && position < items.size()) {
            return items.get(position);
        }
        return null;
    }

    class ScanHistoryViewHolder extends RecyclerView.ViewHolder {
        private final MaterialCardView cardView;
        private final TextView tvProductName;
        private final TextView tvBarcode;
        private final TextView tvTimestamp;

        ScanHistoryViewHolder(@NonNull View itemView) {
            super(itemView);
            cardView = (MaterialCardView) itemView;
            tvProductName = itemView.findViewById(R.id.tvProductName);
            tvBarcode = itemView.findViewById(R.id.tvBarcode);
            tvTimestamp = itemView.findViewById(R.id.tvTimestamp);

            cardView.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION && itemClickListener != null) {
                    itemClickListener.onItemClick(items.get(position));
                }
            });
        }

        void bind(ScanHistory scanHistory) {
            if (scanHistory == null) {
                return;
            }

            // Set product name
            String productName = itemView.getContext().getString(R.string.unknown_product);
            if (scanHistory.getProduct() != null && scanHistory.getProduct().getName() != null) {
                productName = scanHistory.getProduct().getName();
            }
            tvProductName.setText(productName);

            // Set barcode
            String barcode = scanHistory.getBarcode() != null ? scanHistory.getBarcode() : "";
            tvBarcode.setText(itemView.getContext().getString(R.string.barcode_with_value, barcode));

            // Set timestamp
            if (scanHistory.getTimestamp() != null) {
                Date date = new Date(scanHistory.getTimestamp());
                String timeStr = timeFormat.format(date);
                String dateStr = dateFormat.format(date);
                
                // Show date if not today
                Date today = new Date();
                if (!dateFormat.format(today).equals(dateStr)) {
                    tvTimestamp.setText(dateStr + " " + timeStr);
                } else {
                    tvTimestamp.setText(timeStr);
                }
            } else {
                tvTimestamp.setText("");
            }
        }
    }

    /**
     * DiffUtil callback for efficient list updates
     */
    private static class ScanHistoryDiffCallback extends DiffUtil.Callback {
        private final List<ScanHistory> oldList;
        private final List<ScanHistory> newList;

        ScanHistoryDiffCallback(List<ScanHistory> oldList, List<ScanHistory> newList) {
            this.oldList = oldList != null ? oldList : new ArrayList<>();
            this.newList = newList != null ? newList : new ArrayList<>();
        }

        @Override
        public int getOldListSize() {
            return oldList.size();
        }

        @Override
        public int getNewListSize() {
            return newList.size();
        }

        @Override
        public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
            ScanHistory oldItem = oldList.get(oldItemPosition);
            ScanHistory newItem = newList.get(newItemPosition);
            
            // Compare by ID if available, otherwise by barcode + timestamp
            if (oldItem.getId() != null && newItem.getId() != null) {
                return oldItem.getId().equals(newItem.getId());
            }
            
            return oldItem.getBarcode().equals(newItem.getBarcode()) &&
                   oldItem.getTimestamp().equals(newItem.getTimestamp());
        }

        @Override
        public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
            ScanHistory oldItem = oldList.get(oldItemPosition);
            ScanHistory newItem = newList.get(newItemPosition);
            
            // Compare all relevant fields
            boolean sameBarcode = oldItem.getBarcode().equals(newItem.getBarcode());
            boolean sameTimestamp = oldItem.getTimestamp().equals(newItem.getTimestamp());
            
            // Compare product names
            String oldProductName = oldItem.getProduct() != null && oldItem.getProduct().getName() != null
                    ? oldItem.getProduct().getName() : "";
            String newProductName = newItem.getProduct() != null && newItem.getProduct().getName() != null
                    ? newItem.getProduct().getName() : "";
            boolean sameProductName = oldProductName.equals(newProductName);
            
            return sameBarcode && sameTimestamp && sameProductName;
        }
    }
}
