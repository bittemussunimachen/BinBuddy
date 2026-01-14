package com.example.binbuddy.data.repository;

import android.content.Context;

import com.example.binbuddy.data.dao.ProductDao;
import com.example.binbuddy.data.entity.ProductEntity;
import com.example.binbuddy.data.mapper.ProductMapper;
import com.example.binbuddy.data.mapper.WasteCategoryMapper;
import com.example.binbuddy.data.remote.OpenFoodFactsApi;
import com.example.binbuddy.data.remote.model.ProductDto;
import com.example.binbuddy.data.remote.model.ProductResponse;
import com.example.binbuddy.data.remote.model.SearchResponse;
import com.example.binbuddy.data.util.NetworkChecker;
import com.example.binbuddy.domain.model.AppError;
import com.example.binbuddy.domain.model.Product;
import com.example.binbuddy.domain.model.Result;
import com.example.binbuddy.domain.repository.ProductRepository;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import kotlinx.coroutines.flow.Flow;
import kotlinx.coroutines.flow.FlowKt;
import kotlinx.coroutines.flow.MutableStateFlow;
import kotlinx.coroutines.flow.StateFlow;
import retrofit2.Response;

/**
 * Implementation of ProductRepository with comprehensive error handling and offline support.
 * Provides product data from cache → database → API → saves to database.
 * 
 * Strategy:
 * 1. Check in-memory cache (if implemented)
 * 2. Query database via ProductDao
 * 3. If not found and online, call API via OpenFoodFactsApi
 * 4. Save to database
 * 5. Return Result<Product> with proper error handling
 * 
 * Offline support:
 * - If offline, return cached data from database if available
 * - If no cached data and offline, return offline error
 * - Network errors are caught and cached data is returned if available
 */
public class ProductRepositoryImpl implements ProductRepository {

    private final ProductDao productDao;
    private final OpenFoodFactsApi apiService;
    private final ProductMapper productMapper;
    private final WasteCategoryMapper wasteCategoryMapper;
    private final NetworkChecker networkChecker;
    private final ExecutorService executorService;
    private final Context context;

    // In-memory cache (simple implementation, can be enhanced with LRU cache)
    private final java.util.Map<String, Product> memoryCache = new java.util.concurrent.ConcurrentHashMap<>();

    public ProductRepositoryImpl(
            Context context,
            ProductDao productDao,
            OpenFoodFactsApi apiService,
            ProductMapper productMapper,
            WasteCategoryMapper wasteCategoryMapper) {
        this.context = context.getApplicationContext();
        this.productDao = productDao;
        this.apiService = apiService;
        this.productMapper = productMapper;
        this.wasteCategoryMapper = wasteCategoryMapper;
        this.networkChecker = new NetworkChecker(context);
        this.executorService = Executors.newFixedThreadPool(2);
    }

    @Override
    public Flow<Result<Product>> getProduct(String barcode) {
        if (barcode == null || barcode.trim().isEmpty()) {
            MutableStateFlow<Result<Product>> errorFlow = new MutableStateFlow<>(
                Result.error(AppError.invalidInputError("Barcode cannot be empty"))
            );
            return errorFlow;
        }

        MutableStateFlow<Result<Product>> resultFlow = new MutableStateFlow<>(
            Result.success(null) // Initial loading state
        );

        executorService.execute(() -> {
            try {
                // 1. Check in-memory cache
                Product cachedProduct = memoryCache.get(barcode);
                if (cachedProduct != null) {
                    resultFlow.setValue(Result.successFromCache(cachedProduct));
                    return;
                }

                // 2. Check database cache
                ProductEntity entity = productDao.getProduct(barcode);
                if (entity != null) {
                    Product product = productMapper.toDomainFromEntity(entity, null);
                    if (product != null) {
                        // Store in memory cache
                        memoryCache.put(barcode, product);
                        
                        // If offline, return cached data with offline warning
                        if (!networkChecker.isConnected()) {
                            resultFlow.setValue(Result.offlineError(product, 
                                "Showing cached data - no internet connection"));
                        } else {
                            resultFlow.setValue(Result.successFromCache(product));
                        }
                        
                        // Still try to refresh in background if online
                        if (networkChecker.isConnected()) {
                            refreshProductFromApi(barcode, resultFlow);
                        }
                        return;
                    }
                }

                // 3. If not in cache and online, fetch from API
                if (networkChecker.isConnected()) {
                    fetchProductFromApi(barcode, resultFlow);
                } else {
                    // Offline and no cached data
                    resultFlow.setValue(Result.error(AppError.offlineError(
                        "No internet connection and no cached data available")));
                }
            } catch (Exception e) {
                android.util.Log.e("ProductRepositoryImpl", "Error getting product", e);
                resultFlow.setValue(Result.error(AppError.databaseError(
                    "Error accessing database", e)));
            }
        });

        return resultFlow;
    }

