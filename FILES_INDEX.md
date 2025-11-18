# 📁 Kei Plugin - Index všech souborů

## 🎯 Rychlý přehled

**Celkem souborů:** 32  
**Kotlin kód:** 12 souborů (1,062 LOC)  
**Dokumentace:** 9 souborů (56 KB)  
**Příklady:** 7 souborů  
**Konfigurace:** 4 XML soubory  

---

## 💻 ZDROJOVÝ KÓD (12 souborů)

### Core Logic (3 soubory, 321 LOC)
```
src/main/kotlin/com/kei/plugin/core/
├── ContractParser.kt              143 řádků
│   └── OpenAPI 3.x parser, schema extraction
├── ContractIndexService.kt        141 řádků
│   └── Contract caching, file watching, fast lookup
└── ContractDiscoveryStartup.kt     37 řádků
    └── Auto-discovery on project open
```

### Data Models (2 soubory, 108 LOC)
```
src/main/kotlin/com/kei/plugin/models/
├── ApiContract.kt                  73 řádků
│   └── Contract, Endpoint, Schema, Parameter models
└── ValidationResult.kt             35 řádků
    └── Violation types, validation results
```

### Validation Engine (1 soubor, 230 LOC)
```
src/main/kotlin/com/kei/plugin/validation/inspections/
└── JavaScriptApiInspection.kt     230 řádků
    ├── fetch() detection
    ├── axios detection
    ├── Request body validation
    ├── Required field checks
    ├── Type matching
    └── Enum validation
```

### Quick Fixes (1 soubor, 72 LOC)
```
src/main/kotlin/com/kei/plugin/quickfixes/
└── AddMissingFieldFix.kt           72 řádků
    ├── Alt+Enter integration
    ├── Default value generation
    └── Object literal manipulation
```

### UI Components (3 soubory, 219 LOC)
```
src/main/kotlin/com/kei/plugin/ui/
├── ContractExplorerWindowFactory.kt  110 řádků
│   └── Tool window, tree view, schema details
├── settings/
│   └── KeiSettingsConfigurable.kt     81 řádků
│       └── Settings panel, configuration
└── actions/
    └── RefreshContractsAction.kt      28 řádků
        └── Manual refresh action
```

### Utility Classes (2 soubory, 112 LOC)
```
src/main/kotlin/com/kei/plugin/utils/
├── NotificationUtils.kt            57 řádků
│   └── System notifications, toasts, alerts
└── ASTUtils.kt                     55 řádků
    └── AST manipulation, type inference
```

---

## 🧪 TESTY (1 soubor)

```
src/test/kotlin/com/kei/plugin/core/
└── ContractParserTest.kt           58 řádků
    └── Basic OpenAPI parsing test
```

---

## ⚙️ KONFIGURACE (4 soubory)

```
src/main/resources/META-INF/
├── plugin.xml                      Main manifest
│   ├── Plugin metadata
│   ├── Extensions registration
│   ├── Services registration
│   └── Tool window registration
├── kei-javascript.xml              JavaScript inspection config
├── kei-python.xml                  Python support placeholder
└── kei-java.xml                    Java support placeholder
```

---

## 🎨 RESOURCES (1 soubor)

```
src/main/resources/icons/
└── kei.svg                         Plugin icon (16x16)
```

---

## 📖 DOKUMENTACE (9 souborů, 56 KB)

### Quick Start (2 soubory)
```
├── README.md                       3.1 KB
│   └── Feature overview, getting started, examples
└── QUICKSTART.md                   5.2 KB ⭐ START HERE
    └── 5-minute tutorial, step-by-step guide
```

### Installation & Usage (1 soubor)
```
└── INSTALLATION.md                 5.1 KB
    └── Installation guide, troubleshooting, requirements
```

### Technical Documentation (2 soubory)
```
├── PROJECT_SUMMARY.md              11 KB
│   └── Architecture, code structure, tech stack
└── CHECKLIST.md                    3.9 KB
    └── Development roadmap, TODO items, priorities
```

### Presentation & Demo (2 soubory)
```
├── DEMO.md                         6.8 KB
│   └── 5-minute demo script, talking points, FAQ
└── DONE.md                         8.7 KB
    └── Executive summary, status report
```

### Reports (2 soubory)
```
├── FINAL_REPORT.md                 12 KB
│   └── Comprehensive report, metrics, analysis
└── STATUS.md                       6.5 KB
    └── Current status, build info, next steps
```

---

## 📂 EXAMPLE PROJECT (7 souborů)

### API Specifications (2 soubory)
```
example-project/api/
├── openapi.yaml                    203 řádků
│   └── User Management REST API (OpenAPI 3.0)
└── schema.graphql                   78 řádků
    └── GraphQL schema (for v0.2)
```

