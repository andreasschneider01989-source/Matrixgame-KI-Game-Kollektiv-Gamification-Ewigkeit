@echo off
setlocal
cd /d "%~dp0"
where javac >nul 2>nul
if errorlevel 1 (
  echo FEHLER: JDK 17+ fehlt oder javac ist nicht im PATH.
  pause
  exit /b 1
)
if not exist out mkdir out
echo Kompiliere Matrixgame v0.12.0 ...
javac -encoding UTF-8 -d out src\rhgr\MatrixgameUniversalGridLab_v12_0.java
if errorlevel 1 (
  echo FEHLER: Kompilierung fehlgeschlagen.
  pause
  exit /b 1
)
set TICKS=%~1
if "%TICKS%"=="" set TICKS=306
echo Simuliere Infinity-Evolution und erstelle Browserbericht ...
java -Djava.awt.headless=true -cp out rhgr.MatrixgameUniversalGridLab_v12_0 --simulate %TICKS% 20261116 160 100 matrixgame-v12-report.html
if errorlevel 1 (
  echo FEHLER: Simulation fehlgeschlagen.
  pause
  exit /b 1
)
start "" "%CD%\matrixgame-v12-report.html"
endlocal
