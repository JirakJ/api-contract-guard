plugins {
    id("java")
    id("org.jetbrains.kotlin.jvm") version "1.9.21"
    id("org.jetbrains.intellij") version "1.16.1"
}

group = "com.kei"

// Read version from gradle.properties and auto-increment on build
val pluginVersion: String by project
version = pluginVersion

repositories {
    mavenCentral()
}

dependencies {
    implementation("io.swagger.parser.v3:swagger-parser:2.1.19")
    implementation("com.graphql-java:graphql-java:21.3")
    implementation("com.networknt:json-schema-validator:1.0.87")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.3")
    
    testImplementation("org.junit.jupiter:junit-jupiter:5.10.1")
    testImplementation(kotlin("test"))
}

intellij {
    version.set("2023.2.5")
    type.set("IU")
    
    plugins.set(listOf("java", "JavaScript"))
}

tasks {
    withType<JavaCompile> {
        sourceCompatibility = "17"
        targetCompatibility = "17"
    }
    
    withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
        kotlinOptions.jvmTarget = "17"
    }

    patchPluginXml {
        sinceBuild.set("232")
        untilBuild.set("")
        
        pluginDescription.set("""
            <p><strong>Kei – API Contract Testing</strong></p>
            <p>Real-time OpenAPI & GraphQL validation directly in your IDE.</p>
            <ul>
                <li>✓ Automatic contract discovery (OpenAPI, GraphQL)</li>
                <li>✓ Real-time validation as you code</li>
                <li>✓ Smart quick fixes for contract violations</li>
                <li>✓ Breaking change detection</li>
                <li>✓ Contract diff viewer</li>
            </ul>
            <p>Stop breaking APIs before they reach production. Start shipping faster with confidence.</p>
        """.trimIndent())
        
        changeNotes.set("""
            <h3>2026.8.0</h3>
            <ul>
                <li>The plugin is now completely free — all features unlocked, no license needed.</li>
                <li>Source code is now open source under Apache-2.0: https://github.com/JirakJ/api-contract-guard</li>
            </ul>
            <h3>2026.3.15</h3>
            <ul>
                <li>Quick fixes now create PSI via the public PsiFileFactory API instead of internal JavaScript plugin classes</li>
            </ul>
            <h3>2025.11.1</h3>
            <ul>
                <li>🚀 Initial release of Kei - API Contract Testing</li>
                <li>✓ OpenAPI 3.x & GraphQL contract discovery</li>
                <li>✓ Real-time API validation for TypeScript/JavaScript</li>
                <li>✓ Smart quick fixes for missing fields and type mismatches</li>
                <li>✓ Contract diff viewer with breaking change detection</li>
                <li>✓ Mock server generator from contracts</li>
                <li>✓ Version control integration</li>
                <li>✓ Multi-language support (Java, Kotlin, Python)</li>
            </ul>
        """.trimIndent())
    }

    runPluginVerifier {
        ideVersions.set(listOf("IU-2024.3"))
    }

    signPlugin {
        certificateChain.set(System.getenv("CERTIFICATE_CHAIN"))
        privateKey.set(System.getenv("PRIVATE_KEY"))
        password.set(System.getenv("PRIVATE_KEY_PASSWORD"))
    }

    publishPlugin {
        token.set(System.getenv("PLUGIN_TOKEN"))
    }
}

kotlin {
    jvmToolchain(17)
}
