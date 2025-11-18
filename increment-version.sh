#!/bin/bash
# Auto-increment plugin version
# Format: YEAR.RELEASE.PATCH (e.g., 2025.1.1)
# release-version in plugin.xml must be YEAR + RELEASE (e.g., 20251)

PROPS_FILE="gradle.properties"

# Read current version
CURRENT_VERSION=$(grep "pluginVersion=" $PROPS_FILE | cut -d'=' -f2)

# Extract parts (YEAR.RELEASE.PATCH)
YEAR=$(echo $CURRENT_VERSION | cut -d'.' -f1)
RELEASE=$(echo $CURRENT_VERSION | cut -d'.' -f2)
PATCH=$(echo $CURRENT_VERSION | cut -d'.' -f3)

# Increment patch number
NEW_PATCH=$((PATCH + 1))
NEW_VERSION="$YEAR.$RELEASE.$NEW_PATCH"

# Update gradle.properties
sed -i.bak "s/pluginVersion=.*/pluginVersion=$NEW_VERSION/" $PROPS_FILE
rm -f $PROPS_FILE.bak

echo "Version incremented: $CURRENT_VERSION → $NEW_VERSION"
echo "Release version for plugin.xml: $YEAR$RELEASE"
