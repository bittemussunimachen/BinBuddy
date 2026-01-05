package com.example.binbuddy.ui;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.binbuddy.R;
import com.example.binbuddy.ui.ProductSearchActivity.ProductItem;

import java.util.List;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ProductViewHolder> {

    private List<ProductItem> items;

    public ProductAdapter(List<ProductItem> items) {
        this.items = items;
    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_product, parent, false);
        return new ProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
        holder.bind(items.get(position));
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public void updateData(List<ProductItem> newItems) {
        this.items = newItems;
        notifyDataSetChanged();
    }

    static class ProductViewHolder extends RecyclerView.ViewHolder {
        private final TextView tvTitle;
        private final TextView tvBrand;
        private final TextView tvCategories;
        private final TextView tvPackaging;

        ProductViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvBrand = itemView.findViewById(R.id.tvBrand);
            tvCategories = itemView.findViewById(R.id.tvCategories);
            tvPackaging = itemView.findViewById(R.id.tvPackaging);
        }

        void bind(ProductItem item) {
            tvTitle.setText(item.name);
            tvBrand.setText(item.brand.isEmpty() ? "—" : item.brand);
            tvCategories.setText(item.categories.isEmpty() ? "Keine Kategorien" : item.categories);
            tvPackaging.setText(item.packaging.isEmpty() ? "Verpackung unbekannt" : item.packaging);
        }
    }
}

