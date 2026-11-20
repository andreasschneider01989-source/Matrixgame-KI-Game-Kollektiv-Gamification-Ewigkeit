@echo off
setlocal
cd /d "%~dp0"

where javac >nul 2>nul
if errorlevel 1 (
    echo FEHLER: JDK 17 oder neuer ist nicht installiert oder javac fehlt im PATH.
    pause
    exit /b 1
)

if not exist out mkdir out

echo Kompiliere Matrixgame Universal Grid Lab v0.11.0 ...
javac -encoding UTF-8 -d out src\rhgr\MatrixgameUniversalGridLab_v11_0.java
if errorlevel 1 (
    echo FEHLER: Kompilierung fehlgeschlagen.
    pause
    exit /b 1
)

echo Starte reproduzierbare Selbsttests ...
java -Djava.awt.headless=true -cp out rhgr.MatrixgameUniversalGridLab_v11_0 --self-test
if errorlevel 1 (
    echo FEHLER: Mindestens ein Selbsttest ist fehlgeschlagen.
    pause
    exit /b 1
)

echo Alle Selbsttests waren erfolgreich.
pause
endlocal
