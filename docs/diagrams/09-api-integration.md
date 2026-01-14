# API Integration Diagram

## Current API Integration

```mermaid
graph TB
    subgraph "BinBuddy App"
        MA[MainActivity]
        PSA[ProductSearchActivity]
        NET[HttpURLConnection]
    end
    
    subgraph "Open Food Facts API"
        PRODUCT_API["/api/v0/product/{barcode}.json"]
        SEARCH_API["/cgi/search.pl"]
    end
    
    MA -->|GET Request| PRODUCT_API
    PSA -->|GET Request| SEARCH_API
    PRODUCT_API -->|JSON Response| MA
    SEARCH_API -->|JSON Response| PSA
    
    style MA fill:#4CAF50,color:#fff
    style PSA fill:#4CAF50,color:#fff
    style PRODUCT_API fill:#00BCD4,color:#fff
    style SEARCH_API fill:#00BCD4,color:#fff
```

## Current API Endpoints

### 1. Product Lookup by Barcode

```http
GET https://world.openfoodfacts.org/api/v0/product/{barcode}.json

Request:
- barcode: EAN-13, EAN-8, UPC-A, UPC-E

Response:
{
  "status": 1,
  "product": {
    "product_name": "Coca-Cola",
    "brands": "Coca-Cola",
    "barcode": "5449000000996",
    "categories": "Beverages, Carbonated drinks",
    "packaging": "Plastic bottle",
    "quantity": "500ml",
    "ingredients": [...],
    "labels": "Organic",
    "generic_name": "Carbonated soft drink"
  }
}
```

### 2. Product Search

```http
GET https://world.openfoodfacts.org/cgi/search.pl?
    search_terms={query}&
    search_simple=1&
    action=process&
    json=1&
    page_size=25&
    countries=Germany

Request Parameters:
- search_terms: Product name or keyword
- countries: Optional filter (e.g., "Germany")
- page_size: Number of results (default: 25)

Response:
{
  "products": [
    {
      "product_name": "...",
      "brands": "...",
      "code": "...",
      ...
    }
  ],
  "count": 25,
  "page": 1
}
```

## Proposed API Integration Architecture

```mermaid
graph TB
    subgraph "BinBuddy App"
        subgraph "Presentation Layer"
            VM[ViewModels]
        end
        
        subgraph "Domain Layer"
            UC[Use Cases]
        end
        
        subgraph "Data Layer"
            REPO[Repository]
            API_CLIENT[API Client]
            CACHE[Cache Layer]
        end
    end
    
    subgraph "External APIs"
        OFF[Open Food Facts API]
        MUNICIPAL[Municipal Waste Calendar API]
        PFAND[Pfand Database API]
        MAP[Map Services API]
    end
    
    VM --> UC
    UC --> REPO
    REPO --> API_CLIENT
    REPO --> CACHE
    API_CLIENT --> OFF
    API_CLIENT --> MUNICIPAL
    API_CLIENT --> PFAND
    API_CLIENT --> MAP
    
    style VM fill:#4CAF50,color:#fff
    style UC fill:#FF9800,color:#fff
    style REPO fill:#2196F3,color:#fff
    style API_CLIENT fill:#9C27B0,color:#fff
    style OFF fill:#00BCD4,color:#fff
    style MUNICIPAL fill:#00BCD4,color:#fff
    style PFAND fill:#00BCD4,color:#fff
    style MAP fill:#00BCD4,color:#fff
```

## Proposed Additional APIs

### 1. Municipal Waste Calendar API

```mermaid
sequenceDiagram
    participant App
    participant MunicipalAPI
    participant Database

    App->>MunicipalAPI: GET /calendar?postal_code=51643
    MunicipalAPI->>Database: Query collection dates
    Database-->>MunicipalAPI: Collection schedule
    MunicipalAPI-->>App: JSON calendar data
    
    Note over App,Database: Fallback to local database<br/>if API unavailable
```

**Potential Sources:**

- Municipal websites (scraping)
- Open Data portals
- Custom API if available

### 2. Pfand Database API

```http
GET /pfand/check?barcode={barcode}

Response:
{
  "has_pfand": true,
  "pfand_amount": 0.25,
  "return_locations": [
    {
      "name": "Supermarket XYZ",
      "address": "...",
      "latitude": 51.123,
      "longitude": 7.456
    }
  ]
}
```

### 3. Map Services (Google Maps / OpenStreetMap)

```http
GET /places/search?query=pfand+return&location={lat},{lng}

Response:
{
  "places": [
    {
      "name": "...",
      "address": "...",
      "coordinates": {...}
    }
  ]
}
```

