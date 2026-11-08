#!/usr/bin/env sh
set -eu
cd "$(dirname "$0")"
sh build.sh
find src/test/java -name '*.java' -print > build/test-sources.txt
javac --release 21 -Xlint:all -cp build/classes -d build/test-classes @build/test-sources.txt
java -cp build/classes:build/test-classes de.matrixgame.earth.GaiaWaterBucketSelfTest
java -cp build/classes:build/test-classes de.matrixgame.earth.GaiaForestWaterCycleSelfTest
