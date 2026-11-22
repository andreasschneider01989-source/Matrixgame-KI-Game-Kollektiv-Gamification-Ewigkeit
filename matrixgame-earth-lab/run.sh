#!/usr/bin/env sh
set -eu
cd "$(dirname "$0")"
sh build.sh
java -cp build/classes de.matrixgame.earth.GaiaWaterBucketDemo
