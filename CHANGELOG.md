# Changelog

## [Unreleased]

### Fixed
- Calls to concrete URLs (`/users/42`, absolute URLs, URLs with query strings) now match templated contract paths (`/users/{id}`); previously only the literal template text matched.
- Swagger 2.0 contracts are now parsed (only OpenAPI 3 worked) and local `$ref` request schemas are resolved.
- "Add missing field" quick fix no longer produces invalid code (missing comma) when the object already has properties.
- Settings were not saved and had no effect; they are now persisted per project and respected by the inspection and the file watcher.
- Deleting or editing a contract file no longer leaves stale endpoints in the index; file events from other projects are ignored.
- The "no contract found" warning is no longer reported in projects without any contract.
- `axios` method detection no longer misreads calls such as `axios.getUri`.
- The inspection was registered twice (also without the JavaScript plugin installed).
- Indexing no longer runs on an unmanaged coroutine scope or parses files on the UI thread.
- Refresh Contracts action is now available in the Tools menu.

### Changed
- Built with the IntelliJ Platform Gradle Plugin 2.x; the plugin no longer bundles its own copy of kotlinx-coroutines.
- Plugin description and README describe only the features that exist (GraphQL, mock server, diff viewer, analytics and other advertised features were never implemented).

### Removed
- Unreachable code (security analyzer, documentation/CI/client/performance generators, history/diff/analytics helpers, mock data generator), the GraphQL stub, the unused graphql-java and json-schema-validator dependencies, and stale build/marketing documents.

## [2026.8.0] - 2026-11-10

### Changed
- The plugin is now completely free — all features unlocked, no license needed.
- Source code is now open source under Apache-2.0: https://github.com/JirakJ/api-contract-guard

