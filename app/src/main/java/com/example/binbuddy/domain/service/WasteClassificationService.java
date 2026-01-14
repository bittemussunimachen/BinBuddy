package com.example.binbuddy.domain.service;

import com.example.binbuddy.domain.model.Product;
import com.example.binbuddy.domain.model.WasteCategory;

import java.util.List;
import java.util.Locale;

/**
 * Service for determining the waste category of a product based on its properties.
 * Uses classification rules to map product data to appropriate waste categories.
 */
public class WasteClassificationService {

    /**
     * Determine the waste category for a given product.
     * 
     * @param product The product to classify
     * @return The appropriate WasteCategory, or null if classification fails
     */
    public WasteCategory determineWasteCategory(Product product) {
        if (product == null) {
            return getDefaultCategory();
        }

        // Check for Pfand first (deposit bottles)
        WasteCategory pfandCategory = checkPfand(product);
        if (pfandCategory != null) {
            return pfandCategory;
        }

        // Check packaging type
        WasteCategory packagingCategory = checkPackaging(product);
        if (packagingCategory != null) {
            return packagingCategory;
        }

        // Check categories
        WasteCategory categoryMatch = checkCategories(product);
        if (categoryMatch != null) {
            return categoryMatch;
        }

        // Check labels
        WasteCategory labelMatch = checkLabels(product);
        if (labelMatch != null) {
            return labelMatch;
        }

        // Default to residual waste
        return getDefaultCategory();
    }

    /**
     * Check if product has Pfand (deposit)
     */
    private WasteCategory checkPfand(Product product) {
        String packaging = product.getPackaging() != null 
                ? product.getPackaging().toLowerCase(Locale.getDefault()) 
                : "";
        String labels = product.getLabels() != null 
                ? product.getLabels().toLowerCase(Locale.getDefault()) 
                : "";

        // Check for Pfand indicators
        if (packaging.contains("pfand") || 
            packaging.contains("deposit") ||
            labels.contains("pfand") ||
            labels.contains("deposit")) {
            return createPfandCategory();
        }

        // Check barcode patterns for Pfand (common German Pfand barcodes start with specific prefixes)
        String barcode = product.getBarcode();
        if (barcode != null && barcode.length() >= 3) {
            String prefix = barcode.substring(0, 3);
            // Common Pfand bottle prefixes (this is a simplified check)
            if (prefix.equals("400") || prefix.equals("401") || prefix.equals("402")) {
                // Additional check: Pfand bottles are usually in specific categories
                List<String> categories = product.getCategories();
                if (categories != null) {
                    for (String category : categories) {
                        String catLower = category.toLowerCase(Locale.getDefault());
                        if (catLower.contains("getränk") || 
                            catLower.contains("drink") ||
                            catLower.contains("beverage")) {
                            return createPfandCategory();
                        }
                    }
                }
            }
        }

        return null;
    }

    /**
     * Check packaging type for waste category
     */
    private WasteCategory checkPackaging(Product product) {
        String packaging = product.getPackaging() != null 
                ? product.getPackaging().toLowerCase(Locale.getDefault()) 
                : "";

        if (packaging.isEmpty()) {
            return null;
        }

        // Plastic packaging -> Gelbe Tonne (Yellow bin)
        if (packaging.contains("plastic") || 
            packaging.contains("kunststoff") ||
            packaging.contains("pet") ||
            packaging.contains("pe") ||
            packaging.contains("pp") ||
            packaging.contains("ps") ||
            packaging.contains("pvc") ||
            packaging.contains("aluminium") ||
            packaging.contains("aluminum") ||
            packaging.contains("metall") ||
            packaging.contains("metal") ||
            packaging.contains("dose") ||
            packaging.contains("can") ||
            packaging.contains("tetra") ||
            packaging.contains("karton") && packaging.contains("getränk")) {
            return createGelbeTonneCategory();
        }

        // Glass -> Glas (Glass bin, separate by color)
        if (packaging.contains("glass") || 
            packaging.contains("glas") ||
            packaging.contains("flasche") && packaging.contains("glas")) {
            return createGlassCategory();
        }

        // Paper/Cardboard -> Papier
        if (packaging.contains("paper") || 
            packaging.contains("papier") ||
            packaging.contains("cardboard") ||
            packaging.contains("pappe") ||
            packaging.contains("karton") ||
            packaging.contains("carton")) {
            return createPaperCategory();
        }

        // Organic/Biodegradable -> Bio
        if (packaging.contains("bio") ||
            packaging.contains("organic") ||
            packaging.contains("biologisch") ||
            packaging.contains("kompostierbar") ||
            packaging.contains("compostable")) {
            return createBioCategory();
        }

        return null;
    }