### TypeScript Examples (2 soubory)
```
example-project/
├── userService.ts                  109 řádků
│   ├── Valid API calls (fetch, axios)
│   └── Invalid examples with violations
└── graphqlService.ts               145 řádků
    ├── GraphQL queries and mutations
    └── Examples for v0.2 validation
```

### Python Examples (1 soubor)
```
example-project/python/
└── user_service.py                  91 řádků
    ├── requests library examples
    ├── httpx examples
    └── For v0.2 Python validation
```

### Configuration (2 soubory)
```
example-project/
├── package.json                    NPM configuration
└── README.md                       Example project docs
```

---

## 🏗️ BUILD FILES (3 soubory)

```
├── build.gradle.kts                Gradle build script
│   ├── IntelliJ Platform Plugin config
│   ├── Dependencies (Swagger, GraphQL, JSON)
│   └── Build tasks
├── settings.gradle.kts             Gradle settings
└── gradle.properties               Plugin version, IDE version
```

---

## 📦 DISTRIBUTABLE

```
build/distributions/
└── kei-plugin-0.1.0-alpha.zip     18 MB
    └── Ready-to-install plugin
```

---

## 🗂️ DIRECTORY STRUCTURE

```
api-contract-guard/
│
├── 📄 Documentation (9 MD files)
│   ├── README.md
│   ├── QUICKSTART.md ⭐
│   ├── INSTALLATION.md
│   ├── PROJECT_SUMMARY.md
│   ├── CHECKLIST.md
│   ├── DEMO.md
│   ├── DONE.md
│   ├── FINAL_REPORT.md
│   ├── STATUS.md
│   └── FILES_INDEX.md (tento soubor)
│
├── 💻 Source Code
│   └── src/main/kotlin/com/kei/plugin/
│       ├── core/ (3 files)
│       ├── models/ (2 files)
│       ├── validation/inspections/ (1 file)
│       ├── quickfixes/ (1 file)
│       ├── ui/ (3 files)
│       └── utils/ (2 files)
│
├── 🧪 Tests
│   └── src/test/kotlin/com/kei/plugin/
│       └── core/ (1 file)
│
├── ⚙️ Resources
│   └── src/main/resources/
│       ├── META-INF/ (4 XML files)
│       └── icons/ (1 SVG file)
│
├── 📂 Examples
│   └── example-project/
│       ├── api/ (2 files)
│       ├── python/ (1 file)
│       ├── *.ts (2 files)
│       └── *.json, *.md (2 files)
│
├── 🏗️ Build
│   ├── build.gradle.kts
│   ├── settings.gradle.kts
│   ├── gradle.properties
│   └── gradlew, gradlew.bat
│
└── 📦 Distribution
    └── build/distributions/
        └── kei-plugin-0.1.0-alpha.zip
```

---

## 📊 FILE STATISTICS

### By Type
```
Kotlin files:        12 files     1,062 LOC
Markdown docs:        9 files        56 KB
XML configs:          4 files       ~1 KB
TypeScript:           2 files       254 LOC
Python:               1 file         91 LOC
YAML:                 1 file        203 LOC
GraphQL:              1 file         78 LOC
SVG:                  1 file        <1 KB
Test files:           1 file         58 LOC
Build files:          3 files       ~2 KB
```

### By Purpose
```
Core logic:          321 LOC (30%)
Validation:          230 LOC (22%)
UI components:       219 LOC (21%)
Models:              108 LOC (10%)
Utils:               112 LOC (11%)
Quick fixes:          72 LOC (7%)
```

---

## 🔍 WHERE TO START

### Jako uživatel:
1. **QUICKSTART.md** ← START HERE ⭐
2. **INSTALLATION.md** ← Install guide
3. **example-project/** ← Try examples

### Jako vývojář:
1. **PROJECT_SUMMARY.md** ← Architecture
2. **CHECKLIST.md** ← Roadmap
3. **src/main/kotlin/** ← Source code

### Jako presenter:
1. **DEMO.md** ← 5-minute script
2. **DONE.md** ← Executive summary
3. **FINAL_REPORT.md** ← Full analysis

---

## 🎯 KEY FILES

**Must Read:**
- `QUICKSTART.md` - 5-minute tutorial
- `README.md` - Feature overview
- `STATUS.md` - Current status

**For Development:**
- `PROJECT_SUMMARY.md` - Technical deep dive
- `CHECKLIST.md` - Development roadmap
- `JavaScriptApiInspection.kt` - Main validation logic

**For Users:**
- `INSTALLATION.md` - How to install
- `example-project/` - Working examples
- `build/distributions/*.zip` - Plugin file

---

## 📝 NOTES

- Všechny soubory jsou verzované v Git
- Build generuje .zip do `build/distributions/`
- Examples jsou připraveny pro testing
- Dokumentace je kompletní a aktuální
- Kód je čistý a dobře strukturovaný

---

*Generováno: 18. listopadu 2024*
*Plugin version: 0.1.0-alpha*
*Total files: 32*
