# Sequence Diagrams

## 1. Barcode Scanning Flow

```mermaid
sequenceDiagram
    participant User
    participant MainActivity
    participant ScannerActivity
    participant CameraX
    participant MLKit
    participant MainActivity (Result Handler)

    User->>MainActivity: Tap "Scan" button
    MainActivity->>ScannerActivity: startActivityForResult()
    ScannerActivity->>ScannerActivity: Check camera permission
    alt Permission granted
        ScannerActivity->>CameraX: Initialize camera
        CameraX->>ScannerActivity: Camera ready
        ScannerActivity->>MLKit: Setup barcode scanner
        loop Frame processing
            CameraX->>MLKit: Send image frame
            MLKit->>MLKit: Detect barcode
            alt Barcode detected
                MLKit->>ScannerActivity: Return barcode value
                ScannerActivity->>ScannerActivity: Stop scanning
                ScannerActivity->>MainActivity (Result Handler): setResult(barcode)
                ScannerActivity->>ScannerActivity: finish()
            end
        end
    else Permission denied
        ScannerActivity->>User: Show permission request
        alt User grants permission
            ScannerActivity->>CameraX: Initialize camera
        else User denies permission
            ScannerActivity->>ScannerActivity: finish()
        end
    end
    MainActivity (Result Handler)->>MainActivity: onActivityResult()
```

## 2. Product Search Flow

```mermaid
sequenceDiagram
    participant User
    participant ProductSearchActivity
    participant ExecutorService
    participant OpenFoodFactsAPI
    participant ProductAdapter
    participant RecyclerView

    User->>ProductSearchActivity: Enter search term
    User->>ProductSearchActivity: (Optional) Check "Germany only"
    User->>ProductSearchActivity: Tap "Search" button
    ProductSearchActivity->>ProductSearchActivity: Validate input
    ProductSearchActivity->>ProductSearchActivity: Show loading indicator
    ProductSearchActivity->>ExecutorService: Execute search task
    ExecutorService->>OpenFoodFactsAPI: GET /cgi/search.pl?search_terms=...
    OpenFoodFactsAPI->>ExecutorService: Return JSON response
    ExecutorService->>ExecutorService: Parse JSON
    ExecutorService->>ExecutorService: Filter by Germany (if checked)
    ExecutorService->>ProductSearchActivity: Return List<ProductItem>
    ProductSearchActivity->>ProductSearchActivity: Hide loading indicator
    ProductSearchActivity->>ProductAdapter: updateData(results)
    ProductAdapter->>RecyclerView: notifyDataSetChanged()
    RecyclerView->>User: Display product list
```

## 3. Product Lookup Flow (After Scan)

```mermaid
sequenceDiagram
    participant User
    participant MainActivity
    participant ExecutorService
    participant OpenFoodFactsAPI
    participant MainActivity (UI Thread)

    User->>MainActivity: Scan completes, barcode received
    MainActivity->>MainActivity: Show "Fetching..." toast
    MainActivity->>ExecutorService: Execute fetchProductDetails(barcode)
    ExecutorService->>OpenFoodFactsAPI: GET /api/v0/product/{barcode}.json
    OpenFoodFactsAPI->>ExecutorService: Return JSON response
    alt Product found
        ExecutorService->>ExecutorService: Parse JSON to ProductInfo
        ExecutorService->>MainActivity (UI Thread): Return ProductInfo
        MainActivity (UI Thread)->>MainActivity: showProductResult()
        MainActivity->>User: Show Toast with product details
        MainActivity->>User: Show Snackbar with ingredients
    else Product not found
        ExecutorService->>MainActivity (UI Thread): Return error
        MainActivity (UI Thread)->>User: Show "Product not found" toast
    else Network error
        ExecutorService->>MainActivity (UI Thread): Return exception
        MainActivity (UI Thread)->>User: Show "Search error" toast
    end
```

## 4. Error Handling Flow

```mermaid
sequenceDiagram
    participant User
    participant Activity
    participant NetworkLayer
    participant API

    User->>Activity: Trigger action (scan/search)
    Activity->>NetworkLayer: Make API request
    NetworkLayer->>API: HTTP request
    
    alt Network timeout
        API-->>NetworkLayer: Timeout
        NetworkLayer->>Activity: IOException
        Activity->>User: Show "Connection timeout" error
    else Product not found
        API-->>NetworkLayer: 404 or status=0
        NetworkLayer->>Activity: ProductNotFoundException
        Activity->>User: Show "Product not found" error
    else Invalid response
        API-->>NetworkLayer: Invalid JSON
        NetworkLayer->>Activity: JSONException
        Activity->>User: Show "Data parsing error" error
    else Camera error
        Activity->>User: Show "Camera unavailable" error
    else Permission denied
        Activity->>User: Show "Permission required" error
    end
```

## 5. Proposed Improved Flow (With Repository & ViewModel)

```mermaid
sequenceDiagram
    participant User
    participant Activity
    participant ViewModel
    participant Repository
    participant Cache
    participant API
    participant Database

    User->>Activity: Scan barcode
    Activity->>ViewModel: scanBarcode(barcode)
    ViewModel->>Repository: getProduct(barcode)
    
    Repository->>Cache: Check cache
    alt Cache hit
        Cache->>Repository: Return cached product
    else Cache miss
        Repository->>Database: Check local database
        alt Database hit
            Database->>Repository: Return stored product
            Repository->>Cache: Update cache
        else Database miss
            Repository->>API: Fetch from API
            API->>Repository: Return product data
            Repository->>Database: Save to database
            Repository->>Cache: Update cache
        end
    end
    
    Repository->>ViewModel: Return Product
    ViewModel->>Activity: Update LiveData/StateFlow
    Activity->>User: Display product details
```
