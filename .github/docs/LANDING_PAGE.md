# Kei — API Contract Testing

## Catch Breaking API Changes While You Code, Not in Production

Kei brings real-time API contract testing directly into your JetBrains IDE. Validate every API call against its contract as you type — with instant feedback in under 50 milliseconds.

[Install Free from JetBrains Marketplace](#) · [View Documentation](#) · [Watch Demo](#)

---

## The Problem

API contract violations are one of the top causes of integration failures. Teams discover broken contracts in staging, in CI pipelines, or worse — in production. By then, the cost of fixing them has multiplied.

Traditional contract testing happens too late. You write code, push it, wait for a pipeline, and only then find out that a renamed field broke three downstream services. Meanwhile, your team has moved on to the next task.

**The fix is simple: test contracts where the code is written — in the IDE.**

Kei shifts contract testing left. It validates your API calls against OpenAPI and GraphQL schemas in real time, right inside your editor. No context switching. No waiting for CI. No production surprises.

---

## Key Features

### Automatic Contract Discovery
Kei finds your OpenAPI and GraphQL schemas automatically. Point it at a directory, a URL, or a registry — it handles the rest. No manual configuration needed.

### Real-Time Validation
Every API call is validated against its contract as you type. Results appear in under 50 milliseconds across TypeScript, JavaScript, Python, Java, and Kotlin.

### Inline Warnings
Contract violations show up as red underlines directly in your code, just like a compiler error. You see the problem the moment you introduce it.

### Smart Quick Fixes
Press Alt+Enter on any violation to see suggested fixes. Kei can rename fields, update types, add missing parameters, and restructure payloads automatically.

### Breaking Change Detection
Kei compares contract versions and flags breaking changes with severity levels — critical, warning, and info — so you know exactly what matters.

### Contract Explorer
A dedicated tree view shows all discovered contracts, their endpoints, schemas, and validation status in one place.

### One-Click Mock Server
Generate a mock server from any contract with a single click. Test your client code against realistic responses without deploying anything.

### Contract Diff Viewer
Compare contract versions side by side. See exactly what changed between two versions of a schema with clear visual highlighting.

---

## How It Works

### Step 1: Install and Open Your Project
Install Kei from the JetBrains Marketplace. Open any project that consumes or produces APIs. Kei activates automatically.

### Step 2: Contracts Are Discovered
Kei scans your project for OpenAPI and GraphQL schemas — local files, remote URLs, and common registry locations. Discovered contracts appear in the Contract Explorer panel.

### Step 3: Code With Instant Feedback
Write API calls as you normally would. Kei validates every request and response shape against the matching contract in real time. Violations appear as inline warnings with clear descriptions.

### Step 4: Fix Issues Before They Ship
Use the suggested quick fixes to resolve violations in seconds. Review breaking changes in the diff viewer. Push code that is already contract-compliant.

---

## Pricing

### Free
Everything you need to start testing contracts in your IDE.

- Automatic contract discovery (OpenAPI, GraphQL)
- Real-time validation (under 50ms, 5+ languages)
- Inline warnings for contract violations
- Smart quick fixes (Alt+Enter)
- Contract Explorer tree view
- Breaking change detection with severity levels
- One-click mock server generation

**$0 / month — free forever**

---

### Pro
For developers who want deeper insight and control.

Everything in Free, plus:

- Analytics dashboard — track contract health over time
- Version management — pin, compare, and roll back contract versions
- Advanced mocking — custom response rules, delays, and error simulation
- Test generation — auto-generate contract tests from schemas

**$9 / month per user**

---

### Team / Enterprise
For teams that need security, collaboration, and CI/CD integration.

Everything in Pro, plus:

- Security analyzer — detect insecure patterns in API contracts
- AI-powered suggestions — intelligent contract fixes and improvements
- Team collaboration — shared contract registries and review workflows
- CI/CD integration — validate contracts in your pipeline with a single command

**Contact us for pricing**

---

## What Developers Are Saying

> "Placeholder testimonial — replace with a real quote from an early adopter. Focus on time saved or bugs prevented."
>
> — **Developer Name**, Role at Company

> "Placeholder testimonial — replace with a real quote highlighting the real-time validation experience and how it compares to manual testing."
>
> — **Developer Name**, Role at Company

> "Placeholder testimonial — replace with a real quote from a team lead about the impact on integration failure rates."
>
> — **Developer Name**, Role at Company

---

## Frequently Asked Questions

### What API contract formats does Kei support?
Kei supports OpenAPI (v2 and v3) and GraphQL schemas. Contracts can be loaded from local files, remote URLs, or schema registries.

### Which languages are supported?
Kei validates API calls written in TypeScript, JavaScript, Python, Java, and Kotlin. Additional languages are on the roadmap.

### Does Kei work offline?
Yes. All core features — contract discovery, validation, inline warnings, quick fixes, and mock server — work fully offline. Some Pro and Team features may require an internet connection.

### What are the system requirements?
Kei requires JDK 17 or later and IntelliJ IDEA 2023.1 or later. It also works with other JetBrains IDEs based on the same platform version (WebStorm, PyCharm, etc.).

### Is the Free tier limited in any way?
The Free tier includes all core contract testing features with no time limits, no usage caps, and no watermarks. Pro and Team tiers add analytics, advanced mocking, security analysis, and collaboration tools.

---

## Start Catching Contract Violations Today

Install Kei for free and validate your first API contract in under two minutes.

[Install from JetBrains Marketplace](#) · [Read the Docs](#) · [Join the Community](#)
