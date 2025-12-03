Write-Host "Initialisiere Git Repository..." -ForegroundColor Green

if (-not (Get-Command git -ErrorAction SilentlyContinue)) {
    Write-Host "Git ist nicht installiert oder nicht im PATH." -ForegroundColor Red
    Write-Host "Bitte installiere Git von https://git-scm.com/download/win" -ForegroundColor Yellow
    Write-Host "Oder führe die Befehle manuell in Android Studio Terminal aus:" -ForegroundColor Yellow
    Write-Host ""
    Write-Host "git init" -ForegroundColor Cyan
    Write-Host "git add ." -ForegroundColor Cyan
    Write-Host "git commit -m 'Initial commit'" -ForegroundColor Cyan
    Write-Host "git branch -M main" -ForegroundColor Cyan
    Write-Host "git remote add origin https://github.com/bittemussunimachen/BinBuddy.git" -ForegroundColor Cyan
    Write-Host "git push -u origin main" -ForegroundColor Cyan
    exit 1
}

Write-Host "Git gefunden!" -ForegroundColor Green

if (Test-Path .git) {
    Write-Host "Git Repository bereits initialisiert." -ForegroundColor Yellow
} else {
    Write-Host "Initialisiere Git Repository..." -ForegroundColor Green
    git init
}

Write-Host "Füge alle Dateien hinzu..." -ForegroundColor Green
git add .

Write-Host "Erstelle ersten Commit..." -ForegroundColor Green
git commit -m "Initial commit: BinBuddy Android App"

Write-Host "Setze Branch auf main..." -ForegroundColor Green
git branch -M main

Write-Host "Füge Remote Repository hinzu..." -ForegroundColor Green
git remote remove origin -ErrorAction SilentlyContinue
git remote add origin https://github.com/bittemussunimachen/BinBuddy.git

Write-Host "Pushe zum Remote Repository..." -ForegroundColor Green
Write-Host "Hinweis: Du musst möglicherweise deine GitHub-Credentials eingeben." -ForegroundColor Yellow
git push -u origin main

Write-Host "Fertig! Repository wurde erfolgreich eingerichtet." -ForegroundColor Green

