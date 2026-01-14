package com.example.binbuddy.domain.service;

import com.example.binbuddy.domain.model.PfandInfo;
import com.example.binbuddy.domain.model.Product;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Service for detecting Pfand (deposit) information for products.
 */
public class PfandService {
    
    // Common Pfand barcode prefixes (German market)
    private static final List<String> PFAND_BARCODE_PREFIXES = Arrays.asList(
        "400", "401", "402", "403", "404", "405", "406", "407", "408", "409",
        "410", "411", "412", "413", "414", "415", "416", "417", "418", "419",
        "420", "421", "422", "423", "424", "425", "426", "427", "428", "429",
        "430", "431", "432", "433", "434", "435", "436", "437", "438", "439"
    );

    // Pfand indicators in product categories
    private static final List<String> PFAND_CATEGORIES = Arrays.asList(
        "beers", "bier", "soft drinks", "softdrinks", "soft-drinks",
        "beverages", "getränke", "drinks", "carbonated drinks"
    );

    // Pfand indicators in packaging
    private static final List<String> PFAND_PACKAGING = Arrays.asList(
        "bottle", "flasche", "can", "dose", "pfand", "deposit",
        "einweg", "mehrweg", "returnable"
    );

    // Pfand symbols/indicators in labels
    private static final List<String> PFAND_LABELS = Arrays.asList(
        "pfand", "deposit", "pfandpflichtig", "pfandzeichen",
        "einwegpfand", "mehrwegpfand"
    );

    /**
     * Check if a product has Pfand (deposit).
     * 
     * @param product Product to check
     * @return PfandInfo with detection result
     */
    public PfandInfo checkPfand(Product product) {
        if (product == null || product.getBarcode() == null) {
            return new PfandInfo(false, null, new ArrayList<>());
        }

        boolean hasPfand = false;
        Double amount = null;

        // Check barcode prefix (German EAN codes)
        String barcode = product.getBarcode();
        if (barcode.length() >= 3) {
            String prefix = barcode.substring(0, 3);
            if (PFAND_BARCODE_PREFIXES.contains(prefix)) {
                hasPfand = true;
            }
        }

        // Check categories
        if (!hasPfand && product.getCategories() != null) {
            for (String category : product.getCategories()) {
                String lowerCategory = category.toLowerCase();
                for (String pfandCategory : PFAND_CATEGORIES) {
                    if (lowerCategory.contains(pfandCategory)) {
                        hasPfand = true;
                        break;
                    }
                }
                if (hasPfand) break;
            }
        }

        // Check packaging
        if (!hasPfand && product.getPackaging() != null) {
            String lowerPackaging = product.getPackaging().toLowerCase();
            for (String pfandPackaging : PFAND_PACKAGING) {
                if (lowerPackaging.contains(pfandPackaging)) {
                    hasPfand = true;
                    break;
                }
            }
        }

        // Check labels
        if (!hasPfand && product.getLabels() != null) {
            String lowerLabels = product.getLabels().toLowerCase();
            for (String pfandLabel : PFAND_LABELS) {
                if (lowerLabels.contains(pfandLabel)) {
                    hasPfand = true;
                    break;
                }
            }
        }

        // Determine Pfand amount based on product characteristics
        if (hasPfand) {
            amount = determinePfandAmount(product);
        }

        // Return locations would be fetched from a location service
        // For now, return empty list
        List<String> returnLocations = new ArrayList<>();

        return new PfandInfo(hasPfand, amount, returnLocations);
    }

    /**
     * Determine Pfand amount based on product characteristics.
     * 
     * @param product Product to analyze
     * @return Pfand amount in EUR, or null if cannot be determined
     */
    private Double determinePfandAmount(Product product) {
        // Default amounts in Germany:
        // - 0.08 EUR: Einweg (single-use) bottles and cans
        // - 0.15 EUR: Mehrweg (reusable) beer bottles
        // - 0.25 EUR: Mehrweg (reusable) soft drink bottles

        String packaging = product.getPackaging() != null ? product.getPackaging().toLowerCase() : "";
        String categories = product.getCategories() != null ? String.join(", ", product.getCategories()).toLowerCase() : "";

        // Check for Mehrweg (reusable)
        if (packaging.contains("mehrweg") || packaging.contains("reusable")) {
            // Check if it's beer
            if (categories.contains("beer") || categories.contains("bier") || 
                product.getName() != null && product.getName().toLowerCase().contains("bier")) {
                return 0.15; // Beer bottles
            } else {
                return 0.25; // Soft drink bottles
            }
        }

        // Check for Einweg (single-use) or cans
        if (packaging.contains("einweg") || packaging.contains("can") || 
            packaging.contains("dose") || packaging.contains("single-use")) {
            return 0.08; // Single-use bottles and cans
        }

        // Default to Einweg amount if Pfand is detected but type unclear
        return 0.08;
    }

    /**
     * Check if a barcode pattern matches Pfand products.
     * 
     * @param barcode Barcode to check
     * @return true if barcode matches Pfand pattern
     */
    public boolean isPfandBarcode(String barcode) {
        if (barcode == null || barcode.length() < 3) {
            return false;
        }
        String prefix = barcode.substring(0, 3);
        return PFAND_BARCODE_PREFIXES.contains(prefix);
    }

    /**
     * Get Pfand amount for a specific barcode (if known).
     * 
     * @param barcode Barcode to check
     * @return Pfand amount or null if not found
     */
    public Double getPfandAmountForBarcode(String barcode) {
        // This could be extended with a database lookup
        // For now, return null (will be determined by checkPfand)
        return null;
    }
}
