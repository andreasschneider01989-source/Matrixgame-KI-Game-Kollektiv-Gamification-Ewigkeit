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
java -cp out rhgr.MatrixgameUniversalGridLab_v12_0
endlocal
