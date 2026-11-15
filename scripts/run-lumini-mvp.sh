#!/usr/bin/env bash
set -euo pipefail
ROOT="$(cd "$(dirname "$0")/.." && pwd)"
BUILD="$ROOT/build/lumini-mvp"
mkdir -p "$BUILD/classes" "$BUILD/test-classes"
javac --release 11 -encoding UTF-8 -Xlint:all -Werror \
  -d "$BUILD/classes" "$ROOT"/src/main/java/de/matrixgame/lumini/*.java
javac --release 11 -encoding UTF-8 -Xlint:all -Werror \
  -cp "$BUILD/classes" -d "$BUILD/test-classes" \
  "$ROOT"/src/test/java/de/matrixgame/lumini/*.java
if [ "$#" -gt 0 ] && [ "$1" = "--jsonl-only" ]; then
  java -ea -Dfile.encoding=UTF-8 -cp "$BUILD/classes:$BUILD/test-classes" \
    de.matrixgame.lumini.IdeaCompilerPipelineSelfTest >&2
else
  java -ea -Dfile.encoding=UTF-8 -cp "$BUILD/classes:$BUILD/test-classes" \
    de.matrixgame.lumini.IdeaCompilerPipelineSelfTest
fi
java -Dfile.encoding=UTF-8 -cp "$BUILD/classes" \
  de.matrixgame.lumini.LearningRoundDemo "$@"
