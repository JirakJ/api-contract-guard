# 🛠️ Kei Plugin - Developer Quick Reference

**Fast access to everything you need for development**

---

## 🚀 Quick Start

### Build & Run
```bash
# Build plugin
./gradlew buildPlugin

# Run in IDE (launches IntelliJ with plugin installed)
./gradlew runIde

# Run tests
./gradlew test

# Generate documentation
./gradlew dokkaHtml
```

### Project Structure
```
src/main/kotlin/com/github/jirakj/kei/
├── core/           # Contract parsing & indexing
├── validation/     # Real-time validation engine
├── mockserver/     # Embedded mock server
├── testing/        # Test generation
├── vcs/            # Git integration
├── ui/             # UI components
└── models/         # Data models
```

---

## 📝 Code Conventions

### Naming
- **Classes**: PascalCase (`ContractParser`)
- **Functions**: camelCase (`parseContract()`)
- **Constants**: UPPER_SNAKE_CASE (`MAX_CONTRACTS`)
- **Private fields**: underscore prefix (`_cache`)

### Documentation
```kotlin
/**
 * Parses an OpenAPI contract from a file.
 *
 * @param file The YAML/JSON file containing the contract
 * @return Parsed ApiContract object
 * @throws UnsupportedContractFormat if file format is invalid
 */
fun parseContract(file: VirtualFile): ApiContract
```

### Error Handling
```kotlin
// Good: Specific exceptions
throw ContractParsingException("Invalid OpenAPI format in ${file.name}")

// Bad: Generic exceptions
throw Exception("Something went wrong")
```

---

## 🧪 Testing

### Unit Tests
```kotlin
class ContractParserTest {
    @Test
    fun `should parse OpenAPI 3_0 successfully`() {
        val file = loadTestResource("openapi-valid.yaml")
        val contract = ContractParser().parse(file)
        
        assertEquals(ContractType.OPENAPI, contract.type)
        assertEquals(5, contract.endpoints.size)
    }
}
```

### Integration Tests
```kotlin
@Test
fun `should validate TypeScript fetch call against contract`() {
    val project = myFixture.project
    val contract = loadContract("petstore.yaml")
    
    val file = myFixture.addFileToProject(
        "test.ts",
        """
        fetch('/api/pets', {
            method: 'POST',
            body: JSON.stringify({ name: 'Fluffy' })
        })
        """.trimIndent()
    )
    
    val inspections = InspectionEngine.runInspections(file, contract)
    assertTrue(inspections.any { it.type == ViolationType.MISSING_REQUIRED_FIELD })
}
```

---

## 🎨 UI Development

### Tool Window
```kotlin
class ContractExplorerWindow(project: Project) : SimpleToolWindowPanel(true, true) {
    private val tree = Tree()
    
    init {
        val scrollPane = JBScrollPane(tree)
        setContent(scrollPane)
        
        // Populate tree
        val root = DefaultMutableTreeNode("Contracts")
        ContractIndex.getContracts(project).forEach { contract ->
            val node = DefaultMutableTreeNode(contract.file.name)
            root.add(node)
        }
        tree.model = DefaultTreeModel(root)
    }
}
```

### Settings Panel
```kotlin
class KeiSettingsPanel : BoundConfigurable("Kei") {
    override fun createPanel(): DialogPanel = panel {
        row("Mock Server Port:") {
            intTextField(MockServerConfig.port, IntRange(3000, 9999))
        }
        row("Validation Enabled:") {
            checkBox("", ValidationConfig.enabled)
        }
    }
}
```

---

## 🔧 Key Classes Reference

### ContractParser
```kotlin
// Usage
val parser = ContractParser()
val contract = parser.parse(virtualFile)

// Supported formats
- OpenAPI 2.0, 3.0, 3.1 (YAML, JSON)
- GraphQL (.graphql, .gql)
- gRPC (.proto)
```

### ValidationEngine
```kotlin
// Usage
val engine = ValidationEngine(project)
val violations = engine.validate(psiElement, contract)

// Returns list of violations
violations.forEach { violation ->
    holder.registerProblem(
        violation.element,
        violation.message,
        ProblemHighlightType.ERROR
    )
}
```

