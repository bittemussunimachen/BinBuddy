package com.example.binbuddy.ui;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.example.binbuddy.R;
import com.example.binbuddy.domain.model.Product;

import java.util.ArrayList;
import java.util.List;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ProductViewHolder> {

    private List<Product> items;
    private OnItemClickListener onItemClickListener;

    public interface OnItemClickListener {
        void onItemClick(Product product);
    }

    public ProductAdapter(List<Product> items) {
        this.items = items != null ? new ArrayList<>(items) : new ArrayList<>();
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.onItemClickListener = listener;
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

    public void updateData(List<Product> newItems) {
        if (newItems == null) {
            newItems = new ArrayList<>();
        }
        
        DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(
                new ProductDiffCallback(this.items, newItems)
        );
        this.items = new ArrayList<>(newItems);
        diffResult.dispatchUpdatesTo(this);
    }

    class ProductViewHolder extends RecyclerView.ViewHolder {
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
            
            itemView.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION && onItemClickListener != null) {
                    onItemClickListener.onItemClick(items.get(position));
                }
            });
        }

        void bind(Product product) {
            if (product == null) {
                return;
            }

            android.content.Context context = itemView.getContext();
            
            String productName = product.getName();
            tvTitle.setText(TextUtils.isEmpty(productName)
                    ? context.getString(R.string.no_product_data)
                    : productName);
            
            String brand = product.getBrand();
            String brandText = TextUtils.isEmpty(brand)
                    ? context.getString(R.string.no_brand)
                    : brand;
            tvBrand.setText(brandText);
            
            if (product.getCategories() != null && !product.getCategories().isEmpty()) {
                tvCategories.setText(TextUtils.join(", ", product.getCategories()));
            } else {
                tvCategories.setText(context.getString(R.string.no_categories));
            }
            
            String packaging = product.getPackaging();
            String packagingText = TextUtils.isEmpty(packaging)
                    ? context.getString(R.string.packaging_unknown)
                    : packaging;
            tvPackaging.setText(packagingText);
        }
    }

    private static class ProductDiffCallback extends DiffUtil.Callback {
        private final List<Product> oldList;
        private final List<Product> newList;

        ProductDiffCallback(List<Product> oldList, List<Product> newList) {
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
            Product oldItem = oldList.get(oldItemPosition);
            Product newItem = newList.get(newItemPosition);
            return oldItem != null && newItem != null && 
                   oldItem.getBarcode() != null && 
                   oldItem.getBarcode().equals(newItem.getBarcode());
        }

        @Override
        public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
            Product oldItem = oldList.get(oldItemPosition);
            Product newItem = newList.get(newItemPosition);
            if (oldItem == null || newItem == null) {
                return oldItem == newItem;
            }
            return TextUtils.equals(oldItem.getName(), newItem.getName()) &&
                   TextUtils.equals(oldItem.getBrand(), newItem.getBrand());
        }
    }
}
