@echo off
cd /d "%~dp0"
javac -d out src\EvoGrid.java src\sim\*.java src\ui\*.java src\editor\*.java
java -cp out EvoGrid
