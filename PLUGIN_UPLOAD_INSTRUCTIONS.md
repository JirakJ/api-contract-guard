# Kei Plugin - Upload Instructions

## 📦 Plugin Ready for Upload!

**Plugin File**: `build/distributions/kei-plugin-2025.1.6.zip`
**Version**: 2025.1.6
**Product Code**: PKEIAPICONTRACT
**Pricing Model**: FREEMIUM

---

## 🚀 Upload to JetBrains Marketplace

### Step 1: Go to Plugin Upload Page
Visit: https://plugins.jetbrains.com/plugin/add

### Step 2: Upload Plugin
1. Click **"Upload Plugin"**
2. Select file: `build/distributions/kei-plugin-2025.1.6.zip`
3. Fill in the details:

### Step 3: Plugin Details

**Name**: Kei - API Contract Testing

**Category**: Testing Tools

**Description**:
```
Real-time OpenAPI & GraphQL validation directly in your IDE. 
Prevent API breaking changes before they reach production.

Features:
• Automatic contract discovery (OpenAPI, GraphQL)
• Real-time code validation with instant feedback
• Smart quick fixes for contract violations
• Mock server generator
• Breaking change detection
• Multi-language support (TypeScript, Python, Java, Kotlin)
• Version control integration

FREEMIUM model with premium features available.
```

**Tags**: 
- api
- contract
- openapi
- swagger
- graphql
- testing
- validation
- rest
- pact

**License**: MIT (or your preferred license)

**Source Code URL**: https://github.com/JirakJ/api-contract-guard

---

## 📸 Screenshots Needed

Before publishing, add these screenshots:

1. **Contract Explorer** - Tool window showing discovered contracts
2. **Real-time Validation** - Code editor with inline warnings
3. **Quick Fixes** - Alt+Enter menu with suggested fixes
4. **Diff Viewer** - Breaking change comparison
5. **Mock Server** - Running mock server panel

---

## ✅ After Upload

Once uploaded, you can use automated publishing:

```bash
PUBLISH_TOKEN="your-token" ./gradlew publishPlugin
```

This will automatically publish future versions!

---

## 🎨 Plugin Icons

Icons are included in the plugin:
- Light theme: `src/main/resources/META-INF/pluginIcon.svg`
- Dark theme: `src/main/resources/META-INF/pluginIcon_dark.svg`

The icons feature a shield with a checkmark (representing API contract protection).

---

## 🔑 Monetization Setup

After upload, configure monetization:

1. Go to plugin page → **Monetization** tab
2. Set **Pricing Model**: FREEMIUM
3. Set **Product Code**: PKEIAPICONTRACT
4. Configure free vs premium features

### Free Tier Features:
- Basic contract discovery
- OpenAPI validation
- Real-time warnings
- 3 contracts max

### Premium Features:
- Unlimited contracts
- GraphQL support
- Mock server
- Breaking change detection
- Team collaboration
- Advanced analytics
- Security scanning
- Performance testing
- CI/CD integration

---

## 📝 Version Management

Auto-increment version for each build:

```bash
./increment-version.sh
./gradlew buildPlugin
```

---

## 🐛 Current Build Info

**Build Date**: 2025-11-18
**IntelliJ Platform**: 2023.2+
**Kotlin**: 1.9.20
**Gradle**: 8.5

**Dependencies**:
- swagger-parser 2.1.22
- graphql-java 21.0
- json-schema-validator 1.5.1
- ktor-server-core 2.3.12
- kotlin-faker 1.16.0

---

## 📧 Support

For questions: marketplace@jetbrains.com

---

**Good luck with the launch! 🚀**
