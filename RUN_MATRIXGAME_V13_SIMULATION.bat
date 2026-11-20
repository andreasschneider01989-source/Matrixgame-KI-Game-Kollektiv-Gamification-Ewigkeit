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
echo Kompiliere Matrixgame v0.13.0 ...
javac -encoding UTF-8 -d out src\rhgr\MatrixgameUniversalGridLab_v13_0.java
if errorlevel 1 (
  echo FEHLER: Kompilierung fehlgeschlagen.
  pause
  exit /b 1
)
set TICKS=%~1
if "%TICKS%"=="" set TICKS=500
echo Simuliere die vergroesserte 320x200-Welt ...
java -Djava.awt.headless=true -cp out rhgr.MatrixgameUniversalGridLab_v13_0 --simulate %TICKS% 20261116 320 200 matrixgame-v13-report.html
if errorlevel 1 (
  echo FEHLER: Simulation fehlgeschlagen.
  pause
  exit /b 1
)
start "" "%CD%\matrixgame-v13-report.html"
endlocal