### MockServerManager
```kotlin
// Start mock server
val manager = MockServerManager(project)
manager.start(contract, port = 3000)

// Stop mock server
manager.stop()

// Check status
val isRunning = manager.isRunning()
```

### ContractIndex
```kotlin
// Get all contracts
val contracts = ContractIndex.getContracts(project)

// Find contract by endpoint
val contract = ContractIndex.findContract(endpoint, method)

// Refresh index
ContractIndex.refresh(project)
```

---

## 🐛 Debugging

### Enable Debug Logging
```kotlin
// Add to plugin.xml
<extensions defaultExtensionNs="com.intellij">
    <applicationConfigurable instance="com.github.jirakj.kei.settings.KeiSettings"/>
</extensions>

// In code
private val logger = Logger.getInstance(this::class.java)
logger.debug("Parsing contract: ${file.name}")
logger.warn("Contract validation failed")
logger.error("Failed to start mock server", exception)
```

### Common Issues

**Issue**: Plugin not loading
```bash
# Check build output
./gradlew buildPlugin --info

# Verify plugin.xml
# Must have: <id>, <name>, <version>, <vendor>
```

**Issue**: Validation not working
```kotlin
// Check contract is indexed
val contracts = ContractIndex.getContracts(project)
logger.debug("Found ${contracts.size} contracts")

// Check PSI tree
psiFile.accept(object : PsiRecursiveElementVisitor() {
    override fun visitElement(element: PsiElement) {
        logger.debug("Element: ${element.text}")
        super.visitElement(element)
    }
})
```

**Issue**: Mock server not starting
```kotlin
// Check port availability
val port = MockServerConfig.port
if (isPortInUse(port)) {
    logger.error("Port $port already in use")
    // Show notification
    NotificationUtils.showError(project, "Port $port is busy")
}
```

---

## 📊 Performance Tips

### Caching
```kotlin
// Use CachedValue for expensive operations
private val contractCache = CachedValuesManager.getCachedValue(project) {
    CachedValueProvider.Result.create(
        ContractIndex.getContracts(project),
        ModificationTracker.EVER_CHANGED
    )
}
```

### Async Operations
```kotlin
// Use coroutines for long-running tasks
GlobalScope.launch(Dispatchers.IO) {
    val result = performExpensiveOperation()
    withContext(Dispatchers.EDT) {
        updateUI(result)
    }
}
```

### Background Tasks
```kotlin
// Use ProgressManager for background tasks
ProgressManager.getInstance().run(object : Task.Backgroundable(project, "Validating Contracts") {
    override fun run(indicator: ProgressIndicator) {
        indicator.text = "Scanning project..."
        val contracts = ContractIndex.getContracts(project)
        
        contracts.forEachIndexed { index, contract ->
            indicator.fraction = index.toDouble() / contracts.size
            indicator.text = "Validating ${contract.file.name}"
            validate(contract)
        }
    }
})
```

---

## 🔌 Extension Points

### Add New Inspection
```kotlin
// 1. Create inspection class
class MyCustomInspection : LocalInspectionTool() {
    override fun buildVisitor(holder: ProblemsHolder, isOnTheFly: Boolean): PsiElementVisitor {
        return MyCustomVisitor(holder)
    }
}

// 2. Register in plugin.xml
<extensions defaultExtensionNs="com.intellij">
    <localInspection
        language="TypeScript"
        implementationClass="com.github.jirakj.kei.inspections.MyCustomInspection"
        displayName="My Custom Inspection"
        groupName="Kei"
        enabledByDefault="true"
        level="ERROR"/>
</extensions>
```

### Add New Protocol Support
```kotlin
// 1. Implement ContractHandler interface
class AsyncApiHandler : ContractHandler {
    override fun canHandle(file: VirtualFile): Boolean {
        return file.name.endsWith(".asyncapi.yaml")
    }
    
    override fun parse(file: VirtualFile): ApiContract {
        // Parse AsyncAPI spec
        val content = VfsUtil.loadText(file)
        // ... parsing logic
        return ApiContract(...)
    }
}

// 2. Register in ContractHandlerFactory
object ContractHandlerFactory {
    private val handlers = listOf(
        OpenApiHandler(),
        GraphQLHandler(),
        GrpcHandler(),
        AsyncApiHandler() // Add here
    )
}
```

