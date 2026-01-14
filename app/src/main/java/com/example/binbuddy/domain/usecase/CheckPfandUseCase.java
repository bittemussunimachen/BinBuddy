package com.example.binbuddy.domain.usecase;

import com.example.binbuddy.domain.model.PfandInfo;
import com.example.binbuddy.domain.model.Product;
import com.example.binbuddy.domain.service.PfandService;

/**
 * Use case for checking Pfand (deposit) information for a product.
 * Encapsulates the business logic for Pfand detection.
 */
public class CheckPfandUseCase {

    private final PfandService pfandService;

    public CheckPfandUseCase(PfandService pfandService) {
        this.pfandService = pfandService;
    }

    /**
     * Execute the use case to check Pfand information for a product.
     * 
     * @param product Product to check
     * @return PfandInfo containing Pfand status and amount
     */
    public PfandInfo execute(Product product) {
        if (product == null) {
            android.util.Log.w("CheckPfandUseCase", "Attempted to check Pfand for null product");
            return new PfandInfo(false, null, null);
        }

        // Use PfandService to check Pfand
        return pfandService.checkPfand(product);
    }
}
