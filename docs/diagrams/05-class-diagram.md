# Class Diagram

## Current Class Structure

```mermaid
classDiagram
    class MainActivity {
        -ExecutorService networkExecutor
        -static int SCAN_REQUEST_CODE
        -static String OFF_BASE_URL
        +onCreate(Bundle)
        -setupClickListeners()
        -openScanner()
        -openSearch()
        -onActivityResult(int, int, Intent)
        -fetchProductDetails(String)
        -fetchFromOpenFoodFacts(String) ProductInfo
        -showProductResult(ProductInfo)
    }
    
    class ScannerActivity {
        -PreviewView previewView
        -BarcodeScanner barcodeScanner
        -ExecutorService cameraExecutor
        -boolean isScanning
        +onCreate(Bundle)
        -initViews()
        -setupBarcodeScanner()
        -checkCameraPermission()
        -startCamera()
        -processImageProxy(ImageProxy)
        -handleBarcodeResult(String)
    }
    
    class ProductSearchActivity {
        -TextInputEditText etSearch
        -CheckBox checkGermany
        -RecyclerView rvProducts
        -ProductAdapter adapter
        -ExecutorService networkExecutor
        +onCreate(Bundle)
        -startSearch()
        -searchOpenFoodFacts(String, boolean) List~ProductItem~
        -parseProducts(String, boolean) List~ProductItem~
        -isGerman(JSONObject) boolean
    }
    
    class ProductAdapter {
        -List~ProductItem~ items
        +onCreateViewHolder(ViewGroup, int) ProductViewHolder
        +onBindViewHolder(ProductViewHolder, int)
        +getItemCount() int
        +updateData(List~ProductItem~)
    }
    
    class ProductViewHolder {
        -TextView tvTitle
        -TextView tvBrand
        -TextView tvCategories
        -TextView tvPackaging
        +bind(ProductItem)
    }
    
    class ProductInfo {
        +String name
        +String brand
        +String quantity
        +String categories
        +String packaging
        +String generic
        +String labels
        +List~String~ ingredients
    }
    
    class ProductItem {
        +String name
        +String brand
        +String categories
        +String packaging
        +String code
        +String quantity
    }
    
    MainActivity --> ProductInfo : creates
    MainActivity --> ScannerActivity : starts
    MainActivity --> ProductSearchActivity : starts
    ProductSearchActivity --> ProductAdapter : uses
    ProductSearchActivity --> ProductItem : creates
    ProductAdapter --> ProductViewHolder : creates
    ProductAdapter --> ProductItem : uses
    ProductViewHolder --> ProductItem : displays
```

## Proposed Class Structure (MVVM)

```mermaid
classDiagram
    class MainActivity {
        -MainViewModel viewModel
        -RecyclerView recyclerView
        +onCreate(Bundle)
        -observeViewModel()
        -setupRecyclerView()
    }
    
    class MainViewModel {
        -GetProductUseCase getProductUseCase
        -SaveScanHistoryUseCase saveHistoryUseCase
        -LiveData~Product~ product
        -LiveData~String~ error
        +scanBarcode(String)
        +searchProduct(String)
    }
    
    class ProductRepository {
        -ProductApiService apiService
        -ProductDao productDao
        -ProductCache cache
        +getProduct(String) Flow~Product~
        +searchProducts(String, boolean) Flow~List~Product~~
        +saveProduct(Product)
        +getScanHistory() Flow~List~ScanHistory~~
    }
    
    class GetProductUseCase {
        -ProductRepository repository
        +execute(String) Flow~Result~Product~~
    }
    
    class SearchProductsUseCase {
        -ProductRepository repository
        +execute(String, boolean) Flow~Result~List~Product~~~
    }
    
    class SaveScanHistoryUseCase {
        -ProductRepository repository
        +execute(ScanHistory)
    }
    
    class Product {
        +String id
        +String name
        +String brand
        +String barcode
        +List~Category~ categories
        +PackagingInfo packaging
        +WasteCategory wasteCategory
    }
    
    class ScanHistory {
        +Long id
        +String barcode
        +String productName
        +Long timestamp
        +String location
    }
    
    class WasteCategory {
        +String id
        +String name
        +String description
        +String icon
    }
    
    class ProductDao {
        <<interface>>
        +insertProduct(Product)
        +getProduct(String) Flow~Product~
        +getScanHistory() Flow~List~ScanHistory~~
    }
    
    class ProductApiService {
        <<interface>>
        +getProduct(String) Single~ProductResponse~
        +searchProducts(String, boolean) Single~SearchResponse~
    }
    
    MainActivity --> MainViewModel : observes
    MainViewModel --> GetProductUseCase : uses
    MainViewModel --> SaveScanHistoryUseCase : uses
    GetProductUseCase --> ProductRepository : uses
    SearchProductsUseCase --> ProductRepository : uses
    SaveScanHistoryUseCase --> ProductRepository : uses
    ProductRepository --> ProductApiService : uses
    ProductRepository --> ProductDao : uses
    Product --> WasteCategory : has
    ScanHistory --> Product : references
```

## Class Relationships

### Current Structure
- **Tight Coupling**: Activities directly handle network calls
- **No Abstraction**: Direct dependency on HttpURLConnection
- **Mixed Responsibilities**: UI and business logic in same class
- **No Persistence**: No database classes

### Proposed Structure
- **Loose Coupling**: Activities only depend on ViewModels
- **Clear Separation**: Use Cases for business logic, Repository for data
- **Testable**: Interfaces allow easy mocking
- **Persistent**: Room database with DAOs

## Key Design Patterns

### Current
- **None**: Procedural code in Activities

### Proposed
- **MVVM**: ViewModel pattern for state management
- **Repository**: Single source of truth for data
- **Use Case**: Business logic encapsulation
- **Observer**: LiveData/StateFlow for reactive UI
- **Dependency Injection**: Hilt/Koin for dependency management
