# Screenshots and Media

Guidelines for marketplace screenshots and demo video for Kei — API Contract Testing.

---

## Screenshots

### 1. Inline Contract Violation Warning
**File:** `screenshots/01-inline-warning.png`

Show a code editor with a TypeScript or Java file open. A red underline highlights an API call where a request body field does not match the OpenAPI contract. The tooltip displays the violation message: expected field type `string`, got `number`. The gutter shows a warning icon.

---

### 2. Smart Quick Fix Menu
**File:** `screenshots/02-quick-fix.png`

Show the same violation from Screenshot 1 with the Alt+Enter quick fix menu open. The menu lists options such as "Rename field to `userName`", "Change type to `string`", and "Add missing required field `email`". One option is highlighted.

---

### 3. Contract Explorer Panel
**File:** `screenshots/03-contract-explorer.png`

Show the Contract Explorer tool window on the right side of the IDE. The tree view displays two discovered contracts — one OpenAPI and one GraphQL. Each contract is expanded to show endpoints or queries. Green checkmarks indicate valid endpoints; yellow warnings indicate endpoints with violations.

---

### 4. Breaking Change Detection
**File:** `screenshots/04-breaking-changes.png`

Show the breaking change notification panel. A list of changes is displayed with severity badges: one critical (removed required field), one warning (type changed from `int` to `string`), and one info (new optional field added). Each item includes the affected endpoint path.

---

### 5. Contract Diff Viewer
**File:** `screenshots/05-contract-diff.png`

Show the side-by-side diff viewer comparing two versions of an OpenAPI schema. The left panel shows version 1.2.0 and the right panel shows version 1.3.0. Removed lines are highlighted in red, added lines in green. A summary bar at the top reads "3 breaking, 2 non-breaking, 1 deprecated".

---

### 6. One-Click Mock Server
**File:** `screenshots/06-mock-server.png`

Show the mock server dialog. The dialog displays a contract name, a generated base URL (`localhost:8091`), and a list of available endpoints with toggle switches. A "Start Server" button is at the bottom. The status bar at the base of the IDE shows "Mock server running on port 8091".

---

### 7. Analytics Dashboard (Pro)
**File:** `screenshots/07-analytics-dashboard.png`

Show the analytics dashboard in a tool window. Include a line chart of contract violations over the past 30 days (trending downward), a breakdown by severity (pie chart), and a table of the top 5 most-violated endpoints. A "Pro" badge is visible in the corner of the panel.

---

### 8. Multi-Language Support
**File:** `screenshots/08-multi-language.png`

Show a split editor with four tabs open: a TypeScript file, a Python file, a Java file, and a Kotlin file. Each file contains an API call with inline validation. The tab bar shows all four files. The purpose is to demonstrate that Kei works consistently across languages.

---

## Screenshot Guidelines

- **Resolution:** 2560×1600 (Retina) or 1280×800 minimum
- **Theme:** Use the default IntelliJ light theme for primary screenshots. Include one dark theme variant of Screenshot 1 for marketplace variety.
- **Font size:** Increase editor font to 14–16pt so code is readable at thumbnail size.
- **Annotations:** Do not add arrows or callouts to the screenshots. Let the UI speak for itself. Use the description text to explain what to look at.
- **Sensitive data:** Remove any real API keys, tokens, or internal URLs before capturing.

---

## Video Demo Outline

**Duration:** 45–60 seconds
**Format:** 1920×1080, 30fps, MP4
**File:** `media/demo-video.mp4`

| Timestamp | Scene | Description |
|-----------|-------|-------------|
| 0:00–0:05 | Title card | "Kei — API Contract Testing for JetBrains IDEs" over a dark background with the plugin logo. |
| 0:05–0:15 | Open a project | Open a TypeScript project in IntelliJ. Show Kei discovering two OpenAPI contracts automatically. The Contract Explorer populates in the sidebar. |
| 0:15–0:25 | Introduce a violation | Type an API call with an incorrect field name. The red underline appears within one second. Hover to show the tooltip with the violation details. |
| 0:25–0:35 | Apply a quick fix | Press Alt+Enter. Select "Rename field to `userId`" from the menu. The violation disappears. The gutter icon changes to a green checkmark. |
| 0:35–0:45 | Show breaking changes | Open the breaking change panel. Scroll through a short list of changes with severity badges. Click on one to navigate to the affected code. |
| 0:45–0:55 | Start a mock server | Right-click a contract in the explorer. Select "Start Mock Server". Show the mock server dialog. Click "Start". The status bar confirms the server is running. |
| 0:55–1:00 | Closing card | "Install free from the JetBrains Marketplace" with a link and the plugin logo. |

**Voiceover notes:** Keep narration short and factual. One sentence per scene. No background music during code scenes — use a subtle ambient track only on the title and closing cards.
