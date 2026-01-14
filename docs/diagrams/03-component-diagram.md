# Component Diagram

## Current Component Structure

```mermaid
graph TB
    subgraph "UI Components"
        MA[MainActivity]
        SA[ScannerActivity]
        PSA[ProductSearchActivity]
        PA[ProductAdapter]
        LAYOUTS[XML Layouts]
    end
    
    subgraph "Business Logic Components"
        PL[Product Lookup Logic<br/>in MainActivity]
        SL[Search Logic<br/>in ProductSearchActivity]
        BL[Barcode Logic<br/>in ScannerActivity]
    end
    
    subgraph "Data Components"
        NET[Network Layer<br/>HttpURLConnection]
        PARSER[JSON Parser<br/>Manual Parsing]
    end
    
    subgraph "External Components"
        CAMERAX[CameraX Library]
        MLKIT[ML Kit Barcode Scanner]
        OFF_API[Open Food Facts API]
    end
    
    subgraph "Missing Components"
        REPO[Repository Layer]
        DB[Database Layer]
        VM[ViewModel Layer]
        DI[Dependency Injection]
        CACHE[Cache Layer]
    end
    
    MA --> PL
    SA --> BL
    PSA --> SL
    
    PL --> NET
    SL --> NET
    BL --> CAMERAX
    BL --> MLKIT
    
    NET --> OFF_API
    NET --> PARSER
    
    MA -.->|Should use| VM
    SA -.->|Should use| VM
    PSA -.->|Should use| VM
    
    VM -.->|Should use| REPO
    REPO -.->|Should use| DB
    REPO -.->|Should use| CACHE
    REPO -.->|Should use| NET
    
    style MA fill:#4CAF50,color:#fff
    style SA fill:#4CAF50,color:#fff
    style PSA fill:#4CAF50,color:#fff
    style PA fill:#4CAF50,color:#fff
    style PL fill:#FF9800,color:#fff
    style SL fill:#FF9800,color:#fff
    style BL fill:#FF9800,color:#fff
    style NET fill:#2196F3,color:#fff
    style PARSER fill:#2196F3,color:#fff
    style CAMERAX fill:#9C27B0,color:#fff
    style MLKIT fill:#9C27B0,color:#fff
    style OFF_API fill:#00BCD4,color:#fff
    style REPO fill:#f44336,color:#fff,stroke-dasharray: 5 5
    style DB fill:#f44336,color:#fff,stroke-dasharray: 5 5
    style VM fill:#f44336,color:#fff,stroke-dasharray: 5 5
    style DI fill:#f44336,color:#fff,stroke-dasharray: 5 5
    style CACHE fill:#f44336,color:#fff,stroke-dasharray: 5 5
```

## Component Dependencies

### Current Dependencies
- **Activities** depend directly on network layer
- **Activities** contain business logic
- **Network layer** uses HttpURLConnection
- **No abstraction** between layers

### Proposed Dependencies
- **Activities** depend only on ViewModels
- **ViewModels** depend on Use Cases
- **Use Cases** depend on Repository
- **Repository** depends on Data Sources (API, Database, Cache)
- **Dependency Injection** manages all dependencies

## Component Responsibilities

### UI Components
- **MainActivity**: Home screen, navigation, product display
- **ScannerActivity**: Camera preview, barcode scanning UI
- **ProductSearchActivity**: Search input, results list
- **ProductAdapter**: RecyclerView item rendering

### Business Logic Components (Current)
- **Product Lookup**: Fetches product by barcode
- **Search Logic**: Searches products by name
- **Barcode Logic**: Processes camera frames

### Data Components
- **Network Layer**: HTTP requests to Open Food Facts
- **JSON Parser**: Manual JSON parsing (no Gson/Jackson)

### External Components
- **CameraX**: Camera lifecycle and preview
- **ML Kit**: Barcode detection from images
- **Open Food Facts API**: Product database

### Missing Components
- **Repository**: Single source of truth for data
- **Database**: Local storage (Room)
- **ViewModel**: UI state management
- **Dependency Injection**: Hilt/Koin for dependency management
- **Cache**: Offline data caching
