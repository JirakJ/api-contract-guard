# Kei - API Contract Testing

[![Plugin Verification](https://github.com/JirakJ/api-contract-guard/actions/workflows/verify.yml/badge.svg)](https://github.com/JirakJ/api-contract-guard/actions/workflows/verify.yml)
[![License](https://img.shields.io/badge/license-Apache--2.0-blue)](LICENSE)

IntelliJ Platform plugin that finds the OpenAPI 3.x / Swagger 2.0 contracts in a project and checks
`fetch` and `axios` calls in JavaScript and TypeScript against them while you edit.

## Features

- **Contract discovery** — indexes YAML/JSON files whose name contains `openapi`, `swagger` or `api` and that declare an
  `openapi`/`swagger` version. `node_modules`, `build`, `dist`, `target`, `.git`, `.idea`, `.gradle` and `out` are skipped.
  Contract files are re-indexed when they change; **Tools → Refresh Contracts** re-scans on demand.
- **Kei Contracts tool window** — every contract with its endpoints, request body fields (required ones marked `*`) and responses.
- **Call validation** (requires the bundled JavaScript plugin, i.e. IntelliJ IDEA Ultimate, WebStorm, PhpStorm, …):
  - `fetch(url, { method, body })` and `axios.get/post/put/patch/delete/head/options(url, body)` with a string-literal URL
  - path templates (`/users/{id}`) match concrete URLs (`/users/42`); host, query string and a base-path prefix are ignored
  - object-literal bodies (also wrapped in `JSON.stringify`): missing required fields, literal type mismatches, invalid enum values
  - optional weak warning when a call matches no endpoint (only when the project has at least one contract)
  - quick fix: add a missing required field with a placeholder value

Not supported: GraphQL, URLs built from template literals or variables, request bodies held in variables.

## Usage

1. Install from **Settings → Plugins → Marketplace** (search for "Kei") or from a release ZIP.
2. Open a project that contains an OpenAPI/Swagger file — a notification reports how many contracts were found.
3. Open a `.js`/`.ts` file: contract problems are highlighted; press Alt+Enter on a missing-field problem to fix it.

Try it on [`example-project/`](example-project/).

## Settings

**Settings → Tools → Kei API Contract Testing** (per project)

| Setting | Default |
|---------|---------|
| Validate fetch/axios calls in JavaScript and TypeScript | on |
| Warn about calls with no matching contract endpoint | on |
| Re-index contract files when they change | on |

## Building

```bash
./gradlew detekt test buildPlugin
```

Requires JDK 17. The plugin targets IntelliJ Platform 2023.2 (build 232) and newer.

## License

Apache License 2.0 — see [LICENSE](LICENSE) and [NOTICE](NOTICE). Free and open source; issues and pull requests are welcome.
