#!/usr/bin/env bash
# ./run.sh [AnzahlSeeds]   -> Arena laufen lassen und results/ARENA.md schreiben
set -euo pipefail
cd "$(dirname "$0")"
[ -d out ] || ./build.sh
java -cp out matrixgame.arena.Arena "$@"
