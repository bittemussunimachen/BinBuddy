# BinBuddy Diagrams Documentation

This directory contains all architectural and design diagrams for the BinBuddy Android application.

## Diagram Index

### 1. System Architecture Diagram
**File**: `01-system-architecture.md`
- High-level system components
- External services and dependencies
- Data flows between components

### 2. Application Architecture Diagram
**File**: `02-application-architecture.md`
- Current Activity-based architecture
- Proposed MVVM architecture
- Migration path and comparison

### 3. Component Diagram
**File**: `03-component-diagram.md`
- Current component structure
- Component dependencies
- Missing components identification

### 4. Sequence Diagrams
**File**: `04-sequence-diagrams.md`
- Barcode scanning flow
- Product search flow
- Product lookup flow
- Error handling flow
- Proposed improved flow with Repository & ViewModel

### 5. Class Diagram
**File**: `05-class-diagram.md`
- Current class structure
- Proposed class structure (MVVM)
- Class relationships and design patterns

### 6. Data Flow Diagram
**File**: `06-data-flow-diagram.md`
- Current data flow (no persistence)
- Proposed data flow (with persistence)
- Data transformation points
- Storage strategy

### 7. User Journey Map
**File**: `07-user-journey-map.md`
- Complete user journey from installation to success
- Emotional journey mapping
- Pain points and solutions
- Touchpoint analysis

### 8. Database Schema Diagram
**File**: `08-database-schema.md`
- Proposed Room database schema
- Entity relationships
- Database indexes
- Migration strategy
- Room entity examples

### 9. API Integration Diagram
**File**: `09-api-integration.md`
- Current API integration
- Proposed API architecture
- Additional APIs (Municipal, Pfand, Maps)
- Error handling strategy
- Response models

### 10. Feature Dependency Graph
**File**: `10-feature-dependency-graph.md`
- Feature implementation dependencies
- Implementation phases
- Critical path analysis
- Risk assessment
- Implementation recommendations

## How to View These Diagrams

### Option 1: GitHub/GitLab
These Mermaid diagrams will render automatically when viewed on GitHub or GitLab.

### Option 2: Mermaid Live Editor
1. Copy the Mermaid code from any diagram file
2. Paste into [Mermaid Live Editor](https://mermaid.live)
3. View and export as PNG/SVG

### Option 3: VS Code Extension
Install the "Markdown Preview Mermaid Support" extension in VS Code to view diagrams directly.

### Option 4: Documentation Tools
- **MkDocs** with `mkdocs-mermaid2-plugin`
- **Docusaurus** with `@docusaurus/theme-mermaid`
- **GitBook** supports Mermaid natively

## Diagram Format

All diagrams use **Mermaid** syntax, which is:
- Text-based and version-controllable
- Widely supported across platforms
- Easy to maintain and update
- Can be exported to images

## Updating Diagrams

When updating diagrams:
1. Edit the corresponding `.md` file
2. Update the Mermaid code block
3. Test rendering in Mermaid Live Editor
4. Commit changes to version control

## Related Documentation

For additional documentation, see:
- ADRs (Architecture Decision Records) - `../adr/`
- Domain Models - `../domain-models/`
- HTAs (Hierarchical Task Analysis) - `../hta/`
- Stakeholder Analysis - `../stakeholders/`