    /**
     * Fetch product from API and update cache.
     */
    private void fetchProductFromApi(String barcode, MutableStateFlow<Result<Product>> resultFlow) {
        executorService.execute(() -> {
            try {
                Response<ProductResponse> response = apiService.getProduct(barcode).execute();
                
                if (response.isSuccessful() && response.body() != null) {
                    ProductResponse productResponse = response.body();
                    
                    if (productResponse.getStatus() == 1 && productResponse.getProduct() != null) {
                        ProductDto dto = productResponse.getProduct();
                        Product product = productMapper.toDomain(dto, null);
                        
                        if (product != null) {
                            // Save to database cache
                            saveProductToDatabase(product);
                            
                            // Store in memory cache
                            memoryCache.put(barcode, product);
                            
                            resultFlow.setValue(Result.success(product));
                        } else {
                            resultFlow.setValue(Result.error(AppError.parseError(
                                "Failed to map product data", null)));
                        }
                    } else {
                        // Product not found (status != 1)
                        String statusVerbose = productResponse.getStatusVerbose();
                        resultFlow.setValue(Result.error(AppError.notFoundError(
                            statusVerbose != null ? statusVerbose : "Product not found")));
                    }
                } else {
                    // HTTP error
                    int statusCode = response.code();
                    if (statusCode == 404) {
                        resultFlow.setValue(Result.error(AppError.notFoundError(
                            "Product not found (HTTP 404)")));
                    } else if (statusCode >= 500) {
                        resultFlow.setValue(Result.error(AppError.serverError(statusCode,
                            "Server error: HTTP " + statusCode)));
                    } else {
                        resultFlow.setValue(Result.error(AppError.networkError(
                            "API request failed: HTTP " + statusCode, null)));
                    }
                }
            } catch (IOException e) {
                android.util.Log.e("ProductRepositoryImpl", "Network error fetching product", e);
                
                // Try to return cached data if available
                try {
                    ProductEntity entity = productDao.getProduct(barcode);
                    if (entity != null) {
                        Product product = productMapper.toDomainFromEntity(entity, null);
                        if (product != null) {
                            resultFlow.setValue(Result.offlineError(product,
                                "Network error - showing cached data"));
                            return;
                        }
                    }
                } catch (Exception dbException) {
                    android.util.Log.e("ProductRepositoryImpl", "Error getting cached product", dbException);
                }
                
                // No cached data available
                if (e instanceof java.net.SocketTimeoutException) {
                    resultFlow.setValue(Result.error(AppError.timeoutError(
                        "Request timed out", e)));
                } else {
                    resultFlow.setValue(Result.error(AppError.networkError(
                        "Network error: " + e.getMessage(), e)));
                }
            } catch (Exception e) {
                android.util.Log.e("ProductRepositoryImpl", "Unexpected error fetching product", e);
                resultFlow.setValue(Result.error(AppError.unknownError(
                    "Unexpected error: " + e.getMessage(), e)));
            }
        });
    }

