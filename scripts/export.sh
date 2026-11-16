#!/usr/bin/env bash
set -euo pipefail
cd "$(dirname "$0")/.."
git diff --quiet
git diff --cached --quiet
if [[ -n "$(git ls-files --others --exclude-standard)" ]]; then
  echo "Commit source files before exporting." >&2
  exit 1
fi
mkdir -p dist
cp app/build/outputs/apk/debug/app-debug.apk dist/Simple-debug.apk
cp app/build/outputs/apk/androidTest/debug/app-debug-androidTest.apk dist/Simple-tests.apk
git bundle create dist/Simple.bundle --all
git bundle verify dist/Simple.bundle
git archive --format=zip --output=dist/Simple-source.zip HEAD
(cd dist && sha256sum Simple-debug.apk Simple-tests.apk Simple.bundle Simple-source.zip > SHA256SUMS)
