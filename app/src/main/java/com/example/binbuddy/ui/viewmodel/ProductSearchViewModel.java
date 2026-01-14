package com.example.binbuddy.ui.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.binbuddy.domain.model.AppError;
import com.example.binbuddy.domain.model.Product;
import com.example.binbuddy.domain.model.Result;
import com.example.binbuddy.domain.repository.ProductRepository;
import com.example.binbuddy.util.FlowCollector;
import kotlin.Unit;
import java.util.concurrent.CancellationException;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;
import kotlinx.coroutines.flow.Flow;

/**
 * ViewModel for product search functionality.
 * Uses ProductRepository to search for products via OpenFoodFacts API.
 */
@HiltViewModel
public class ProductSearchViewModel extends AndroidViewModel {
    
    private final ProductRepository productRepository;
    private FlowCollector<Result<List<Product>>> currentCollector;
    
    private final MutableLiveData<List<Product>> searchResults = new MutableLiveData<>(new ArrayList<>());
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>(false);
    private final MutableLiveData<String> error = new MutableLiveData<>();

    @Inject
    public ProductSearchViewModel(@NonNull Application application, ProductRepository productRepository) {
        super(application);
        this.productRepository = productRepository;
    }

    public LiveData<List<Product>> getSearchResults() {
        return searchResults;
    }

    public LiveData<Boolean> getIsLoading() {
        return isLoading;
    }

    public LiveData<String> getError() {
        return error;
    }

    /**
     * Search for products using the repository.
     * @param query Search query string
     * @param germanyOnly Whether to filter results to Germany only
     */
    public void searchProducts(String query, boolean germanyOnly) {
        if (query == null || query.trim().isEmpty()) {
            error.setValue("Suchbegriff eingeben");
            return;
        }

        // Cancel previous search if any
        if (currentCollector != null) {
            currentCollector.cancel();
        }

        isLoading.setValue(true);
        error.setValue(null);
        searchResults.setValue(new ArrayList<>());
        
        // Use repository to search products
        Flow<Result<List<Product>>> searchFlow = productRepository.searchProducts(query.trim(), germanyOnly);
        
        // Collect Flow values and update LiveData
        currentCollector = new FlowCollector<>(
            searchFlow,
            result -> {
                handleSearchResult(result);
                return Unit.INSTANCE;
            },
            throwable -> {
                if (throwable instanceof CancellationException) {
                    // Ignore expected cancellations when new searches start or scope clears
                    return Unit.INSTANCE;
                }
                android.util.Log.e("ProductSearchViewModel", "Error collecting search flow", throwable);
                isLoading.postValue(false);
                error.postValue("Fehler bei der Suche: " + throwable.getMessage());
                return Unit.INSTANCE;
            }
        );
        
        currentCollector.start();
    }
    
    private void handleSearchResult(Result<List<Product>> result) {
        isLoading.postValue(false);
        
        if (result.isSuccess()) {
            List<Product> products = result.getData();
            if (products != null) {
                searchResults.postValue(products);
            } else {
                searchResults.postValue(new ArrayList<>());
            }
            
            // Check for offline warning (cached data)
            if (result.isFromCache()) {
                String message = result.getErrorMessage();
                if (message != null && !message.isEmpty()) {
                    // Show info message but still display results
                    android.util.Log.i("ProductSearchViewModel", message);
                }
            }
        } else {
            // Handle error
            AppError appError = result.getError();
            String errorMessage = "Fehler bei der Suche";
            
            if (appError != null) {
                AppError.ErrorType errorType = appError.getErrorType();
                if (errorType == AppError.ErrorType.NETWORK_ERROR) {
                    errorMessage = "Netzwerkfehler. Bitte überprüfen Sie Ihre Internetverbindung.";
                } else if (errorType == AppError.ErrorType.OFFLINE_ERROR) {
                    errorMessage = "Keine Internetverbindung. Bitte versuchen Sie es später erneut.";
                } else if (errorType == AppError.ErrorType.TIMEOUT_ERROR) {
                    errorMessage = "Zeitüberschreitung. Bitte versuchen Sie es erneut.";
                } else if (errorType == AppError.ErrorType.SERVER_ERROR) {
                    errorMessage = "Serverfehler. Bitte versuchen Sie es später erneut.";
                } else {
                    // Use user-friendly message from AppError
                    String userMessage = appError.getUserMessage();
                    if (userMessage != null && !userMessage.isEmpty()) {
                        errorMessage = userMessage;
                    } else {
                        String technicalMessage = appError.getTechnicalMessage();
                        if (technicalMessage != null && !technicalMessage.isEmpty()) {
                            errorMessage = technicalMessage;
                        }
                    }
                }
            }
            
            error.postValue(errorMessage);
            searchResults.postValue(new ArrayList<>());
        }
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        if (currentCollector != null) {
            currentCollector.cancel();
        }
    }
}
