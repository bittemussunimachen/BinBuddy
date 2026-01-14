# Application Architecture Diagram

## Current Architecture (Activity-Based)

```mermaid
graph TB
    subgraph "Current Architecture"
        MA[MainActivity<br/>- UI Rendering<br/>- Product Lookup<br/>- Navigation]
        SA[ScannerActivity<br/>- Camera Control<br/>- Barcode Scanning<br/>- Result Return]
        PSA[ProductSearchActivity<br/>- Search UI<br/>- API Calls<br/>- Result Display]
        PA[ProductAdapter<br/>- RecyclerView Adapter]
        
        MA --> SA
        MA --> PSA
        PSA --> PA
        MA -->|HttpURLConnection| OFF[Open Food Facts API]
        PSA -->|HttpURLConnection| OFF
    end
    
    style MA fill:#4CAF50,color:#fff
    style SA fill:#FF9800,color:#fff
    style PSA fill:#2196F3,color:#fff
    style PA fill:#9C27B0,color:#fff
    style OFF fill:#00BCD4,color:#fff
```

## Proposed Architecture (MVVM)

```mermaid
graph TB
    subgraph "Proposed MVVM Architecture"
        subgraph "Presentation Layer"
            A1[MainActivity]
            A2[ScannerActivity]
            A3[ProductSearchActivity]
            A4[ProductDetailActivity]
        end
        
        subgraph "ViewModels"
            VM1[MainViewModel]
            VM2[ScannerViewModel]
            VM3[SearchViewModel]
            VM4[ProductDetailViewModel]
        end
        
        subgraph "Domain Layer"
            UC1[GetProductUseCase]
            UC2[SearchProductsUseCase]
            UC3[SaveScanHistoryUseCase]
            UC4[GetWasteCategoryUseCase]
        end
        
        subgraph "Data Layer"
            REPO[ProductRepository]
            DB[(Room Database)]
            API[OpenFoodFactsAPI]
        end
        
        A1 --> VM1
        A2 --> VM2
        A3 --> VM3
        A4 --> VM4
        
        VM1 --> UC1
        VM1 --> UC3
        VM2 --> UC1
        VM3 --> UC2
        VM4 --> UC1
        VM4 --> UC4
        
        UC1 --> REPO
        UC2 --> REPO
        UC3 --> REPO
        UC4 --> REPO
        
        REPO --> API
        REPO --> DB
    end
    
    style A1 fill:#4CAF50,color:#fff
    style A2 fill:#4CAF50,color:#fff
    style A3 fill:#4CAF50,color:#fff
    style A4 fill:#4CAF50,color:#fff
    style VM1 fill:#FF9800,color:#fff
    style VM2 fill:#FF9800,color:#fff
    style VM3 fill:#FF9800,color:#fff
    style VM4 fill:#FF9800,color:#fff
    style UC1 fill:#2196F3,color:#fff
    style UC2 fill:#2196F3,color:#fff
    style UC3 fill:#2196F3,color:#fff
    style UC4 fill:#2196F3,color:#fff
    style REPO fill:#9C27B0,color:#fff
    style DB fill:#00BCD4,color:#fff
    style API fill:#00BCD4,color:#fff
```

## Architecture Comparison

### Current (Activity-Based)
**Pros:**
- Simple and straightforward
- Fast to implement
- No additional dependencies

**Cons:**
- No separation of concerns
- Business logic mixed with UI
- Difficult to test
- State lost on configuration changes
- No data persistence layer

### Proposed (MVVM)
**Pros:**
- Clear separation of concerns
- Testable ViewModels
- Lifecycle-aware components
- State preservation
- Reusable Use Cases
- Centralized data access

**Cons:**
- More complex initial setup
- Requires learning curve
- Additional dependencies (ViewModel, LiveData/StateFlow)

## Migration Path
1. Add ViewModel dependencies
2. Extract business logic to ViewModels
3. Create Repository layer
4. Implement Room database
5. Create Use Cases for business rules
6. Migrate Activities to observe ViewModels
