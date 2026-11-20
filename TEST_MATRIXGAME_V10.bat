@echo off
setlocal
cd /d "%~dp0"
chcp 65001 >nul

echo.
echo  MATRIXGAME v0.10.0 — SELBSTTESTS
echo  --------------------------------

where javac >nul 2>nul
if errorlevel 1 (
  echo FEHLER: javac wurde nicht gefunden. Bitte JDK 17+ installieren.
  pause
  exit /b 1
)

if not exist "out" mkdir "out"

echo [1/2] Kompiliere Java-Quelltext ...
javac -encoding UTF-8 -d out src\rhgr\MatrixgameUniversalGridLab_v10_0.java
if errorlevel 1 (
  echo KOMPILIERUNG FEHLGESCHLAGEN.
  pause
  exit /b 1
)

echo [2/2] Fuehre alle Selbsttests aus ...
java -Djava.awt.headless=true -cp out rhgr.MatrixgameUniversalGridLab_v10_0 --self-test
if errorlevel 1 (
  echo SELBSTTESTS FEHLGESCHLAGEN.
  pause
  exit /b 1
)

echo.
echo ALLE SELBSTTESTS ERFOLGREICH.
pause
endlocal
