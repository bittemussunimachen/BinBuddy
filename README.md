# BinBuddy

Eine Android-App für internationale Nutzer:innen, die bei der deutschen Mülltrennung hilft. Scanne Produkte, suche nach Artikeln, filtere auf Deutschland und erhalte Produkt- und Verpackungsinfos aus der Open Food Facts API.

Kurz gesagt: Scanne oder suche Produkte, bekomme Name/Marke/Kategorien/Verpackung aus Open Food Facts (frei, kein API-Key), optional nur deutsche Produkte anzeigen, einfache UI mit Listenansicht und Scan-Toast.

## Features
- Barcode-Scanner mit CameraX + ML Kit
- Produktsuche (Open Food Facts), optionaler Deutschland-Filter
- Ergebnisliste mit Name, Marke, Kategorien, Verpackung
- Einfacher Produkt-Detailtoast nach Scan
- Material-gestütztes UI (RecyclerView, TextInputLayout, etc.)

## Tech Stack
- Android (Java 11)
- CameraX, ML Kit Barcode Scanning
- Open Food Facts (freie API, keine Keys nötig)
- Material Components, RecyclerView, ConstraintLayout

## Anforderungen
- Android Studio
- Min SDK: 24, Target SDK: 36
- Java 11

## Projekt aufsetzen
1. Klonen  
   ```bash
   git clone https://github.com/bittemussunimachen/BinBuddy.git
   ```
2. In Android Studio öffnen und Gradle Sync ausführen.
3. App auf Emulator (API 33/34 reicht) oder Gerät starten.

## CLI-Test ohne Android Studio
Einzelnes Produkt abfragen (Open Food Facts):
```bash
python scripts/opengtin_test.py 4009233006847
```

## Bekannte Hinweise
- Für Push-Rechte auf das Original-Repo wird GitHub-Zugriff benötigt; sonst in einen Fork pushen.
- Emulator-Kamera ist begrenzt; für zuverlässiges Scannen besser ein physisches Gerät nutzen.
