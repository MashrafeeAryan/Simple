#!/usr/bin/env bash
set -euo pipefail
cd "$(dirname "$0")/.."
if [[ -f /workspace/tools/java-env.sh ]]; then source /workspace/tools/java-env.sh; fi
if [[ -x /workspace/tools/gradle-8.10.2/bin/gradle ]]; then GRADLE=/workspace/tools/gradle-8.10.2/bin/gradle; else GRADLE=./gradlew; fi
"$GRADLE" --no-daemon --max-workers=2 :app:assembleDebug :app:testDebugUnitTest :app:lintDebug
(cd tests && npm ci --cache /workspace/tools/npm-cache --ignore-scripts && npm test)
