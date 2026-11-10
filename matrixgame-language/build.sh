#!/usr/bin/env sh
set -eu
cd "$(dirname "$0")"
rm -rf build
mkdir -p build/classes
find src/main/java -name '*.java' -print > build/main-sources.txt
javac --release 21 -Xlint:all -d build/classes @build/main-sources.txt
echo "LUMINI compiler build successful."