    /**
     * Refresh product from API in background (non-blocking).
     */
    private void refreshProductFromApi(String barcode, MutableStateFlow<Result<Product>> resultFlow) {
        executorService.execute(() -> {
            try {
                Response<ProductResponse> response = apiService.getProduct(barcode).execute();
                
                if (response.isSuccessful() && response.body() != null) {
                    ProductResponse productResponse = response.body();
                    
                    if (productResponse.getStatus() == 1 && productResponse.getProduct() != null) {
                        ProductDto dto = productResponse.getProduct();
                        Product product = productMapper.toDomain(dto, null);
                        
                        if (product != null) {
                            // Update database cache
                            saveProductToDatabase(product);
                            
                            // Update memory cache
                            memoryCache.put(barcode, product);
                            
                            // Update flow with fresh data
                            resultFlow.setValue(Result.success(product));
                        }
                    }
                }
            } catch (Exception e) {
                // Silently fail background refresh - cached data is already shown
                android.util.Log.d("ProductRepositoryImpl", "Background refresh failed", e);
            }
        });
    }

    @Override
    public Flow<Result<List<Product>>> searchProducts(String query, boolean germanyOnly) {
        if (query == null || query.trim().isEmpty()) {
            MutableStateFlow<Result<List<Product>>> errorFlow = new MutableStateFlow<>(
                Result.error(AppError.invalidInputError("Search query cannot be empty"))
            );
            return errorFlow;
        }

        MutableStateFlow<Result<List<Product>>> resultFlow = new MutableStateFlow<>(
            Result.success(Collections.emptyList()) // Initial loading state
        );

        executorService.execute(() -> {
            try {
                // If offline, try to get cached search results from database
                if (!networkChecker.isConnected()) {
                    List<ProductEntity> entities = productDao.searchProducts(query);
                    if (entities != null && !entities.isEmpty()) {
                        List<Product> products = new ArrayList<>();
                        for (ProductEntity entity : entities) {
                            Product product = productMapper.toDomainFromEntity(entity, null);
                            if (product != null) {
                                products.add(product);
                            }
                        }
                        resultFlow.setValue(Result.offlineError(products,
                            "Showing cached search results - no internet connection"));
                    } else {
                        resultFlow.setValue(Result.error(AppError.offlineError(
                            "No internet connection and no cached search results")));
                    }
                    return;
                }

                // Online: fetch from API
                String countries = germanyOnly ? "Germany" : null;
                Response<SearchResponse> response = apiService.searchProducts(
                    query, countries, 20, 1, "process", 1
                ).execute();

                if (response.isSuccessful() && response.body() != null) {
                    SearchResponse searchResponse = response.body();
                    List<ProductDto> productDtos = searchResponse.getProducts();
                    
                    if (productDtos != null && !productDtos.isEmpty()) {
                        List<Product> products = new ArrayList<>();
                        for (ProductDto dto : productDtos) {
                            try {
                                Product product = productMapper.toDomain(dto, null);
                                if (product != null) {
                                    products.add(product);
                                    // Save to database cache
                                    saveProductToDatabase(product);
                                }
                            } catch (Exception e) {
                                android.util.Log.w("ProductRepositoryImpl", 
                                    "Failed to map product in search", e);
                            }
                        }
                        
                        resultFlow.setValue(Result.success(products));
                    } else {
                        resultFlow.setValue(Result.success(Collections.emptyList()));
                    }
                } else {
                    int statusCode = response.code();
                    if (statusCode >= 500) {
                        resultFlow.setValue(Result.error(AppError.serverError(statusCode,
                            "Server error: HTTP " + statusCode)));
                    } else {
                        resultFlow.setValue(Result.error(AppError.networkError(
                            "Search request failed: HTTP " + statusCode, null)));
                    }
                }
            } catch (IOException e) {
                android.util.Log.e("ProductRepositoryImpl", "Network error searching products", e);
                
                // Try to return cached search results
                try {
                    List<ProductEntity> entities = productDao.searchProducts(query);
                    if (entities != null && !entities.isEmpty()) {
                        List<Product> products = new ArrayList<>();
                        for (ProductEntity entity : entities) {
                            Product product = productMapper.toDomainFromEntity(entity, null);
                            if (product != null) {
                                products.add(product);
                            }
                        }
                        resultFlow.setValue(Result.offlineError(products,
                            "Network error - showing cached search results"));
                        return;
                    }
                } catch (Exception dbException) {
                    android.util.Log.e("ProductRepositoryImpl", "Error getting cached search", dbException);
                }
                
                if (e instanceof java.net.SocketTimeoutException) {
                    resultFlow.setValue(Result.error(AppError.timeoutError(
                        "Search request timed out", e)));
                } else {
                    resultFlow.setValue(Result.error(AppError.networkError(
                        "Network error: " + e.getMessage(), e)));
                }
            } catch (Exception e) {
                android.util.Log.e("ProductRepositoryImpl", "Unexpected error searching products", e);
                resultFlow.setValue(Result.error(AppError.unknownError(
                    "Unexpected error: " + e.getMessage(), e)));
            }
        });

        return resultFlow;
    }

