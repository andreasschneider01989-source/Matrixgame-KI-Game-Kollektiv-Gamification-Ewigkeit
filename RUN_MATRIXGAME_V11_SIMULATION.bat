@echo off
setlocal
cd /d "%~dp0"

where javac >nul 2>nul
if errorlevel 1 (
    echo FEHLER: JDK 17 oder neuer ist nicht installiert oder javac fehlt im PATH.
    echo Installiere ein JDK 17+ und starte dieses Skript erneut.
    pause
    exit /b 1
)

if not exist out mkdir out

echo Kompiliere Matrixgame v0.11.0 ...
javac -encoding UTF-8 -d out src\rhgr\MatrixgameUniversalGridLab_v11_0.java
if errorlevel 1 (
    echo FEHLER: Kompilierung fehlgeschlagen.
    pause
    exit /b 1
)

set TICKS=%~1
if "%TICKS%"=="" set TICKS=306

echo Starte headless Evolution und erstelle Browser-Canvas ...
java -Djava.awt.headless=true -cp out rhgr.MatrixgameUniversalGridLab_v11_0 --simulate %TICKS% 20261116 160 100 matrixgame-report.html
if errorlevel 1 (
    echo FEHLER: Simulation fehlgeschlagen.
    pause
    exit /b 1
)

echo Oeffne das HTML-Dashboard im Standardbrowser ...
start "" "%CD%\matrixgame-report.html"
endlocal
