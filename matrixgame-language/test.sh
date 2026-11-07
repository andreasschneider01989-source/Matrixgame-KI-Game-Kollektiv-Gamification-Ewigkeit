#!/usr/bin/env sh
set -eu
cd "$(dirname "$0")"
./build.sh
find src/test/java -name '*.java' -print > build/test-sources.txt
javac --release 21 -Xlint:all -cp build/classes -d build/classes @build/test-sources.txt
java -ea -cp build/classes de.matrixgame.language.LuminiCompilerSelfTest
