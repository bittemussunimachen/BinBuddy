package com.example.binbuddy.domain.model;

/**
 * Domain model representing a waste category.
 */
public class WasteCategory {
    private final String id;
    private final String nameDe;
    private final String nameEn;
    private final String descriptionDe;
    private final String descriptionEn;
    private final String iconName;
    private final String colorHex;

    public WasteCategory(String id, String nameDe, String nameEn,
                        String descriptionDe, String descriptionEn,
                        String iconName, String colorHex) {
        this.id = id != null ? id : "";
        this.nameDe = nameDe != null ? nameDe : "";
        this.nameEn = nameEn != null ? nameEn : "";
        this.descriptionDe = descriptionDe != null ? descriptionDe : "";
        this.descriptionEn = descriptionEn != null ? descriptionEn : "";
        this.iconName = iconName != null ? iconName : "";
        this.colorHex = colorHex != null ? colorHex : "";
    }

    // Getters
    public String getId() {
        return id;
    }

    public String getNameDe() {
        return nameDe;
    }

    public String getNameEn() {
        return nameEn;
    }

    public String getDescriptionDe() {
        return descriptionDe;
    }

    public String getDescriptionEn() {
        return descriptionEn;
    }

    public String getIconName() {
        return iconName;
    }

    public String getColorHex() {
        return colorHex;
    }

    /**
     * Get localized name based on language code
     */
    public String getName(String languageCode) {
        if ("de".equals(languageCode)) {
            return nameDe;
        }
        return nameEn;
    }

    /**
     * Get localized description based on language code
     */
    public String getDescription(String languageCode) {
        if ("de".equals(languageCode)) {
            return descriptionDe;
        }
        return descriptionEn;
    }

    /**
     * Builder pattern for easier construction
     */
    public static class Builder {
        private String id;
        private String nameDe;
        private String nameEn;
        private String descriptionDe;
        private String descriptionEn;
        private String iconName;
        private String colorHex;

        public Builder setId(String id) {
            this.id = id;
            return this;
        }

        public Builder setNameDe(String nameDe) {
            this.nameDe = nameDe;
            return this;
        }

        public Builder setNameEn(String nameEn) {
            this.nameEn = nameEn;
            return this;
        }

        public Builder setDescriptionDe(String descriptionDe) {
            this.descriptionDe = descriptionDe;
            return this;
        }

        public Builder setDescriptionEn(String descriptionEn) {
            this.descriptionEn = descriptionEn;
            return this;
        }

        public Builder setIconName(String iconName) {
            this.iconName = iconName;
            return this;
        }

        public Builder setColorHex(String colorHex) {
            this.colorHex = colorHex;
            return this;
        }

        public WasteCategory build() {
            return new WasteCategory(id, nameDe, nameEn, descriptionDe,
                    descriptionEn, iconName, colorHex);
        }
    }
}