## API Client Architecture

```mermaid
classDiagram
    class ApiClient {
        <<interface>>
        +getProduct(barcode: String): Single~ProductResponse~
        +searchProducts(query: String, filters: Map): Single~SearchResponse~
    }
    
    class OpenFoodFactsClient {
        -baseUrl: String
        -retrofit: Retrofit
        +getProduct(barcode: String)
        +searchProducts(query: String, filters: Map)
    }
    
    class MunicipalApiClient {
        -baseUrl: String
        +getCalendar(postalCode: String)
        +getCollectionDates(location: Location)
    }
    
    class PfandApiClient {
        -baseUrl: String
        +checkPfand(barcode: String)
        +getReturnLocations(location: Location)
    }
    
    class ApiResponse {
        +data: T
        +error: ApiError?
        +isSuccess: Boolean
    }
    
    class ApiError {
        +code: Int
        +message: String
        +type: ErrorType
    }
    
    ApiClient <|.. OpenFoodFactsClient
    ApiClient <|.. MunicipalApiClient
    ApiClient <|.. PfandApiClient
    OpenFoodFactsClient --> ApiResponse
    MunicipalApiClient --> ApiResponse
    PfandApiClient --> ApiResponse
    ApiResponse --> ApiError
```

## API Request/Response Flow

### Current Flow (Simple)

```mermaid
sequenceDiagram
    participant Activity
    participant Executor
    participant HttpURLConnection
    participant API

    Activity->>Executor: Execute network task
    Executor->>HttpURLConnection: Create connection
    HttpURLConnection->>API: GET request
    API-->>HttpURLConnection: JSON response
    HttpURLConnection->>Executor: Parse JSON
    Executor->>Activity: Return result (UI thread)
```

### Proposed Flow (With Retrofit + RxJava/Coroutines)

```mermaid
sequenceDiagram
    participant ViewModel
    participant UseCase
    participant Repository
    participant ApiClient
    participant Retrofit
    participant API
    participant Cache

    ViewModel->>UseCase: Execute use case
    UseCase->>Repository: Get product
    Repository->>Cache: Check cache
    alt Cache miss
        Repository->>ApiClient: Fetch from API
        ApiClient->>Retrofit: Create request
        Retrofit->>API: GET /product/{barcode}
        API-->>Retrofit: JSON response
        Retrofit->>ApiClient: ProductResponse
        ApiClient->>Repository: Product
        Repository->>Cache: Update cache
    end
    Repository-->>UseCase: Product
    UseCase-->>ViewModel: Result
    ViewModel-->>UI: Update LiveData
```

## Error Handling Strategy

```mermaid
flowchart TD
    REQUEST[API Request]
    SUCCESS{Response Status}
    PARSE{Valid JSON?}
    CACHE{Cache Available?}
    ERROR[Error Handling]
    
    REQUEST --> SUCCESS
    SUCCESS -->|200 OK| PARSE
    SUCCESS -->|404| ERROR
    SUCCESS -->|500| ERROR
    SUCCESS -->|Timeout| CACHE
    PARSE -->|Yes| SUCCESS_RESULT[Return Data]
    PARSE -->|No| ERROR
    CACHE -->|Yes| CACHE_RESULT[Return Cached Data]
    CACHE -->|No| ERROR
    ERROR --> USER_ERROR[Show User-Friendly Error]
    
    style SUCCESS_RESULT fill:#4CAF50,color:#fff
    style CACHE_RESULT fill:#FF9800,color:#fff
    style ERROR fill:#f44336,color:#fff
```

## API Rate Limiting & Caching

### Current

- ❌ No rate limiting
- ❌ No caching
- ❌ No retry logic
- ❌ No offline support

### Proposed

- ✅ Request caching (1 hour TTL)
- ✅ Exponential backoff retry
- ✅ Offline-first with cache
- ✅ Rate limiting (respect API limits)
- ✅ Request queuing for offline requests

## API Response Models

### ProductResponse

```kotlin
data class ProductResponse(
    val status: Int,
    val statusVerbose: String?,
    val product: ProductDto?
)

data class ProductDto(
    val barcode: String,
    val productName: String?,
    val brands: String?,
    val categories: String?,
    val packaging: String?,
    val quantity: String?,
    val ingredients: List<IngredientDto>?,
    val labels: String?,
    val genericName: String?,
    val imageUrl: String?
)
```

### SearchResponse

```kotlin
data class SearchResponse(
    val products: List<ProductDto>,
    val count: Int,
    val page: Int,
    val pageSize: Int
)
```
