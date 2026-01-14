# Database Schema Diagram

## Proposed Room Database Schema

```mermaid
erDiagram
    PRODUCT ||--o{ SCAN_HISTORY : "has"
    PRODUCT ||--o{ PRODUCT_CATEGORY : "has"
    PRODUCT }o--|| WASTE_CATEGORY : "belongs_to"
    PRODUCT }o--|| PACKAGING_INFO : "has"
    USER ||--o{ SCAN_HISTORY : "creates"
    USER ||--o{ FAVORITE_PRODUCT : "saves"
    PRODUCT ||--o{ FAVORITE_PRODUCT : "referenced_by"
    LOCATION ||--o{ WASTE_CALENDAR : "has"
    WASTE_CATEGORY ||--o{ RECYCLING_RULE : "has"
    
    PRODUCT {
        string id PK
        string barcode UK
        string name
        string brand
        string generic_name
        string quantity
        string labels
        string image_url
        long created_at
        long updated_at
        string waste_category_id FK
    }
    
    SCAN_HISTORY {
        long id PK
        string user_id FK
        string product_id FK
        string barcode
        long timestamp
        string location
        double latitude
        double longitude
    }
    
    PRODUCT_CATEGORY {
        long id PK
        string product_id FK
        string category_name
        int hierarchy_level
    }
    
    WASTE_CATEGORY {
        string id PK
        string name_de
        string name_en
        string description_de
        string description_en
        string icon_name
        string color_hex
        int sort_order
    }
    
    PACKAGING_INFO {
        long id PK
        string product_id FK
        string material_type
        string recyclability
        string disposal_method
        boolean is_pfand
        float pfand_amount
    }
    
    USER {
        string id PK
        string name
        string email
        string language_code
        string location_city
        string location_postal_code
        double location_latitude
        double location_longitude
        long created_at
        long last_active_at
    }
    
    FAVORITE_PRODUCT {
        long id PK
        string user_id FK
        string product_id FK
        long created_at
    }
    
    LOCATION {
        string id PK
        string city
        string postal_code
        string state
        string country
        double latitude
        double longitude
    }
    
    WASTE_CALENDAR {
        long id PK
        string location_id FK
        string waste_category_id FK
        long collection_date
        string collection_type
        string notes
    }
    
    RECYCLING_RULE {
        long id PK
        string waste_category_id FK
        string rule_name
        string description_de
        string description_en
        string example_products
        int priority
    }
```

## Entity Relationships

### Core Entities

#### Product
- **Primary Key**: `id` (UUID)
- **Unique Constraint**: `barcode`
- **Relationships**:
  - One-to-Many with `ScanHistory`
  - One-to-Many with `ProductCategory`
  - Many-to-One with `WasteCategory`
  - One-to-One with `PackagingInfo`
  - Many-to-Many with `User` (via `FavoriteProduct`)

#### ScanHistory
- **Primary Key**: `id` (Auto-increment)
- **Indexes**: `user_id`, `product_id`, `timestamp`
- **Relationships**:
  - Many-to-One with `User`
  - Many-to-One with `Product`

#### WasteCategory
- **Primary Key**: `id` (String: "gelbe_tonne", "restmuell", etc.)
- **Predefined Values**:
  - `gelbe_tonne` - Yellow bin (packaging)
  - `restmuell` - Residual waste
  - `papier` - Paper
  - `bio` - Organic waste
  - `glas` - Glass
  - `pfand` - Deposit return

### Supporting Entities

#### User
- **Primary Key**: `id` (UUID)
- **Optional**: Can work without user accounts (guest mode)

#### Location
- **Primary Key**: `id` (String: postal code + city)
- **Used for**: Waste calendar, local rules

## Database Indexes

```sql
-- Performance indexes
CREATE INDEX idx_scan_history_user_timestamp ON scan_history(user_id, timestamp DESC);
CREATE INDEX idx_scan_history_product ON scan_history(product_id);
CREATE INDEX idx_product_barcode ON product(barcode);
CREATE INDEX idx_product_waste_category ON product(waste_category_id);
CREATE INDEX idx_favorite_user_product ON favorite_product(user_id, product_id);
CREATE INDEX idx_waste_calendar_location_date ON waste_calendar(location_id, collection_date);
```

## Data Migration Strategy

### Version 1: Initial Schema
- Product table
- ScanHistory table
- Basic indexes

### Version 2: Add User Support
- User table
- FavoriteProduct table
- Update ScanHistory with user_id

### Version 3: Add Location Features
- Location table
- WasteCalendar table
- Update User with location

### Version 4: Add Waste Classification
- WasteCategory table
- RecyclingRule table
- Update Product with waste_category_id

## Room Entity Examples

### Product Entity (Kotlin)
```kotlin
@Entity(
    tableName = "product",
    indices = [Index(value = ["barcode"], unique = true)]
)
data class ProductEntity(
    @PrimaryKey val id: String,
    val barcode: String,
    val name: String,
    val brand: String?,
    val genericName: String?,
    val quantity: String?,
    val labels: String?,
    val imageUrl: String?,
    val wasteCategoryId: String?,
    val createdAt: Long,
    val updatedAt: Long
)
```

### ScanHistory Entity (Kotlin)
```kotlin
@Entity(
    tableName = "scan_history",
    indices = [
        Index(value = ["userId", "timestamp"]),
        Index(value = ["productId"])
    ],
    foreignKeys = [
        ForeignKey(
            entity = ProductEntity::class,
            parentColumns = ["id"],
            childColumns = ["productId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class ScanHistoryEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val userId: String?,
    val productId: String?,
    val barcode: String,
    val timestamp: Long,
    val location: String?,
    val latitude: Double?,
    val longitude: Double?
)
```

## Data Access Objects (DAOs)

### ProductDao
- `getProduct(barcode: String): Flow<ProductEntity?>`
- `insertProduct(product: ProductEntity)`
- `updateProduct(product: ProductEntity)`
- `searchProducts(query: String): Flow<List<ProductEntity>>`
- `getProductsByWasteCategory(categoryId: String): Flow<List<ProductEntity>>`

### ScanHistoryDao
- `getScanHistory(userId: String?): Flow<List<ScanHistoryEntity>>`
- `insertScan(scan: ScanHistoryEntity)`
- `deleteScan(id: Long)`
- `getRecentScans(limit: Int): Flow<List<ScanHistoryEntity>>`

### WasteCategoryDao
- `getAllCategories(): Flow<List<WasteCategoryEntity>>`
- `getCategory(id: String): Flow<WasteCategoryEntity?>`
