package com.example.binbuddy.domain.model;

/**
 * Domain model representing a scan history entry.
 */
public class ScanHistory {
    private final Long id;
    private final String barcode;
    private final Product product;
    private final Long timestamp;
    private final String location;

    public ScanHistory(Long id, String barcode, Product product, Long timestamp, String location) {
        this.id = id;
        this.barcode = barcode != null ? barcode : "";
        this.product = product;
        this.timestamp = timestamp != null ? timestamp : System.currentTimeMillis();
        this.location = location;
    }

    // Getters
    public Long getId() {
        return id;
    }

    public String getBarcode() {
        return barcode;
    }

    public Product getProduct() {
        return product;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public String getLocation() {
        return location;
    }

    /**
     * Builder pattern for easier construction
     */
    public static class Builder {
        private Long id;
        private String barcode;
        private Product product;
        private Long timestamp;
        private String location;

        public Builder setBarcode(String barcode) {
            this.barcode = barcode;
            return this;
        }

        public Builder setProduct(Product product) {
            this.product = product;
            return this;
        }

        public Builder setTimestamp(Long timestamp) {
            this.timestamp = timestamp;
            return this;
        }

        public Builder setLocation(String location) {
            this.location = location;
            return this;
        }

        public Builder setId(Long id) {
            this.id = id;
            return this;
        }

        public ScanHistory build() {
            return new ScanHistory(id, barcode, product, timestamp, location);
        }
    }
}
