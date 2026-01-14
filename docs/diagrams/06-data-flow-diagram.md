# Data Flow Diagram

## Current Data Flow (No Persistence)

```mermaid
flowchart LR
    subgraph "User Input"
        SCAN[Barcode Scan]
        SEARCH[Text Search]
    end
    
    subgraph "Processing"
        PARSE[Parse Barcode/Search]
        VALIDATE[Validate Input]
    end
    
    subgraph "Network"
        HTTP[HttpURLConnection]
        JSON[JSON Parsing]
    end
    
    subgraph "External"
        API[Open Food Facts API]
    end
    
    subgraph "Display"
        TOAST[Toast Message]
        SNACKBAR[Snackbar]
        LIST[RecyclerView List]
    end
    
    SCAN --> PARSE
    SEARCH --> PARSE
    PARSE --> VALIDATE
    VALIDATE --> HTTP
    HTTP --> API
    API --> JSON
    JSON --> TOAST
    JSON --> SNACKBAR
    JSON --> LIST
    
    style SCAN fill:#4CAF50,color:#fff
    style SEARCH fill:#4CAF50,color:#fff
    style API fill:#00BCD4,color:#fff
    style TOAST fill:#FF9800,color:#fff
    style SNACKBAR fill:#FF9800,color:#fff
    style LIST fill:#FF9800,color:#fff
```

## Proposed Data Flow (With Persistence)

```mermaid
flowchart TB
    subgraph "User Input"
        SCAN[Barcode Scan]
        SEARCH[Text Search]
        VIEW[View History]
    end
    
    subgraph "UI Layer"
        ACTIVITY[Activity]
        VIEWMODEL[ViewModel]
    end
    
    subgraph "Business Logic"
        USECASE[Use Case]
    end
    
    subgraph "Data Layer"
        REPO[Repository]
        CACHE[Memory Cache]
        DB[(Room Database)]
        API_SERVICE[API Service]
    end
    
    subgraph "External"
        API[Open Food Facts API]
    end
    
    subgraph "Display"
        DETAIL[Product Detail Screen]
        LIST[Product List]
        HISTORY[Scan History]
    end
    
    SCAN --> ACTIVITY
    SEARCH --> ACTIVITY
    VIEW --> ACTIVITY
    
    ACTIVITY --> VIEWMODEL
    VIEWMODEL --> USECASE
    USECASE --> REPO
    
    REPO --> CACHE
    REPO --> DB
    REPO --> API_SERVICE
    
    API_SERVICE --> API
    API --> API_SERVICE
    
    CACHE --> VIEWMODEL
    DB --> VIEWMODEL
    API_SERVICE --> VIEWMODEL
    
    VIEWMODEL --> DETAIL
    VIEWMODEL --> LIST
    VIEWMODEL --> HISTORY
    
    style SCAN fill:#4CAF50,color:#fff
    style SEARCH fill:#4CAF50,color:#fff
    style VIEW fill:#4CAF50,color:#fff
    style REPO fill:#2196F3,color:#fff
    style CACHE fill:#FF9800,color:#fff
    style DB fill:#9C27B0,color:#fff
    style API fill:#00BCD4,color:#fff
    style DETAIL fill:#4CAF50,color:#fff
    style LIST fill:#4CAF50,color:#fff
    style HISTORY fill:#4CAF50,color:#fff
```

## Data Flow Scenarios

### Scenario 1: First-Time Product Scan
```mermaid
sequenceDiagram
    participant User
    participant ViewModel
    participant Repository
    participant Cache
    participant Database
    participant API

    User->>ViewModel: Scan barcode "1234567890"
    ViewModel->>Repository: getProduct("1234567890")
    Repository->>Cache: Check cache
    Cache-->>Repository: Cache miss
    Repository->>Database: Query product
    Database-->>Repository: Not found
    Repository->>API: GET /product/1234567890.json
    API-->>Repository: Product data
    Repository->>Database: Save product
    Repository->>Cache: Update cache
    Repository-->>ViewModel: Product
    ViewModel-->>User: Display product
```

### Scenario 2: Cached Product Lookup
```mermaid
sequenceDiagram
    participant User
    participant ViewModel
    participant Repository
    participant Cache
    participant Database
    participant API

    User->>ViewModel: Scan barcode "1234567890"
    ViewModel->>Repository: getProduct("1234567890")
    Repository->>Cache: Check cache
    Cache-->>Repository: Cache hit
    Repository-->>ViewModel: Product (from cache)
    ViewModel-->>User: Display product (instant)
```

### Scenario 3: Offline Product Lookup
```mermaid
sequenceDiagram
    participant User
    participant ViewModel
    participant Repository
    participant Cache
    participant Database
    participant API

    User->>ViewModel: Scan barcode "1234567890"
    ViewModel->>Repository: getProduct("1234567890")
    Repository->>Cache: Check cache
    Cache-->>Repository: Cache miss
    Repository->>Database: Query product
    Database-->>Repository: Product found
    Repository->>Cache: Update cache
    Repository-->>ViewModel: Product (from database)
    ViewModel-->>User: Display product (offline)
```

## Data Transformation Points

1. **Barcode → Product ID**: Scanner extracts barcode, used as product identifier
2. **API Response → Domain Model**: JSON parsing converts API response to Product object
3. **Domain Model → UI Model**: Product transformed to display format
4. **Domain Model → Database Entity**: Product converted to Room Entity for storage
5. **Database Entity → Domain Model**: Stored entity converted back to domain model

## Data Storage Strategy

### Current
- **No Storage**: All data fetched fresh from API
- **No Caching**: Every request hits the network
- **No Offline Support**: Requires internet connection

### Proposed
- **Memory Cache**: Fast access for recently viewed products
- **Room Database**: Persistent storage for offline access
- **Cache Invalidation**: TTL-based cache expiration
- **Sync Strategy**: Background sync for cached data
