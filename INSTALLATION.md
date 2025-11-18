# Kei Plugin - Installation Guide

## 📦 Plugin Distribution

**File:** `build/distributions/kei-plugin-0.1.0-alpha.zip`  
**Size:** ~18 MB  
**Build Status:** ✅ Successfully built  

## 🚀 Installation Methods

### Method 1: Install from Disk (Recommended)

1. **Locate Plugin File**
   ```bash
   cd /Users/jirakj/work/api-contract-guard
   ls build/distributions/kei-plugin-0.1.0-alpha.zip
   ```

2. **Open IntelliJ IDEA / WebStorm / PyCharm**

3. **Go to Plugins**
   - **macOS:** `IntelliJ IDEA → Settings → Plugins`
   - **Windows/Linux:** `File → Settings → Plugins`

4. **Install Plugin**
   - Click **⚙️** (gear icon)
   - Select **Install Plugin from Disk...**
   - Navigate to `kei-plugin-0.1.0-alpha.zip`
   - Click **OK**

5. **Restart IDE**
   - Click **Restart IDE** button
   - Or manually restart

6. **Verify Installation**
   - Go to `Settings → Plugins`
   - Search for "Kei"
   - Should see: "Kei – API Contract Testing" with version 0.1.0-alpha

### Method 2: Run in Development Mode

For testing/development without installation:

```bash
cd /Users/jirakj/work/api-contract-guard
./gradlew runIde
```

This opens a new IDE instance with the plugin pre-installed.

## ✅ First Run

### 1. Open Example Project

**Option A: Open existing example**
```
File → Open → Navigate to:
/Users/jirakj/work/api-contract-guard/example-project
```

**Option B: Create your own project**
1. Create new project
2. Add `openapi.yaml` file in root or `api/` folder
3. Add TypeScript file with API calls

### 2. Verify Plugin Works

**Check Notification:**
- After opening project, you should see:
  ```
  Kei: Found 1 API contract
  Open 'Kei Contracts' tool window to explore
  ```

**Open Tool Window:**
- **View → Tool Windows → Kei Contracts**
- Should see tree view with your API endpoints

**Test Validation:**
- Open `userService.ts` (in example project)
- Navigate to `createUserMissingEmail()` function
- Should see red underline on object literal
- Hover to see: "Missing required field: 'email'"

**Test Quick Fix:**
- Put cursor on red underline
- Press **Alt+Enter** (or **⌥+Enter** on Mac)
- Select "Add missing field 'email'"
- Field should be added automatically

## 🎛️ Configuration

### Settings

**Settings → Tools → Kei API Contract Testing**

Available options:
- ✓ Enable real-time validation
- ✓ Show warnings for missing contracts
- ✓ Auto-refresh contracts on file changes

### Contract File Patterns

Plugin automatically discovers files matching:
```
**/openapi.{yaml,yml,json}
**/swagger.{yaml,yml,json}
**/api.{yaml,yml,json}
```

Recommended file names:
- `openapi.yaml` (preferred)
- `swagger.json`
- `api/openapi.yaml`

## 🐛 Troubleshooting

### Issue: Plugin not found after installation

**Solution:**
1. Check plugin file is not corrupted
2. Verify IDE version is 2023.2+
3. Restart IDE completely (not just reload)
4. Check IDE logs: `Help → Show Log in Finder/Explorer`

### Issue: No contracts found

**Solution:**
1. Verify file name matches pattern: `openapi.yaml`
2. Check file is in project directory (not excluded)
3. Try manual refresh: **Kei Contracts** tool window → Refresh button
4. Check IDE notification for errors

### Issue: Validation not working

**Solution:**
1. Ensure JavaScript plugin is enabled:
   - `Settings → Plugins → Installed → JavaScript`
2. Check file extension is `.ts`, `.js`, `.tsx`, or `.jsx`
3. Try closing and reopening the file
4. Invalidate caches: `File → Invalidate Caches / Restart`

### Issue: Quick fixes not appearing

**Solution:**
1. Ensure cursor is exactly on the red underline
2. Press **Alt+Enter** (or **⌥+Enter** on Mac)
3. Wait for menu to appear (may take 1-2 seconds)
4. Try typing character and pressing Alt+Enter again

### Issue: Performance problems

**Symptoms:**
- IDE freezes when opening files
- Slow typing
- High CPU usage

**Solution:**
1. Plugin is still alpha, performance not optimized
2. Try disabling for large projects (100k+ files)
3. Report issue with project details
4. Workaround: Disable in `Settings → Tools → Kei`

## 📋 System Requirements

### Minimum Requirements
- **IDE:** IntelliJ IDEA 2023.2+ (Ultimate), WebStorm 2023.2+, PyCharm 2023.2+ (Professional)
- **JDK:** 17 or higher
- **OS:** macOS, Windows, Linux
- **RAM:** 4 GB (8 GB recommended)
- **Disk:** 50 MB free space

### Supported Languages
- ✅ TypeScript
- ✅ JavaScript
- ⚠️ Python (planned v0.2)
- ⚠️ Java (planned v0.3)
- ⚠️ Kotlin (planned v0.3)

### Supported API Specs
- ✅ OpenAPI 3.0.x
- ✅ OpenAPI 3.1.x
- ⚠️ Swagger 2.0 (basic support)
- ⚠️ GraphQL (planned v0.2)

## 🆘 Getting Help

### Documentation
- **Quick Start:** `QUICKSTART.md`
- **README:** `README.md`
- **Project Summary:** `PROJECT_SUMMARY.md`
- **Checklist:** `CHECKLIST.md`

### Support Channels
- **Issues:** Report bugs and feature requests
- **Email:** support@kei.dev
- **Documentation:** docs.kei.dev

## 🎉 Next Steps

1. ✅ Install plugin
2. ✅ Open example project
3. ✅ Verify validation works
4. ✅ Try quick fixes
5. ✅ Explore Contract Explorer
6. ✅ Configure settings
7. 🚀 Start using in your projects!

---

**Happy coding! 🎯**
