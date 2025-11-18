# Kei Plugin - Quick Start Guide

## 🚀 Installation & First Run

### 1. Build the Plugin

```bash
cd /Users/jirakj/work/api-contract-guard
./gradlew buildPlugin
```

The plugin will be built in: `build/distributions/kei-plugin-0.1.0-alpha.zip`

### 2. Install in IntelliJ IDEA

**Option A: Install from Disk**
1. Open IntelliJ IDEA / WebStorm / PyCharm
2. Go to **Settings → Plugins**
3. Click **⚙️** (gear icon) → **Install Plugin from Disk...**
4. Select `build/distributions/kei-plugin-0.1.0-alpha.zip`
5. Restart IDE

**Option B: Run in Development Mode**
```bash
./gradlew runIde
```
This opens a new IDE instance with the plugin pre-installed.

### 3. Open Example Project

1. In IDE: **File → Open**
2. Navigate to: `/Users/jirakj/work/api-contract-guard/example-project`
3. Click **Open**

### 4. See It in Action

**Automatic Discovery**
- Plugin automatically discovers `api/openapi.yaml`
- Notification appears: "Kei: Found 1 API contract"

**Contract Explorer**
1. Open: **View → Tool Windows → Kei Contracts**
2. See tree view of all API endpoints:
   ```
   📄 openapi.yaml (OPENAPI)
     ├─ GET /api/users
     ├─ POST /api/users
     │   ├─ Request Body
     │   │   ├─ name: string *
     │   │   ├─ email: string *
     │   │   └─ role: string *
     │   └─ Responses
     │       ├─ 201: User created
     │       └─ 400: Bad request
     └─ ...
   ```

**Real-Time Validation**
1. Open `userService.ts`
2. Navigate to `createUserMissingEmail()` function
3. See red underline on the object literal
4. Hover to see error: "Missing required field: 'email'"
5. Press **Alt+Enter** → Select "Add missing field 'email'"
6. Field is automatically added!

**Validation Examples**

✅ **Valid Call:**
```typescript
fetch('/api/users', {
  method: 'POST',
  body: JSON.stringify({
    name: 'John',
    email: 'john@example.com',
    role: 'user'  // Valid enum
  })
})
```

❌ **Invalid - Missing Field:**
```typescript
fetch('/api/users', {
  method: 'POST',
  body: JSON.stringify({
    name: 'John',
    role: 'user'
    // ❌ Missing required field: email
  })
})
```

❌ **Invalid - Wrong Enum:**
```typescript
fetch('/api/users', {
  method: 'POST',
  body: JSON.stringify({
    name: 'John',
    email: 'john@example.com',
    role: 'superadmin'  // ❌ Should be: admin, user, or guest
  })
})
```

## 🛠️ Development Commands

### Build Plugin
```bash
./gradlew build
```

### Run Tests
```bash
./gradlew test
```

### Run Plugin in Development IDE
```bash
./gradlew runIde
```

### Verify Plugin Configuration
```bash
./gradlew verifyPlugin
```

### Build Distribution
```bash
./gradlew buildPlugin
```
Output: `build/distributions/kei-plugin-*.zip`

## 📝 Configuration

### Settings
**Settings → Tools → Kei API Contract Testing**

Options:
- ✓ Enable real-time validation
- ✓ Show warnings for missing contracts
- ✓ Auto-refresh contracts on file changes

### Contract Discovery

Plugin automatically discovers:
- `**/openapi.{yaml,yml,json}`
- `**/swagger.{yaml,yml,json}`
- `**/api.{yaml,yml,json}`

Files to create:
- `openapi.yaml` (recommended)
- `swagger.json`
- Any file matching the patterns above

## 🎯 What Works Now (v0.1-alpha)

### ✅ Implemented Features
- OpenAPI 3.x parsing
- Contract discovery & indexing
- TypeScript/JavaScript validation
  - `fetch()` API calls
  - `axios.get/post/put/delete/patch` calls
- Request body validation
  - Required field checks
  - Type matching (string, number, boolean, object, array)
  - Enum validation
- Quick Fixes
  - Add missing fields (Alt+Enter)
- Contract Explorer UI
  - Tree view of all contracts
  - Endpoint details
  - Request/response schemas
- Breaking change detection (file watcher)
- Settings panel

### ⚠️ Known Limitations
- Variable reference validation not implemented (MVP scope)
- Response validation not yet implemented
- Path parameter validation basic
- Header validation not yet implemented
- GraphQL support planned for v0.2

## 🐛 Troubleshooting

### Plugin doesn't find contracts
1. Check file name matches pattern: `openapi.yaml`, `swagger.json`
2. Try manual refresh: **Kei Contracts** tool window → Refresh button
3. Check notification panel for errors

### No validation in code
1. Ensure JavaScript plugin is enabled
2. Check file extension: `.ts`, `.js`, `.tsx`, `.jsx`
3. Try invalidating caches: **File → Invalidate Caches / Restart**

### Quick fixes don't work
1. Ensure cursor is on the red underline
2. Press **Alt+Enter** (or **⌥+Enter** on Mac)
3. Select "Add missing field ..." from menu

### Performance issues
- Plugin indexes contracts on startup (may take 1-2 seconds)
- Large projects (100k+ files) not yet optimized (planned for v1.0)

## 📚 Next Steps

1. Try modifying `openapi.yaml` and see changes detected
2. Add your own API endpoints
3. Write more TypeScript code with API calls
4. Explore Contract Explorer tool window
5. Check Settings panel for configuration options

## 🤝 Contributing

See `CHECKLIST.md` for development roadmap and TODO items.

**Priority areas:**
- GraphQL support (v0.2)
- Python validation (v0.2)
- Mock server generator (v0.2)
- Java/Kotlin support (v0.3)

---

Made with ❤️ for API developers
