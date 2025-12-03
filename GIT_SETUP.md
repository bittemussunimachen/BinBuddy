# Git Repository Setup Anleitung

## Option 1: PowerShell Skript ausführen

Führe das Skript `setup_git.ps1` aus:

```powershell
.\setup_git.ps1
```

## Option 2: Manuelle Befehle in Android Studio Terminal

Öffne das Terminal in Android Studio und führe folgende Befehle aus:

```bash
git init
git add .
git commit -m "Initial commit: BinBuddy Android App"
git branch -M main
git remote add origin https://github.com/bittemussunimachen/BinBuddy.git
git push -u origin main
```

## Option 3: Über Android Studio GUI

1. **VCS** → **Enable Version Control Integration**
2. Wähle **Git**
3. **VCS** → **Git** → **Remotes...**
4. Füge Remote hinzu: `https://github.com/bittemussunimachen/BinBuddy.git`
5. **VCS** → **Commit**
6. Wähle alle Dateien aus
7. Commit Message: "Initial commit: BinBuddy Android App"
8. **VCS** → **Git** → **Push**
9. Wähle den Remote "origin" und Branch "main"

## Wichtige Hinweise

- Stelle sicher, dass Git installiert ist: https://git-scm.com/download/win
- Bei der ersten Push-Operation musst du dich möglicherweise bei GitHub authentifizieren
- Die `.gitignore` Datei ist bereits konfiguriert und ignoriert Build-Dateien

## Bei Problemen

Falls Git nicht gefunden wird:
1. Installiere Git von https://git-scm.com/download/win
2. Starte Android Studio neu
3. Oder verwende die Android Studio GUI (Option 3)

