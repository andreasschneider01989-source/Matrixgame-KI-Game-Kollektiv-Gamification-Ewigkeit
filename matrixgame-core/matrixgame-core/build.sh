#!/usr/bin/env bash
# Kompiliert alles nach ./out. Nutzt javac (JDK 21+), sonst das jdk.compiler-Modul (JRE-only-Systeme).
set -euo pipefail
cd "$(dirname "$0")"
rm -rf out && mkdir -p out
if command -v javac >/dev/null 2>&1; then
  JAVAC=(javac --release 21)
else
  JAVAC=(java -m jdk.compiler/com.sun.tools.javac.Main)   # --release braucht ct.sym, das hier fehlt
fi
"${JAVAC[@]}" -Xlint:all -d out $(find src test -name '*.java')
echo "Build ok -> out/"
