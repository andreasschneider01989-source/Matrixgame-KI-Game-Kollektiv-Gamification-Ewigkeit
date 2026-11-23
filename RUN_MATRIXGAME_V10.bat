@echo off
setlocal
cd /d "%~dp0"
chcp 65001 >nul

echo.
echo  MATRIXGAME UNIVERSAL GRID LAB v0.10.0
echo  -------------------------------------

where javac >nul 2>nul
if errorlevel 1 (
  echo FEHLER: javac wurde nicht gefunden.
  echo Bitte ein JDK 17 oder neuer installieren und JAVA_HOME/PATH setzen.
  pause
  exit /b 1
)

if not exist "out" mkdir "out"

echo Kompiliere Java-Quelltext als UTF-8 ...
javac -encoding UTF-8 -d out src\rhgr\MatrixgameUniversalGridLab_v10_0.java
if errorlevel 1 (
  echo.
  echo KOMPILIERUNG FEHLGESCHLAGEN. Bitte Fehlermeldung oben pruefen.
  pause
  exit /b 1
)

echo Starte Simulation ...
java -cp out rhgr.MatrixgameUniversalGridLab_v10_0
if errorlevel 1 (
  echo.
  echo Die Anwendung wurde mit einem Fehler beendet.
  pause
  exit /b 1
)

endlocal