### Add New Quick Fix
```kotlin
// 1. Create quick fix class
class MyCustomFix : LocalQuickFix {
    override fun getName() = "My Custom Fix"
    override fun getFamilyName() = "Kei"
    
    override fun applyFix(project: Project, descriptor: ProblemDescriptor) {
        val element = descriptor.psiElement
        // Apply fix logic
    }
}

// 2. Return from inspection
holder.registerProblem(
    element,
    "Problem description",
    ProblemHighlightType.ERROR,
    MyCustomFix()
)
```

---

## 📦 Dependencies

### Core
```kotlin
// build.gradle.kts
dependencies {
    implementation("io.swagger.parser.v3:swagger-parser:2.1.16")
    implementation("com.graphql-java:graphql-java:21.0")
    implementation("io.ktor:ktor-server-core:2.3.6")
    implementation("io.ktor:ktor-server-netty:2.3.6")
    implementation("io.github.serpro69:kotlin-faker:1.15.0")
    implementation("com.networknt:json-schema-validator:1.0.87")
    implementation("org.eclipse.jgit:org.eclipse.jgit:6.7.0.202309050840-r")
}
```

### Testing
```kotlin
testImplementation("org.jetbrains.kotlin:kotlin-test")
testImplementation("io.mockk:mockk:1.13.8")
testImplementation("io.kotest:kotest-assertions-core:5.8.0")
```

---

## 🚀 Release Process

### 1. Version Bump
```kotlin
// gradle.properties
version=1.0.1

// plugin.xml
<version>1.0.1</version>
```

### 2. Changelog
```markdown
## [1.0.1] - 2024-11-20
### Added
- GraphQL subscription support
- Performance improvements

### Fixed
- Mock server port conflict
- TypeScript validation edge case
```

### 3. Build & Test
```bash
./gradlew clean test buildPlugin

# Verify plugin.zip
unzip -l build/distributions/kei-1.0.1.zip
```

### 4. Upload to JetBrains Marketplace
```bash
# Manual: https://plugins.jetbrains.com/plugin/add
# Or via Gradle:
./gradlew publishPlugin \
    -Pplugin.verifier.home.dir=~/.pluginVerifier \
    -PjetbrainsToken=$TOKEN
```

---

## 📚 Useful Resources

### IntelliJ Platform SDK
- [Official Docs](https://plugins.jetbrains.com/docs/intellij/)
- [PSI Viewer](https://plugins.jetbrains.com/plugin/227-psiviewer)
- [Platform API](https://github.com/JetBrains/intellij-community)

### Testing
- [Test Framework](https://plugins.jetbrains.com/docs/intellij/testing-plugins.html)
- [Test Fixtures](https://plugins.jetbrains.com/docs/intellij/testing-plugins.html#tests-with-testdatapath)

### UI
- [UI Guidelines](https://jetbrains.design/intellij/)
- [Swing Components](https://docs.oracle.com/javase/tutorial/uiswing/)

---

## 💡 Tips & Tricks

### 1. Fast Iteration
```bash
# Use runIde with auto-reload
./gradlew runIde --continuous
```

### 2. Debug Plugin in IDE
```kotlin
// Set breakpoint in code
// Run "Plugin" configuration in debug mode
// Trigger action in guest IDE
```

### 3. Profile Performance
```kotlin
import com.intellij.openapi.diagnostic.PerformanceWatcher

val watcher = PerformanceWatcher.getInstance()
watcher.start("Contract Validation")
// ... your code
watcher.stop()
```

### 4. Inspect PSI Structure
```kotlin
// Right-click in editor → Show Context Actions → View PSI Structure
// Or use PsiViewer plugin
```

---

## 🆘 Getting Help

### Internal
- Check `docs/` folder
- Search codebase for examples
- Run tests for usage examples

### External
- [JetBrains Plugin Developers](https://plugins.jetbrains.com/slack)
- [Stack Overflow](https://stackoverflow.com/questions/tagged/intellij-plugin)
- [JetBrains Forum](https://intellij-support.jetbrains.com/hc/en-us/community/topics/200366979-IntelliJ-IDEA-Open-API-and-Plugin-Development)

---

**Last Updated**: November 18, 2024  
**Maintainer**: Jiri Kafka

Happy coding! 🚀
