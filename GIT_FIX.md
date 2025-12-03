# Git Push Fehler beheben

## Problem
Das Remote-Repository enthält bereits Inhalte (z.B. README.md), die lokal nicht vorhanden sind.

## Lösung

Führe folgende Befehle im Terminal aus (Android Studio Terminal oder PowerShell):

### Schritt 1: Remote-Änderungen holen und zusammenführen

```bash
git pull origin main --allow-unrelated-histories
```

Dieser Befehl:
- Holt die Remote-Änderungen
- Führt sie mit deinen lokalen Änderungen zusammen
- `--allow-unrelated-histories` erlaubt das Zusammenführen unterschiedlicher Histories

### Schritt 2: Bei Merge-Konflikten

Falls es Konflikte gibt (z.B. bei README.md):
1. Öffne die betroffenen Dateien
2. Entscheide, welche Version behalten werden soll
3. Oder kombiniere beide Versionen
4. Dann:
```bash
git add .
git commit -m "Merge remote and local changes"
```

### Schritt 3: Pushen

```bash
git push -u origin main
```

## Alternative: Force Push (NUR wenn du sicher bist!)

**WARNUNG:** Dies überschreibt alle Remote-Änderungen!

```bash
git push -u origin main --force
```

**Verwende dies nur, wenn:**
- Du sicher bist, dass die Remote-Änderungen nicht wichtig sind
- Du alleine am Repository arbeitest
- Du die Remote-README.md nicht behalten möchtest

## Empfohlene Lösung (Schritt-für-Schritt)

```bash
# 1. Remote-Änderungen holen
git pull origin main --allow-unrelated-histories

# 2. Falls Merge-Commit erstellt wurde, pushen
git push -u origin main
```

## Wenn Git nicht im PATH ist

Verwende die Android Studio GUI:
1. **VCS** → **Git** → **Pull**
2. Wähle Remote: `origin`, Branch: `main`
3. Aktivieren: "Allow unrelated histories"
4. Klicke **Pull**
5. Bei Konflikten: Dateien manuell bearbeiten
6. **VCS** → **Git** → **Push**

