package com.example.binbuddy.domain.service;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * Static classification rules for waste categorization.
 * Contains mappings and patterns used by WasteClassificationService.
 */
public class ClassificationRules {

    /**
     * Keywords that indicate plastic packaging
     */
    public static final Set<String> PLASTIC_KEYWORDS = new HashSet<>(Arrays.asList(
            "plastic", "kunststoff", "pet", "pe", "pp", "ps", "pvc", "polyethylen",
            "polypropylen", "polystyrol"
    ));

    /**
     * Keywords that indicate metal packaging
     */
    public static final Set<String> METAL_KEYWORDS = new HashSet<>(Arrays.asList(
            "aluminium", "aluminum", "metall", "metal", "dose", "can", "tin",
            "blech", "eisen", "iron"
    ));

    /**
     * Keywords that indicate glass packaging
     */
    public static final Set<String> GLASS_KEYWORDS = new HashSet<>(Arrays.asList(
            "glass", "glas", "flasche", "bottle", "jar", "glasflasche"
    ));

    /**
     * Keywords that indicate paper/cardboard packaging
     */
    public static final Set<String> PAPER_KEYWORDS = new HashSet<>(Arrays.asList(
            "paper", "papier", "cardboard", "pappe", "karton", "carton", "pap",
            "verpackung", "packaging"
    ));

    /**
     * Keywords that indicate organic/biodegradable materials
     */
    public static final Set<String> ORGANIC_KEYWORDS = new HashSet<>(Arrays.asList(
            "bio", "organic", "biologisch", "kompostierbar", "compostable",
            "biodegradable", "verrottbar"
    ));

    /**
     * Keywords that indicate Pfand (deposit)
     */
    public static final Set<String> PFAND_KEYWORDS = new HashSet<>(Arrays.asList(
            "pfand", "deposit", "pfandflasche", "pfanddose"
    ));

    /**
     * Product categories that typically contain beverages
     */
    public static final Set<String> BEVERAGE_CATEGORIES = new HashSet<>(Arrays.asList(
            "getränk", "drink", "beverage", "soft drink", "soda", "limonade",
            "bier", "beer", "wein", "wine", "wasser", "water"
    ));

    /**
     * Product categories that typically contain organic/food items
     */
    public static final Set<String> FOOD_CATEGORIES = new HashSet<>(Arrays.asList(
            "obst", "fruit", "gemüse", "vegetable", "lebensmittel", "food",
            "nahrung", "nahrungsmittel"
    ));

    /**
     * Waste category IDs
     */
    public static class CategoryIds {
        public static final String PFAND = "pfand";
        public static final String GELBE_TONNE = "gelbe_tonne";
        public static final String GLAS = "glas";
        public static final String PAPIER = "papier";
        public static final String BIO = "bio";
        public static final String RESTMUELL = "restmuell";
    }

    /**
     * Check if a string contains any of the given keywords (case-insensitive)
     */
    public static boolean containsKeyword(String text, Set<String> keywords) {
        if (text == null || text.isEmpty()) {
            return false;
        }
        String lowerText = text.toLowerCase();
        for (String keyword : keywords) {
            if (lowerText.contains(keyword.toLowerCase())) {
                return true;
            }
        }
        return false;
    }
}