    @Override
    public void saveProduct(Product product) {
        if (product == null) {
            return;
        }

        executorService.execute(() -> {
            try {
                saveProductToDatabase(product);
                
                // Update memory cache
                if (product.getBarcode() != null) {
                    memoryCache.put(product.getBarcode(), product);
                }
            } catch (Exception e) {
                android.util.Log.e("ProductRepositoryImpl", "Error saving product", e);
            }
        });
    }

    /**
     * Save product to database (internal method with error handling).
     */
    private void saveProductToDatabase(Product product) {
        try {
            ProductEntity entity = (ProductEntity) productMapper.toEntity(product);
            if (entity != null) {
                entity.updatedAt = System.currentTimeMillis();
                productDao.insertProduct(entity);
            }
        } catch (Exception e) {
            android.util.Log.e("ProductRepositoryImpl", "Error saving product to database", e);
            throw new RuntimeException("Failed to save product to database", e);
        }
    }

    @Override
    public Flow<Result<List<Product>>> getProductsByWasteCategory(String wasteCategoryId) {
        if (wasteCategoryId == null || wasteCategoryId.trim().isEmpty()) {
            MutableStateFlow<Result<List<Product>>> errorFlow = new MutableStateFlow<>(
                Result.error(AppError.invalidInputError("Waste category ID cannot be empty"))
            );
            return errorFlow;
        }

        MutableStateFlow<Result<List<Product>>> resultFlow = new MutableStateFlow<>(
            Result.success(Collections.emptyList()) // Initial loading state
        );

        executorService.execute(() -> {
            try {
                List<ProductEntity> entities = productDao.getProductsByWasteCategory(wasteCategoryId);
                
                if (entities != null && !entities.isEmpty()) {
                    List<Product> products = new ArrayList<>();
                    for (ProductEntity entity : entities) {
                        try {
                            Product product = productMapper.toDomainFromEntity(entity, null);
                            if (product != null) {
                                products.add(product);
                            }
                        } catch (Exception e) {
                            android.util.Log.w("ProductRepositoryImpl", 
                                "Failed to map product", e);
                        }
                    }
                    resultFlow.setValue(Result.success(products));
                } else {
                    resultFlow.setValue(Result.success(Collections.emptyList()));
                }
            } catch (Exception e) {
                android.util.Log.e("ProductRepositoryImpl", 
                    "Error getting products by waste category", e);
                resultFlow.setValue(Result.error(AppError.databaseError(
                    "Error querying database", e)));
            }
        });

        return resultFlow;
    }

    /**
     * Clear in-memory cache (useful for testing or memory management).
     */
    public void clearMemoryCache() {
        memoryCache.clear();
    }

    /**
     * Cleanup resources.
     */
    public void shutdown() {
        if (executorService != null && !executorService.isShutdown()) {
            executorService.shutdown();
        }
        memoryCache.clear();
    }
}
