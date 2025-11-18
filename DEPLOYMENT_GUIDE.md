# 🚀 Kei Plugin - Deployment Guide

## ✅ Build Complete

**Version**: 2025.11.2  
**Product Code**: PKEIAPICONTRACT  
**Pricing Model**: FREEMIUM  
**Build Date**: 2025-11-18

---

## 📦 Plugin Artifact

```
Location: build/distributions/Kei-API-Contract-Testing-2025.11.2.zip
Size: ~15-20 MB
Compatible IDEs: IntelliJ IDEA Ultimate 2023.2+, WebStorm, PyCharm Professional, GoLand
```

---

## 🔧 Build Commands

### Build Plugin with Auto-Increment
```bash
./build-plugin.sh
```

### Manual Version Increment
```bash
./increment-version.sh
```

### Manual Build (without increment)
```bash
./gradlew buildPlugin
```

---

## 📋 Version Management

**Current Version**: 2025.11.2  
**Version Format**: YYYY.MM.BUILD  
**Auto-increment**: Every build automatically increments BUILD number

**Version History**:
- 2025.11.1 - Initial release
- 2025.11.2 - Current build

---

## 🎯 Freemium Feature Tiers

### 🆓 FREE Tier (Community)
**No limits on:**
- ✅ Contract discovery (OpenAPI, GraphQL)
- ✅ Real-time validation
- ✅ Inline warnings
- ✅ Basic quick fixes
- ✅ Contract explorer UI
- ✅ Breaking change detection (basic)

### 💎 PREMIUM Tier
**Unlocked with license: PKEIAPICONTRACT**
- ✅ Advanced quick fixes & auto-refactoring
- ✅ Mock server generator
- ✅ Consumer-driven contract testing
- ✅ Dependency graph visualization
- ✅ Automated test generation
- ✅ Git integration (pre-commit hooks)
- ✅ Contract diff viewer (advanced)
- ✅ Security analyzer (OWASP API Security)
- ✅ Performance testing integration
- ✅ AI-powered contract generation
- ✅ Team collaboration features
- ✅ Multi-protocol support (gRPC, SOAP, AsyncAPI)
- ✅ API analytics & monitoring
- ✅ CI/CD integration
- ✅ Enterprise SSO

---

## 🏪 JetBrains Marketplace Submission

### Checklist
- [x] Plugin built successfully
- [x] Product code configured: PKEIAPICONTRACT
- [x] Freemium model implemented
- [x] Version auto-increment working
- [x] Git repository pushed
- [ ] Create JetBrains Marketplace account
- [ ] Submit plugin for approval
- [ ] Configure pricing tiers
- [ ] Add screenshots & demo video
- [ ] Write marketplace description
- [ ] Set up support channels

### Marketplace Links
- **Upload Plugin**: https://plugins.jetbrains.com/plugin/add
- **Vendor Profile**: https://plugins.jetbrains.com/vendors
- **Pricing Config**: https://plugins.jetbrains.com/plugin/{id}/pricing

---

## 📸 Required Assets for Marketplace

### Screenshots (1280x800 recommended)
1. **Contract Explorer** - Show OpenAPI/GraphQL tree view
2. **Real-time Validation** - Code with inline warnings
3. **Quick Fixes** - Alt+Enter menu with fixes
4. **Breaking Change Detection** - Warning dialog
5. **Mock Server** - Running server panel
6. **Dependency Graph** - Service dependencies visualization

### Demo Video (2-3 minutes)
1. Install plugin
2. Open project with OpenAPI spec
3. Show contract discovery
4. Make API call with violation
5. Use quick fix to resolve
6. Show premium features (mock server, graph)

### Icon Requirements
- **Plugin Icon**: 40x40, 80x80, 160x160 (SVG preferred)
- Already created in `src/main/resources/icons/`

---

## 🔐 License Configuration

### Product Code
```
PKEIAPICONTRACT
```

### License Types
1. **FREE** - No license required, limited features
2. **PERSONAL** - Individual developer
3. **COMMERCIAL** - Business use
4. **ORGANIZATIONAL** - Multi-seat license

### Pricing Strategy (Recommended)
- **Personal**: $99/year (1 developer)
- **Commercial**: $199/year/developer
- **Organizational**: $149/year/developer (10+ seats)
- **Enterprise**: Custom pricing (50+ seats, SSO, on-prem)

---

## 📊 Post-Launch Metrics

### Track These KPIs
- **Downloads**: Target 1,000 in first month
- **Active Installations**: Target 40% activation rate
- **Premium Conversions**: Target 10% conversion rate
- **User Ratings**: Target 4.5+ stars
- **Support Tickets**: Response time < 24h

### Analytics Integration
```kotlin
// Already implemented in AnalyticsService.kt
AnalyticsService.getInstance(project).trackEvent("feature_used", mapOf(
    "feature" to "contract_validation",
    "tier" to "free"
))
```

---

## 🐛 Bug Reporting

### GitHub Issues
https://github.com/JirakJ/api-contract-guard/issues

### Support Channels
- Email: support@kei.dev
- Discord: (TBD)
- Slack: (TBD)

---

## 🚢 Next Steps

### Week 1: Pre-Launch
1. [ ] Create demo video (3 min)
2. [ ] Take screenshots for marketplace
3. [ ] Write marketing copy (landing page)
4. [ ] Set up analytics tracking
5. [ ] Create support documentation

### Week 2: Marketplace Submission
1. [ ] Submit to JetBrains Marketplace
2. [ ] Configure pricing tiers
3. [ ] Set up payment processing
4. [ ] Create vendor profile

### Week 3: Launch
1. [ ] Announce on social media (LinkedIn, Twitter, Dev.to)
2. [ ] Post on HackerNews (Show HN)
3. [ ] Post on Reddit (/r/programming, /r/devops)
4. [ ] Reach out to beta testers
5. [ ] Monitor feedback & bug reports

### Week 4: Post-Launch
1. [ ] Analyze metrics (downloads, conversions)
2. [ ] Fix critical bugs (if any)
3. [ ] Collect user feedback
4. [ ] Plan v2025.12.x features

---

## 🎓 Resources

### Documentation
- **Developer Guide**: `DEV_GUIDE.md`
- **Feature Summary**: `FEATURE_SUMMARY.md`
- **Premium Features**: `PREMIUM_FEATURES.md`
- **Quick Start**: `QUICKSTART.md`

### Links
- **GitHub**: https://github.com/JirakJ/api-contract-guard
- **Plugin Zip**: `build/distributions/Kei-API-Contract-Testing-2025.11.2.zip`

---

## 💡 Tips for Success

1. **Start with FREE tier** - Get users hooked with core features
2. **Premium features must WOW** - Make them clearly valuable
3. **Fast iteration** - Listen to user feedback, release weekly
4. **Great docs** - Clear, visual, with examples
5. **Community building** - Discord/Slack for power users
6. **Content marketing** - Blog posts, tutorials, case studies

---

**Ready to launch?** 🚀

Next command:
```bash
# Build and publish to marketplace
./build-plugin.sh
# Then upload build/distributions/Kei-API-Contract-Testing-*.zip to JetBrains Marketplace
```
