#!/usr/bin/env sh
set -eu
cd "$(dirname "$0")"
[ -d build/classes ] || ./build.sh
java -cp build/classes de.matrixgame.language.LuminiCompiler "$@"
