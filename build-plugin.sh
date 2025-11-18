#!/bin/bash
# Build plugin with auto-incremented version

set -e

echo "🔧 Building Kei Plugin..."

# Increment version
./increment-version.sh

# Build plugin
./gradlew clean buildPlugin --no-daemon

# Show result
VERSION=$(grep "pluginVersion=" gradle.properties | cut -d'=' -f2)
echo ""
echo "✅ Build complete! Version: $VERSION"
echo "📦 Plugin location: build/distributions/Kei-API-Contract-Testing-$VERSION.zip"
