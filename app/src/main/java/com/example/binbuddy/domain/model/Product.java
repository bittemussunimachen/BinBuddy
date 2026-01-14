package com.example.binbuddy.domain.model;

import java.util.List;

public class Product {
    private String id;
    private String barcode;
    private String name;
    private String brand;
    private List<String> categories;
    private String packaging;
    private String quantity;
    private List<String> ingredients;
    private String labels;
    private String genericName;
    private String imageUrl;

    public Product() {
    }

    public Product(String barcode, String name, String brand, List<String> categories, 
                   String packaging, String quantity, List<String> ingredients, 
                   String labels, String genericName, String imageUrl) {
        this.barcode = barcode;
        this.name = name;
        this.brand = brand;
        this.categories = categories;
        this.packaging = packaging;
        this.quantity = quantity;
        this.ingredients = ingredients;
        this.labels = labels;
        this.genericName = genericName;
        this.imageUrl = imageUrl;
    }

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getBarcode() {
        return barcode;
    }

    public void setBarcode(String barcode) {
        this.barcode = barcode;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public List<String> getCategories() {
        return categories;
    }

    public void setCategories(List<String> categories) {
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

    public List<String> getIngredients() {
        return ingredients;
    }

    public void setIngredients(List<String> ingredients) {
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
