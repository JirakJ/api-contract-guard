package com.kei.plugin.core

/**
 * Rules for which files are considered API contract candidates.
 */
object ContractFiles {

    /** Directories never scanned for contracts. */
    val IGNORED_DIRECTORIES = setOf("node_modules", ".git", "build", "dist", "target", ".idea", ".gradle", "out")

    private val EXTENSIONS = listOf(".yaml", ".yml", ".json")
    private val NAME_HINTS = listOf("openapi", "swagger", "api")
    private val CONTENT_MARKER = Regex("""(^|[\s{,])["']?(openapi|swagger)["']?\s*:""")

    /** File name filter: `openapi.yaml`, `swagger.json`, `petstore-api.yml`, ... */
    fun isCandidate(fileName: String): Boolean {
        val name = fileName.lowercase()
        return EXTENSIONS.any { name.endsWith(it) } && NAME_HINTS.any { name.contains(it) }
    }

    fun isIgnoredDirectory(name: String): Boolean = name in IGNORED_DIRECTORIES

    /** Cheap check for a top-level `openapi:` / `swagger:` key before invoking the full parser. */
    fun looksLikeOpenApi(content: String): Boolean = CONTENT_MARKER.containsMatchIn(content.take(MARKER_SCAN_LIMIT))

    private const val MARKER_SCAN_LIMIT = 4096
}
