package com.example.binbuddy.data.mapper;

import com.example.binbuddy.data.remote.model.IngredientDto;
import com.example.binbuddy.data.remote.model.ProductDto;
import com.example.binbuddy.domain.model.Product;
import com.example.binbuddy.domain.model.WasteCategory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Mapper class to convert between Product DTOs/Entities and Domain models.
 * This will be fully implemented once ProductDto and ProductEntity are created.
 */
public class ProductMapper {

    /**
     * Convert ProductDto to Product domain model.
     * 
     * @param dto ProductDto from API response
     * @param wasteCategory WasteCategory for the product (can be null)
     * @return Product domain model
     */
    public Product toDomain(ProductDto dto, WasteCategory wasteCategory) {
        if (dto == null) {
            return null;
        }
        
        Product product = new Product();
        
        // Map basic fields
        product.setBarcode(dto.getBarcode());
        product.setName(dto.getProductName());
        product.setBrand(dto.getBrands());
        product.setPackaging(dto.getPackaging());
        product.setQuantity(dto.getQuantity());
        product.setLabels(dto.getLabels());
        product.setGenericName(dto.getGenericName());
        product.setImageUrl(dto.getImageUrl());
        
        // Parse categories string into list
        product.setCategories(parseCategories(dto.getCategories()));
        
        // Convert ingredients from List<IngredientDto> to List<String>
        List<String> ingredientsList = new ArrayList<>();
        if (dto.getIngredients() != null) {
            for (IngredientDto ingredientDto : dto.getIngredients()) {
                if (ingredientDto != null && ingredientDto.getText() != null) {
                    ingredientsList.add(ingredientDto.getText());
                }
            }
        }
        product.setIngredients(ingredientsList);
        
        // Set ID to barcode if barcode is available
        if (dto.getBarcode() != null) {
            product.setId(dto.getBarcode());
        }
        
        // Note: ecoscoreGrade and ecoscoreScore are not in ProductDto,
        // so they will remain null unless set elsewhere
        
        return product;
    }

    /**
     * Convert ProductEntity to Product domain model.
     * This method will be implemented once ProductEntity is created.
     * 
     * @param entity ProductEntity from database
     * @param wasteCategory WasteCategory for the product (can be null)
     * @return Product domain model
     */
    public Product toDomainFromEntity(Object entity, WasteCategory wasteCategory) {
        // TODO: Implement once ProductEntity is created
        // This is a placeholder that will be replaced with actual mapping logic
        throw new UnsupportedOperationException("ProductEntity mapping not yet implemented");
    }

    /**
     * Convert Product domain model to ProductEntity.
     * This method will be implemented once ProductEntity is created.
     * 
     * @param product Product domain model
     * @return ProductEntity for database storage
     */
    public Object toEntity(Product product) {
        // TODO: Implement once ProductEntity is created
        // This is a placeholder that will be replaced with actual mapping logic
        throw new UnsupportedOperationException("ProductEntity mapping not yet implemented");
    }

    /**
     * Convert list of ProductDto to list of Product domain models.
     * 
     * @param dtos List of ProductDto
     * @param wasteCategoryMapper Mapper for waste categories
     * @return List of Product domain models
     */
    public List<Product> toDomainList(List<?> dtos, WasteCategoryMapper wasteCategoryMapper) {
        if (dtos == null || dtos.isEmpty()) {
            return Collections.emptyList();
        }
        List<Product> products = new ArrayList<>();
        for (Object dto : dtos) {
            try {
                if (dto instanceof ProductDto) {
                    Product product = toDomain((ProductDto) dto, null);
                    if (product != null) {
                        products.add(product);
                    }
                }
            } catch (Exception e) {
                // Skip invalid products
                android.util.Log.w("ProductMapper", "Failed to map product", e);
            }
        }
        return products;
    }

    /**
     * Helper method to parse categories string into list.
     * Categories are typically comma-separated or semicolon-separated.
     * 
     * @param categoriesString Categories as string
     * @return List of category strings
     */
    protected List<String> parseCategories(String categoriesString) {
        if (categoriesString == null || categoriesString.trim().isEmpty()) {
            return Collections.emptyList();
        }
        List<String> categories = new ArrayList<>();
        String[] parts = categoriesString.split("[,;]");
        for (String part : parts) {
            String trimmed = part.trim();
            if (!trimmed.isEmpty()) {
                categories.add(trimmed);
            }
        }
        return categories;
    }

    /**
     * Helper method to parse ingredients list from various formats.
     * 
     * @param ingredients Ingredients as list or string
     * @return List of ingredient strings
     */
    protected List<String> parseIngredients(Object ingredients) {
        if (ingredients == null) {
            return Collections.emptyList();
        }
        List<String> result = new ArrayList<>();
        if (ingredients instanceof List) {
            for (Object item : (List<?>) ingredients) {
                if (item != null) {
                    result.add(item.toString());
                }
            }
        } else if (ingredients instanceof String) {
            String[] parts = ((String) ingredients).split("[,;]");
            for (String part : parts) {
                String trimmed = part.trim();
                if (!trimmed.isEmpty()) {
                    result.add(trimmed);
                }
            }
        }
        return result;
    }
}
