#!/bin/bash
# Gradle startup script for UN*X
export GRADLE_HOME="${GRADLE_HOME:-/c/Users/obai/.gradle/wrapper/dists/gradle-8.5-bin}"
if [ ! -f "$GRADLE_HOME/bin/gradle" ]; then
    echo "Gradle not found, downloading..."
    mkdir -p "$GRADLE_HOME"
    curl -L -o /tmp/gradle.zip https://services.gradle.org/distributions/gradle-8.5-bin.zip
    unzip -q /tmp/gradle.zip -d "$GRADLE_HOME/.."
    mv "$GRADLE_HOME/.."/gradle-8.5/* "$GRADLE_HOME/"
fi
exec "$GRADLE_HOME/bin/gradle" "$@"
