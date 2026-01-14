package com.example.binbuddy.ui;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.example.binbuddy.R;
import com.google.android.material.card.MaterialCardView;

import java.util.List;

public class ReturnLocationAdapter extends ListAdapter<String, ReturnLocationAdapter.ViewHolder> {

    public ReturnLocationAdapter(List<String> locations) {
        super(new DiffUtil.ItemCallback<String>() {
            @Override
            public boolean areItemsTheSame(@NonNull String oldItem, @NonNull String newItem) {
                return oldItem.equals(newItem);
            }

            @Override
            public boolean areContentsTheSame(@NonNull String oldItem, @NonNull String newItem) {
                return oldItem.equals(newItem);
            }
        });
        submitList(locations);
    }

    public void updateLocations(List<String> locations) {
        submitList(locations);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_return_location, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String location = getItem(position);
        holder.bind(location);
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        private final MaterialCardView cardView;
        private final TextView textViewLocationName;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            cardView = itemView.findViewById(R.id.cardViewLocation);
            textViewLocationName = itemView.findViewById(R.id.textViewLocationName);
        }

        void bind(String location) {
            textViewLocationName.setText(location);
        }
    }
}
