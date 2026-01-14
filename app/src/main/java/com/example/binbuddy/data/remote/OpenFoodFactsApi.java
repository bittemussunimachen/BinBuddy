package com.example.binbuddy.data.remote;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Retrofit interface for Open Food Facts API.
 */
public interface OpenFoodFactsApi {

    /**
     * Get product by barcode.
     * 
     * @param barcode Product barcode
     * @return ProductResponse containing product data
     */
    @GET("api/v0/product/{barcode}.json")
    Call<com.example.binbuddy.data.remote.model.ProductResponse> getProduct(
            @retrofit2.http.Path("barcode") String barcode
    );

    /**
     * Search for products.
     * 
     * @param searchTerms Search query
     * @param countries Country filter (e.g., "Germany")
     * @param pageSize Number of results per page
     * @param page Page number
     * @return SearchResponse containing list of products
     */
    @GET("cgi/search.pl")
    Call<com.example.binbuddy.data.remote.model.SearchResponse> searchProducts(
            @Query("search_terms") String searchTerms,
            @Query("countries") String countries,
            @Query("page_size") int pageSize,
            @Query("page") int page,
            @Query("action") String action,
            @Query("json") int json
    );
}
