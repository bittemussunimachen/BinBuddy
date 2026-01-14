package com.example.binbuddy.data.remote.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Search response wrapper.
 */
public class SearchResponse {
    @SerializedName("products")
    private List<ProductDto> products;

    @SerializedName("count")
    private int count;

    @SerializedName("page")
    private int page;

    @SerializedName("page_size")
    private int pageSize;

    public List<ProductDto> getProducts() {
        return products;
    }

    public void setProducts(List<ProductDto> products) {
        this.products = products;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }
}
