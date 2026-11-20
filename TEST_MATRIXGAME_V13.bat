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
echo Fuehre deterministische Tests aus ...
java -Djava.awt.headless=true -cp out rhgr.MatrixgameUniversalGridLab_v13_0 --self-test
if errorlevel 1 (
  echo FEHLER: Selbsttest fehlgeschlagen.
  pause
  exit /b 1
)
echo Alle Selbsttests erfolgreich.
pause
endlocal
