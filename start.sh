#!/bin/bash
echo "🏥 Audino Launcher"
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
cd "$SCRIPT_DIR"

if [ ! -f "target/audino-1.1.0.jar" ]; then
    echo "❌ Application not found. Run: ./setup.sh"
    exit 1
fi

if command -v mvn >/dev/null 2>&1; then
    mvn javafx:run
else
    java -jar target/audino-1.1.0.jar
fi
