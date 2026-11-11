package com.kei.plugin.core

import com.kei.plugin.models.ApiContract
import com.kei.plugin.models.ContractType
import com.kei.plugin.models.Endpoint
import com.kei.plugin.models.HttpMethod
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class EndpointIndexTest {

    private fun endpoint(method: HttpMethod, path: String, id: String = "$method $path") = Endpoint(path, method, id)

    private fun contract(path: String, vararg endpoints: Endpoint) = ApiContract(ContractType.OPENAPI, path, endpoints.toList())

    private val users = contract(
        "/p/api/openapi.yaml",
        endpoint(HttpMethod.GET, "/users"),
        endpoint(HttpMethod.GET, "/users/{id}"),
        endpoint(HttpMethod.GET, "/users/me"),
        endpoint(HttpMethod.POST, "/users"),
    )

    @Test
    fun `finds literal and templated paths by method`() {
        val index = EndpointIndex().apply { put(users) }
        assertEquals("GET /users", index.find("get", "/users")?.operationId)
        assertEquals("POST /users", index.find("POST", "/users")?.operationId)
        assertEquals("GET /users/{id}", index.find("GET", "/users/42")?.operationId)
        assertNull(index.find("DELETE", "/users/42"))
        assertNull(index.find("GET", "/orders"))
    }

    @Test
    fun `literal match wins over template`() {
        val index = EndpointIndex().apply { put(users) }
        assertEquals("GET /users/me", index.find("GET", "/users/me")?.operationId)
    }

    @Test
    fun `ignores host, query, fragment, trailing slash and base path prefix`() {
        val index = EndpointIndex().apply { put(users) }
        assertEquals("GET /users/{id}", index.find("GET", "https://api.example.com/v1/users/7?expand=1#x")?.operationId)
        assertEquals("GET /users", index.find("GET", "/users/")?.operationId)
    }

    @Test
    fun `unknown http method returns null`() {
        val index = EndpointIndex().apply { put(users) }
        assertNull(index.find("FETCH", "/users"))
    }

    @Test
    fun `put replaces contract for the same file and remove drops it`() {
        val index = EndpointIndex()
        index.put(users)
        index.put(contract("/p/api/openapi.yaml", endpoint(HttpMethod.GET, "/orders")))
        assertNull(index.find("GET", "/users"))
        assertEquals(1, index.all().size)
        index.remove("/p/api/openapi.yaml")
        assertTrue(index.isEmpty())
        assertNull(index.find("GET", "/orders"))
    }

    @Test
    fun `removeUnder removes contracts in deleted directories only`() {
        val index = EndpointIndex()
        index.put(contract("/p/api/openapi.yaml"))
        index.put(contract("/p/api2/openapi.yaml"))
        index.put(contract("/p/other/swagger.json"))
        index.removeUnder("/p/api")
        assertEquals(listOf("/p/api2/openapi.yaml", "/p/other/swagger.json"), index.all().map { it.filePath })
        index.removeUnder("/p/other/swagger.json")
        assertEquals(listOf("/p/api2/openapi.yaml"), index.all().map { it.filePath })
        assertEquals("/p/api2/openapi.yaml", index.get("/p/api2/openapi.yaml")?.filePath)
        index.clear()
        assertTrue(index.isEmpty())
    }
}

class EndpointPathsTest {

    @Test
    fun `normalizes call paths`() {
        assertEquals("/a/b", EndpointPaths.normalizeCallPath("http://localhost:8080/a//b/?q=1"))
        assertEquals("/", EndpointPaths.normalizeCallPath("https://host"))
        assertEquals("/a", EndpointPaths.normalizeCallPath("  a#frag "))
    }

    @Test
    fun `template variables match exactly one segment`() {
        assertTrue(EndpointPaths.matches("/users/{id}/posts/{postId}", "/users/1/posts/2"))
        assertFalse(EndpointPaths.matches("/users/{id}", "/users"))
        assertFalse(EndpointPaths.matches("/users/{id}", "/accounts/1"))
        assertTrue(EndpointPaths.matches("/users/{id}", "/api/v2/users/1"))
    }
}

class ContractFilesTest {

    @Test
    fun `candidate names need a hint and a supported extension`() {
        assertTrue(ContractFiles.isCandidate("openapi.yaml"))
        assertTrue(ContractFiles.isCandidate("Swagger.JSON"))
        assertTrue(ContractFiles.isCandidate("petstore-api.yml"))
        assertFalse(ContractFiles.isCandidate("openapi.txt"))
        assertFalse(ContractFiles.isCandidate("config.yaml"))
    }

    @Test
    fun `ignored directories`() {
        assertTrue(ContractFiles.isIgnoredDirectory("node_modules"))
        assertTrue(ContractFiles.isIgnoredDirectory("build"))
        assertFalse(ContractFiles.isIgnoredDirectory("api"))
    }

    @Test
    fun `content marker detects yaml and json documents`() {
        assertTrue(ContractFiles.looksLikeOpenApi("openapi: 3.0.0\n"))
        assertTrue(ContractFiles.looksLikeOpenApi("# comment\nswagger: '2.0'"))
        assertTrue(ContractFiles.looksLikeOpenApi("{\"openapi\": \"3.1.0\"}"))
        assertFalse(ContractFiles.looksLikeOpenApi("name: app\nmyopenapi: 1"))
        assertFalse(ContractFiles.looksLikeOpenApi(""))
    }
}
