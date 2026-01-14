package com.example.binbuddy.data.remote.model;

import com.google.gson.annotations.SerializedName;

/**
 * Ingredient data transfer object.
 */
public class IngredientDto {
    @SerializedName("text")
    private String text;

    @SerializedName("id")
    private String id;

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
