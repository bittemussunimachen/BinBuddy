package com.example.binbuddy.domain.model;

import java.util.List;

/**
 * Domain model representing Pfand (deposit) information for a product.
 */
public class PfandInfo {
    private final boolean hasPfand;
    private final Double amount; // in EUR
    private final List<String> returnLocations;

    public PfandInfo(boolean hasPfand, Double amount, List<String> returnLocations) {
        this.hasPfand = hasPfand;
        this.amount = amount;
        this.returnLocations = returnLocations != null ? returnLocations : java.util.Collections.emptyList();
    }

    // Getters
    public boolean hasPfand() {
        return hasPfand;
    }

    public Double getAmount() {
        return amount;
    }

    public List<String> getReturnLocations() {
        return returnLocations;
    }

    /**
     * Get formatted amount string (e.g., "0,25 €")
     */
    public String getFormattedAmount() {
        if (amount == null) {
            return "";
        }
        return String.format("%.2f €", amount);
    }

    /**
     * Builder pattern for easier construction
     */
    public static class Builder {
        private boolean hasPfand;
        private Double amount;
        private List<String> returnLocations;

        public Builder setHasPfand(boolean hasPfand) {
            this.hasPfand = hasPfand;
            return this;
        }

        public Builder setAmount(Double amount) {
            this.amount = amount;
            return this;
        }

        public Builder setReturnLocations(List<String> returnLocations) {
            this.returnLocations = returnLocations;
            return this;
        }

        public PfandInfo build() {
            return new PfandInfo(hasPfand, amount, returnLocations);
        }
    }
}
