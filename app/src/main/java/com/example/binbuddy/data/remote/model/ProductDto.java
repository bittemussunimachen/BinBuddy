package com.example.binbuddy.data.remote.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Product data transfer object from API.
 */
public class ProductDto {
    @SerializedName("code")
    private String barcode;

    @SerializedName("product_name")
    private String productName;

    @SerializedName("brands")
    private String brands;

    @SerializedName("categories")
    private String categories;

    @SerializedName("packaging")
    private String packaging;

    @SerializedName("quantity")
    private String quantity;

    @SerializedName("ingredients")
    private List<IngredientDto> ingredients;

    @SerializedName("labels")
    private String labels;

    @SerializedName("generic_name")
    private String genericName;

    @SerializedName("image_url")
    private String imageUrl;

    // Getters and setters
    public String getBarcode() {
        return barcode;
    }

    public void setBarcode(String barcode) {
        this.barcode = barcode;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getBrands() {
        return brands;
    }

    public void setBrands(String brands) {
        this.brands = brands;
    }

    public String getCategories() {
        return categories;
    }

    public void setCategories(String categories) {
        this.categories = categories;
    }

    public String getPackaging() {
        return packaging;
    }

    public void setPackaging(String packaging) {
        this.packaging = packaging;
    }

    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }

    public List<IngredientDto> getIngredients() {
        return ingredients;
    }

    public void setIngredients(List<IngredientDto> ingredients) {
        this.ingredients = ingredients;
    }

    public String getLabels() {
        return labels;
    }

    public void setLabels(String labels) {
        this.labels = labels;
    }

    public String getGenericName() {
        return genericName;
    }

    public void setGenericName(String genericName) {
        this.genericName = genericName;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}
