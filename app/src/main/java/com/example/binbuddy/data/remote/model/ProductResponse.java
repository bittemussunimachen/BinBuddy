package com.example.binbuddy.data.remote.model;

import com.google.gson.annotations.SerializedName;

/**
 * Response wrapper for product API calls.
 */
public class ProductResponse {
    @SerializedName("status")
    private int status;

    @SerializedName("status_verbose")
    private String statusVerbose;

    @SerializedName("product")
    private ProductDto product;

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getStatusVerbose() {
        return statusVerbose;
    }

    public void setStatusVerbose(String statusVerbose) {
        this.statusVerbose = statusVerbose;
    }

    public ProductDto getProduct() {
        return product;
    }

    public void setProduct(ProductDto product) {
        this.product = product;
    }
}
