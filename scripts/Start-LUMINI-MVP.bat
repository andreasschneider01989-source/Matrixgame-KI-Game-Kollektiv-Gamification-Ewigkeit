@echo off
chcp 65001 >nul
setlocal
if not exist build\lumini-mvp\classes mkdir build\lumini-mvp\classes
if not exist build\lumini-mvp\test-classes mkdir build\lumini-mvp\test-classes
javac --release 11 -encoding UTF-8 -Xlint:all -Werror -d build\lumini-mvp\classes src\main\java\de\matrixgame\lumini\*.java
if errorlevel 1 goto :error
javac --release 11 -encoding UTF-8 -Xlint:all -Werror -cp build\lumini-mvp\classes -d build\lumini-mvp\test-classes src\test\java\de\matrixgame\lumini\*.java
if errorlevel 1 goto :error
if "%1"=="--jsonl-only" (
  java -ea -Dfile.encoding=UTF-8 -cp build\lumini-mvp\classes;build\lumini-mvp\test-classes de.matrixgame.lumini.IdeaCompilerPipelineSelfTest 1>&2
) else (
  java -ea -Dfile.encoding=UTF-8 -cp build\lumini-mvp\classes;build\lumini-mvp\test-classes de.matrixgame.lumini.IdeaCompilerPipelineSelfTest
)
if errorlevel 1 goto :error
java -Dfile.encoding=UTF-8 -cp build\lumini-mvp\classes de.matrixgame.lumini.LearningRoundDemo %*
goto :done
:error
echo Build oder Test fehlgeschlagen.
pause
exit /b 1
:done
pause
