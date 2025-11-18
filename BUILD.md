# 🔨 KEI PLUGIN - BUILD & DEPLOYMENT GUIDE

## Prerequisites

### Required Software
- **JDK 17 or higher** ([Download](https://adoptium.net/))
- **IntelliJ IDEA 2023.1+** ([Download](https://www.jetbrains.com/idea/download/))
- **Gradle 8.0+** (included via wrapper)
- **Git** ([Download](https://git-scm.com/))

### Verify Installation
```bash
java -version   # Should show 17+
gradle --version # Should show 8.0+
```

---

## Quick Start

### 1. Clone Repository
```bash
git clone https://github.com/jirakj/api-contract-guard.git
cd api-contract-guard
```

### 2. Build Plugin
```bash
./gradlew build
```

Expected output:
```
BUILD SUCCESSFUL in 45s
20 actionable tasks: 20 executed
```

### 3. Run Plugin in Test IDE
```bash
./gradlew runIde
```

This will:
- Download IntelliJ IDEA IDE
- Install your plugin
- Launch test IDE with plugin active

### 4. Test Plugin
In the test IDE:
1. Open `example-project/`
2. Plugin should auto-discover `openapi.yaml`
3. Open `example-project/src/UserService.ts`
4. See validation warnings

---

## Build Commands

### Clean Build
```bash
./gradlew clean build
```

### Build Plugin ZIP
```bash
./gradlew buildPlugin
```

Output: `build/distributions/api-contract-guard-1.0.0.zip`

### Run Tests
```bash
./gradlew test
```

### Run with Logging
```bash
./gradlew runIde --info
```

### Build for Specific IDE Version
```bash
./gradlew runIde -PideaVersion=2023.2
```

---

## Development Workflow

### 1. Make Code Changes
Edit files in `src/main/kotlin/`

### 2. Hot Reload (if supported)
Some changes don't require restart:
- UI changes
- Inspection text
- Quick fix descriptions

Major changes require restart:
- New inspections
- PSI visitors
- Service changes

### 3. Test Changes
```bash
# Run unit tests
./gradlew test

# Run in test IDE
./gradlew runIde
```

### 4. Verify Build
```bash
./gradlew verifyPlugin
```

This checks:
- Plugin structure
- Manifest validity
- Compatibility

---

## Debugging

### Debug in IDE
1. Run `./gradlew runIde --debug-jvm`
2. In your main IntelliJ:
   - Run → Edit Configurations
   - Add "Remote JVM Debug"
   - Host: localhost, Port: 5005
   - Click Debug

### View Logs
Test IDE logs location:
```
build/idea-sandbox/system/log/idea.log
```

Tail logs:
```bash
tail -f build/idea-sandbox/system/log/idea.log
```

### Common Issues

#### Issue: "Cannot find JDK 17"
```bash
# Set JAVA_HOME
export JAVA_HOME=/path/to/jdk17
```

#### Issue: "Plugin verification failed"
```bash
# Clean and rebuild
./gradlew clean verifyPlugin
```

#### Issue: "Test IDE won't start"
```bash
# Delete sandbox
rm -rf build/idea-sandbox/
./gradlew runIde
```

---

## Testing

### Unit Tests
```bash
./gradlew test --tests "ContractParserTest"
```

### Test Coverage
```bash
./gradlew test jacocoTestReport
open build/reports/jacoco/test/html/index.html
```

Target: 80%+ coverage

### Integration Tests
```bash
./gradlew integrationTest
```

### Performance Tests
```bash
./gradlew performanceTest
```

---

## Packaging

### Build for Marketplace
```bash
./gradlew buildPlugin
```

Output: `build/distributions/api-contract-guard-1.0.0.zip`

### Verify Package
```bash
./gradlew verifyPlugin
```

Checks:
- Plugin.xml validity
- Icon formats
- File structure
- Size limits

### Sign Plugin (Enterprise)
```bash
./gradlew signPlugin \
  -Psigning.privateKey=/path/to/private.key \
  -Psigning.certificateChain=/path/to/chain.crt
```

---

## Deployment

### Local Installation
1. Build plugin: `./gradlew buildPlugin`
2. In IntelliJ: Settings → Plugins
3. Click ⚙️ → Install Plugin from Disk
4. Select `build/distributions/api-contract-guard-1.0.0.zip`
5. Restart IDE

### JetBrains Marketplace

#### 1. Create Account
- Go to [JetBrains Marketplace](https://plugins.jetbrains.com/)
- Create developer account
- Verify email

#### 2. Upload Plugin
```bash
# Build
./gradlew buildPlugin

# Upload manually via web UI or:
./gradlew publishPlugin \
  -Ppublish.token=YOUR_TOKEN
```

#### 3. Fill Metadata
- Name: Kei - API Contract Testing
- Category: Testing Tools
- Tags: api, contract, openapi, graphql
- Description: (from README.md)
- Screenshots: 3-5 images
- Demo video: YouTube link

#### 4. Submit for Review
- Approval time: 2-5 business days
- Check for rejections/feedback

---

## Continuous Integration

### GitHub Actions
`.github/workflows/build.yml`:
```yaml
name: Build
on: [push, pull_request]

jobs:
  build:
    runs-on: ubuntu-latest
    steps:
    - uses: actions/checkout@v3
    - uses: actions/setup-java@v3
      with:
        java-version: '17'
    - run: ./gradlew build test
    - run: ./gradlew verifyPlugin
```

### Automated Publishing
`.github/workflows/publish.yml`:
```yaml
name: Publish
on:
  release:
    types: [created]

jobs:
  publish:
    runs-on: ubuntu-latest
    steps:
    - uses: actions/checkout@v3
    - uses: actions/setup-java@v3
      with:
        java-version: '17'
    - run: ./gradlew publishPlugin
      env:
        PUBLISH_TOKEN: ${{ secrets.JETBRAINS_TOKEN }}
```

---

## Version Management

### Update Version
Edit `gradle.properties`:
```properties
pluginVersion=1.0.1
```

### Semantic Versioning
- **1.0.0** - Major release
- **1.1.0** - Minor (new features)
- **1.0.1** - Patch (bug fixes)

### Changelog
Update `CHANGELOG.md`:
```markdown
## [1.0.1] - 2024-01-15
### Fixed
- Contract parser null pointer exception
- Mock server port conflict

### Added
- Support for OpenAPI 3.1
```

---

## Performance Optimization

### Build Speed
```bash
# Enable parallel builds
gradle.properties:
org.gradle.parallel=true
org.gradle.caching=true
```

### Plugin Size
```bash
# Check size
du -sh build/distributions/*.zip

# Should be < 50MB
```

### Startup Time
Target: Plugin should load in <1 second

Profile startup:
```bash
./gradlew runIde -Didea.log.startup.timing=true
```

---

## Release Checklist

### Pre-Release
- [ ] All tests passing
- [ ] Test coverage >80%
- [ ] Performance benchmarks met
- [ ] Documentation updated
- [ ] CHANGELOG.md updated
- [ ] Version bumped

### Build
- [ ] `./gradlew clean build`
- [ ] `./gradlew verifyPlugin`
- [ ] `./gradlew buildPlugin`
- [ ] Test installation locally

### Publish
- [ ] Upload to JetBrains Marketplace
- [ ] Create GitHub release
- [ ] Tag version in Git
- [ ] Update website

### Post-Release
- [ ] Monitor error reports
- [ ] Respond to reviews
- [ ] Announce on social media
- [ ] Update roadmap

---

## Troubleshooting

### Build Fails
```bash
# Clean everything
./gradlew clean
rm -rf build/
rm -rf .gradle/

# Rebuild
./gradlew build
```

### Plugin Won't Load
Check `idea.log` for:
```
ClassNotFoundException
NoSuchMethodError
```

Fix: Update dependencies in `build.gradle.kts`

### Slow Performance
Profile with YourKit:
```bash
./gradlew runIde \
  -Didea.profiler.yourkit.home=/path/to/yourkit
```

---

## Support & Resources

### Documentation
- [IntelliJ Platform SDK](https://plugins.jetbrains.com/docs/intellij/)
- [Gradle IntelliJ Plugin](https://github.com/JetBrains/gradle-intellij-plugin)
- [Plugin Verification](https://github.com/JetBrains/intellij-plugin-verifier)

### Community
- [JetBrains Slack](https://plugins.jetbrains.com/slack)
- [Platform Discuss](https://intellij-support.jetbrains.com/hc/en-us/community/topics)
- [GitHub Issues](https://github.com/jirakj/api-contract-guard/issues)

---

## Quick Reference

```bash
# Build
./gradlew build

# Run
./gradlew runIde

# Test
./gradlew test

# Package
./gradlew buildPlugin

# Publish
./gradlew publishPlugin -Ppublish.token=TOKEN

# Clean
./gradlew clean

# Verify
./gradlew verifyPlugin
```

---

**Happy Building! 🚀**
