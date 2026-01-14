package com.example.binbuddy.domain.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Holds environmental and recycling information for a product.
 * This is derived from the Open Food Facts payload and is kept null/empty safe.
 */
public class EnvironmentInfo {

    private String ecoScoreGrade;
    private Integer ecoScoreScore;
    private Double co2Per100g;
    private String packagingWarning;
    private final List<String> packagingMaterials = new ArrayList<>();
    private final List<String> packagingParts = new ArrayList<>();
    private boolean containsPalmOil;
    private String threatenedSpeciesIngredient;

    public String getEcoScoreGrade() {
        return ecoScoreGrade;
    }

    public void setEcoScoreGrade(String ecoScoreGrade) {
        this.ecoScoreGrade = ecoScoreGrade;
    }

    public Integer getEcoScoreScore() {
        return ecoScoreScore;
    }

    public void setEcoScoreScore(Integer ecoScoreScore) {
        this.ecoScoreScore = ecoScoreScore;
    }

    public Double getCo2Per100g() {
        return co2Per100g;
    }

    public void setCo2Per100g(Double co2Per100g) {
        this.co2Per100g = co2Per100g;
    }

    public String getPackagingWarning() {
        return packagingWarning;
    }

    public void setPackagingWarning(String packagingWarning) {
        this.packagingWarning = packagingWarning;
    }

    public List<String> getPackagingMaterials() {
        return packagingMaterials;
    }

    public List<String> getPackagingParts() {
        return packagingParts;
    }

    public boolean isContainsPalmOil() {
        return containsPalmOil;
    }

    public void setContainsPalmOil(boolean containsPalmOil) {
        this.containsPalmOil = containsPalmOil;
    }

    public String getThreatenedSpeciesIngredient() {
        return threatenedSpeciesIngredient;
    }

    public void setThreatenedSpeciesIngredient(String threatenedSpeciesIngredient) {
        this.threatenedSpeciesIngredient = threatenedSpeciesIngredient;
    }
}