    /**
     * Check product categories for waste classification
     */
    private WasteCategory checkCategories(Product product) {
        List<String> categories = product.getCategories();
        if (categories == null || categories.isEmpty()) {
            return null;
        }

        for (String category : categories) {
            String catLower = category.toLowerCase(Locale.getDefault());

            // Organic/Bio products
            if (catLower.contains("bio") ||
                catLower.contains("organic") ||
                catLower.contains("obst") ||
                catLower.contains("gemüse") ||
                catLower.contains("fruit") ||
                catLower.contains("vegetable")) {
                // Only if packaging is also organic/biodegradable
                String packaging = product.getPackaging() != null 
                        ? product.getPackaging().toLowerCase(Locale.getDefault()) 
                        : "";
                if (packaging.contains("bio") || packaging.contains("organic")) {
                    return createBioCategory();
                }
            }

            // Beverages in glass bottles
            if ((catLower.contains("getränk") || 
                 catLower.contains("drink") ||
                 catLower.contains("beverage")) &&
                product.getPackaging() != null &&
                product.getPackaging().toLowerCase(Locale.getDefault()).contains("glas")) {
                return createGlassCategory();
            }
        }

        return null;
    }

    /**
     * Check labels for waste classification hints
     */
    private WasteCategory checkLabels(Product product) {
        String labels = product.getLabels() != null 
                ? product.getLabels().toLowerCase(Locale.getDefault()) 
                : "";

        if (labels.isEmpty()) {
            return null;
        }

        // Bio/Organic labels
        if (labels.contains("bio") ||
            labels.contains("organic") ||
            labels.contains("biologisch")) {
            return createBioCategory();
        }

        // Recycling labels
        if (labels.contains("recycling") ||
            labels.contains("recycelbar") ||
            labels.contains("recyclable")) {
            // Could be Gelbe Tonne, but need more context
            // Default to Gelbe Tonne for recyclable items
            return createGelbeTonneCategory();
        }

        return null;
    }

    /**
     * Create Pfand category
     */
    private WasteCategory createPfandCategory() {
        return new WasteCategory.Builder()
                .setId("pfand")
                .setNameDe("Pfand")
                .setNameEn("Deposit")
                .setDescriptionDe("Dieses Produkt hat Pfand. Bitte zurückgeben.")
                .setDescriptionEn("This product has a deposit. Please return it.")
                .setIconName("ic_pfand")
                .setColorHex("#FF9800")
                .build();
    }

    /**
     * Create Gelbe Tonne (Yellow bin) category
     */
    private WasteCategory createGelbeTonneCategory() {
        return new WasteCategory.Builder()
                .setId("gelbe_tonne")
                .setNameDe("Gelbe Tonne")
                .setNameEn("Yellow Bin")
                .setDescriptionDe("Verpackungen aus Kunststoff, Metall oder Verbundstoffen gehören in die Gelbe Tonne.")
                .setDescriptionEn("Packaging made of plastic, metal or composite materials belongs in the yellow bin.")
                .setIconName("ic_gelbe_tonne")
                .setColorHex("#FFEB3B")
                .build();
    }

    /**
     * Create Glass category
     */
    private WasteCategory createGlassCategory() {
        return new WasteCategory.Builder()
                .setId("glas")
                .setNameDe("Glas")
                .setNameEn("Glass")
                .setDescriptionDe("Glasflaschen und -behälter gehören in den Glascontainer. Bitte nach Farben trennen.")
                .setDescriptionEn("Glass bottles and containers belong in the glass container. Please separate by color.")
                .setIconName("ic_glas")
                .setColorHex("#2196F3")
                .build();
    }

    /**
     * Create Paper category
     */
    private WasteCategory createPaperCategory() {
        return new WasteCategory.Builder()
                .setId("papier")
                .setNameDe("Papier")
                .setNameEn("Paper")
                .setDescriptionDe("Papier und Pappe gehören in die Papiertonne oder den Altpapiercontainer.")
                .setDescriptionEn("Paper and cardboard belong in the paper bin or paper recycling container.")
                .setIconName("ic_papier")
                .setColorHex("#4CAF50")
                .build();
    }

    /**
     * Create Bio (Organic) category
     */
    private WasteCategory createBioCategory() {
        return new WasteCategory.Builder()
                .setId("bio")
                .setNameDe("Bio")
                .setNameEn("Organic")
                .setDescriptionDe("Biologisch abbaubare Abfälle gehören in die Biotonne.")
                .setDescriptionEn("Biodegradable waste belongs in the organic waste bin.")
                .setIconName("ic_bio")
                .setColorHex("#8BC34A")
                .build();
    }

    /**
     * Get default category (Restmüll - Residual waste)
     */
    private WasteCategory getDefaultCategory() {
        return new WasteCategory.Builder()
                .setId("restmuell")
                .setNameDe("Restmüll")
                .setNameEn("Residual Waste")
                .setDescriptionDe("Nicht recycelbare Abfälle gehören in die Restmülltonne.")
                .setDescriptionEn("Non-recyclable waste belongs in the residual waste bin.")
                .setIconName("ic_restmuell")
                .setColorHex("#757575")
                .build();
    }
}
