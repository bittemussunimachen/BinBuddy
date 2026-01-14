# System Architecture Diagram

## Overview
High-level system architecture showing all components, external services, and data flows in the BinBuddy application.

```mermaid
graph TB
    subgraph "Android Device"
        subgraph "BinBuddy App"
            UI[UI Layer<br/>Activities, Adapters]
            BL[Business Logic<br/>Product Lookup, Search]
            CAM[Camera Module<br/>CameraX]
            ML[ML Kit<br/>Barcode Scanner]
        end
        STORAGE[(Local Storage<br/>Not Implemented)]
    end
    
    subgraph "External Services"
        OFF[Open Food Facts API<br/>world.openfoodfacts.org]
        GPS[Location Services<br/>Not Implemented]
    end
    
    subgraph "Hardware"
        CAMERA[Device Camera]
    end
    
    USER[User] --> UI
    UI --> BL
    UI --> CAM
    CAM --> CAMERA
    CAMERA --> ML
    ML --> BL
    BL --> OFF
    BL -.-> STORAGE
    UI -.-> GPS
    
    style UI fill:#4CAF50,color:#fff
    style BL fill:#2196F3,color:#fff
    style CAM fill:#FF9800,color:#fff
    style ML fill:#9C27B0,color:#fff
    style OFF fill:#00BCD4,color:#fff
    style STORAGE fill:#f44336,color:#fff,stroke-dasharray: 5 5
    style GPS fill:#f44336,color:#fff,stroke-dasharray: 5 5
```

## Components Description

### Android Device Components
- **UI Layer**: MainActivity, ScannerActivity, ProductSearchActivity, ProductAdapter
- **Business Logic**: Product lookup, search, data parsing (currently in Activities)
- **Camera Module**: CameraX for camera access and preview
- **ML Kit**: Google ML Kit for barcode scanning
- **Local Storage**: Not yet implemented (Room database planned)

### External Services
- **Open Food Facts API**: Free product database, no API key required
- **Location Services**: Planned for location-based features

### Data Flows
1. **Scan Flow**: User → UI → Camera → ML Kit → Business Logic → Open Food Facts → UI
2. **Search Flow**: User → UI → Business Logic → Open Food Facts → UI
3. **Storage Flow**: Business Logic → Local Storage (planned)

## Current vs. Planned
- ✅ **Implemented**: UI, Camera, ML Kit, Open Food Facts integration
- ⚠️ **Planned**: Local Storage, Location Services, Additional APIs
