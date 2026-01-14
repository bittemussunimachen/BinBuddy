package com.example.binbuddy.ui;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.example.binbuddy.R;
import com.example.binbuddy.domain.model.WasteCategory;
import com.google.android.material.card.MaterialCardView;

import java.util.List;
import java.util.Locale;

public class WasteCategoryAdapter extends ListAdapter<WasteCategory, WasteCategoryAdapter.ViewHolder> {
    private final OnCategoryClickListener listener;

    public interface OnCategoryClickListener {
        void onCategoryClick(WasteCategory category);
    }

    public WasteCategoryAdapter(List<WasteCategory> categories, OnCategoryClickListener listener) {
        super(new DiffUtil.ItemCallback<WasteCategory>() {
            @Override
            public boolean areItemsTheSame(@NonNull WasteCategory oldItem, @NonNull WasteCategory newItem) {
                return oldItem.getId().equals(newItem.getId());
            }

            @Override
            public boolean areContentsTheSame(@NonNull WasteCategory oldItem, @NonNull WasteCategory newItem) {
                return oldItem.getId().equals(newItem.getId()) &&
                       oldItem.getNameDe().equals(newItem.getNameDe());
            }
        });
        this.listener = listener;
        submitList(categories);
    }

    public void updateCategories(List<WasteCategory> categories) {
        submitList(categories);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_waste_category, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        WasteCategory category = getItem(position);
        holder.bind(category, listener);
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        private final MaterialCardView cardView;
        private final TextView textViewName;
        private final TextView textViewDescription;
        private final View viewColorIndicator;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            cardView = itemView.findViewById(R.id.cardViewCategory);
            textViewName = itemView.findViewById(R.id.textViewCategoryName);
            textViewDescription = itemView.findViewById(R.id.textViewCategoryDescription);
            viewColorIndicator = itemView.findViewById(R.id.viewColorIndicator);
        }

        void bind(WasteCategory category, OnCategoryClickListener listener) {
            String languageCode = Locale.getDefault().getLanguage();
            textViewName.setText(category.getName(languageCode));
            textViewDescription.setText(category.getDescription(languageCode));

            // Set color indicator
            try {
                int color = Color.parseColor(category.getColorHex());
                viewColorIndicator.setBackgroundColor(color);
            } catch (IllegalArgumentException e) {
                viewColorIndicator.setBackgroundColor(Color.GRAY);
            }

            cardView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onCategoryClick(category);
                }
            });
        }
    }
}
