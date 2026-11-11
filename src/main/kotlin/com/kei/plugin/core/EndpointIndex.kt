package com.kei.plugin.core

import com.kei.plugin.models.ApiContract
import com.kei.plugin.models.Endpoint
import com.kei.plugin.models.HttpMethod
import java.util.concurrent.ConcurrentHashMap

/**
 * Thread-safe in-memory index of parsed contracts keyed by file path, with endpoint lookup for concrete call URLs.
 */
class EndpointIndex {

    private val contracts = ConcurrentHashMap<String, ApiContract>()

    fun put(contract: ApiContract) {
        contracts[contract.filePath] = contract
    }

    fun remove(filePath: String) {
        contracts.remove(filePath)
    }

    /** Removes every contract whose file lives under [directoryPath] (used when a directory is deleted). */
    fun removeUnder(directoryPath: String) {
        val prefix = directoryPath.trimEnd('/') + "/"
        contracts.keys.removeIf { it == directoryPath || it.startsWith(prefix) }
    }

    fun clear() = contracts.clear()

    fun all(): List<ApiContract> = contracts.values.sortedBy { it.filePath }

    fun get(filePath: String): ApiContract? = contracts[filePath]

    fun isEmpty(): Boolean = contracts.isEmpty()

    /**
     * Finds the endpoint for an HTTP [method] and a call [url] such as `/api/users/42?x=1` or `https://host/api/users/42`.
     * Exact (literal) path matches win over templated ones; returns null for unknown methods.
     */
    fun find(method: String, url: String): Endpoint? {
        val httpMethod = HttpMethod.values().firstOrNull { it.name == method.uppercase() } ?: return null
        val callPath = EndpointPaths.normalizeCallPath(url)
        val candidates = all().flatMap { it.endpoints }.filter { it.method == httpMethod }
        return candidates.firstOrNull { EndpointPaths.normalizeTemplate(it.path) == callPath }
            ?: candidates.firstOrNull { EndpointPaths.matches(it.path, callPath) }
    }
}

/**
 * Path normalisation and template matching (`/users/{id}` matches `/users/42`).
 */
object EndpointPaths {

    private val SCHEME_AND_HOST = Regex("^[a-zA-Z][a-zA-Z0-9+.-]*://[^/]*")

    /** Strips scheme/host, query string and fragment, collapses slashes and drops a trailing slash. */
    fun normalizeCallPath(url: String): String {
        val withoutHost = url.trim().replace(SCHEME_AND_HOST, "")
        val path = withoutHost.substringBefore('?').substringBefore('#')
        return normalizeTemplate(path)
    }

    fun normalizeTemplate(path: String): String {
        val segments = path.split('/').filter { it.isNotEmpty() }
        return "/" + segments.joinToString("/")
    }

    /**
     * True when [callPath] matches [template]. Template variables match exactly one non-empty segment.
     * A call path may carry an extra prefix (for example a server base path such as `/v1`) before the template.
     */
    fun matches(template: String, callPath: String): Boolean {
        val t = segments(template)
        val c = segments(callPath)
        if (c.size < t.size) return false
        val tail = c.subList(c.size - t.size, c.size)
        return t.zip(tail).all { (ts, cs) -> isVariable(ts) || ts == cs }
    }

    private fun segments(path: String) = path.split('/').filter { it.isNotEmpty() }

    private fun isVariable(segment: String) = segment.startsWith("{") && segment.endsWith("}")
}
