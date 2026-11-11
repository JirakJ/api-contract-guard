package com.kei.plugin.core

import com.kei.plugin.models.ContractType
import com.kei.plugin.models.HttpMethod
import com.kei.plugin.models.ParameterLocation
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class ContractParserTest {

    private val parser = ContractParser()

    private val openApi3 = """
        openapi: 3.0.0
        info:
          title: Test API
          version: 1.2.3
        paths:
          /api/users:
            get:
              operationId: getUsers
              summary: Get all users
              parameters:
                - name: limit
                  in: query
                  schema:
                    type: integer
              responses:
                '200':
                  description: Success
                default:
                  description: Error
            post:
              operationId: createUser
              requestBody:
                required: true
                content:
                  application/json:
                    schema:
                      ${'$'}ref: '#/components/schemas/NewUser'
              responses:
                '201':
                  description: Created
          /api/users/{id}:
            parameters:
              - name: id
                in: path
                required: true
                schema:
                  type: string
            delete:
              responses:
                '204':
                  description: Deleted
        components:
          schemas:
            NewUser:
              type: object
              required: [name, email]
              properties:
                name:
                  type: string
                email:
                  type: string
                  format: email
                role:
                  type: string
                  enum: [admin, user]
                tags:
                  type: array
                  items:
                    type: string
    """.trimIndent()

    @Test
    fun `parses OpenAPI 3 metadata and endpoints`() {
        val contract = parser.parse("/p/api/openapi.yaml", openApi3)!!
        assertEquals(ContractType.OPENAPI, contract.type)
        assertEquals("Test API", contract.title)
        assertEquals("1.2.3", contract.version)
        assertEquals("openapi.yaml", contract.fileName)
        assertEquals(
            setOf("GET /api/users", "POST /api/users", "DELETE /api/users/{id}"),
            contract.endpoints.map { "${it.method} ${it.path}" }.toSet(),
        )
    }

    @Test
    fun `resolves local schema refs in request bodies`() {
        val post = parser.parse("openapi.yaml", openApi3)!!.endpoints.single { it.method == HttpMethod.POST }
        val body = post.requestBody!!
        assertTrue(body.required)
        assertEquals("application/json", body.contentType)
        assertEquals("object", body.schema.type)
        assertEquals(setOf("name", "email"), body.schema.required.orEmpty().toSet())
        assertEquals("email", body.schema.properties!!["email"]!!.format)
        assertEquals(listOf("admin", "user"), body.schema.properties!!["role"]!!.enum)
        assertEquals("string", body.schema.properties!!["tags"]!!.items!!.type)
    }

    @Test
    fun `parses parameters including path-level ones and numeric responses only`() {
        val endpoints = parser.parse("openapi.yaml", openApi3)!!.endpoints
        val get = endpoints.single { it.method == HttpMethod.GET }
        assertEquals(ParameterLocation.QUERY, get.parameters.single().location)
        assertEquals(listOf(200), get.responses.map { it.statusCode })
        val delete = endpoints.single { it.method == HttpMethod.DELETE }
        assertEquals("id", delete.parameters.single().name)
        assertTrue(delete.parameters.single().required)
    }

    @Test
    fun `parses JSON OpenAPI documents`() {
        val json = """{"openapi":"3.0.1","info":{"title":"J","version":"1"},""" +
            """"paths":{"/ping":{"get":{"responses":{"200":{"description":"ok"}}}}}}"""
        val contract = parser.parse("api.json", json)!!
        assertEquals("/ping", contract.endpoints.single().path)
    }

    @Test
    fun `converts Swagger 2 documents`() {
        val swagger = """
            swagger: '2.0'
            info:
              title: Legacy
              version: '0.1'
            paths:
              /pets:
                post:
                  parameters:
                    - in: body
                      name: pet
                      required: true
                      schema:
                        type: object
                        required: [name]
                        properties:
                          name:
                            type: string
                  responses:
                    '200':
                      description: ok
        """.trimIndent()
        val contract = parser.parse("swagger.yaml", swagger)!!
        assertEquals("Legacy", contract.title)
        val post = contract.endpoints.single()
        assertEquals(HttpMethod.POST, post.method)
        assertEquals(listOf("name"), post.requestBody?.schema?.required)
    }

    @Test
    fun `non OpenAPI and malformed content yields null`() {
        assertNull(parser.parse("api.yaml", "name: my-app\nversion: 1.0\n"))
        assertNull(parser.parse("api.json", """{"dependencies": {}}"""))
        assertNull(parser.parse("openapi.yaml", "openapi: 3.0.0\n  : : broken: [\n"))
        assertNull(parser.parse("openapi.yaml", ""))
    }

    @Test
    fun `document without paths has no endpoints`() {
        val contract = parser.parse("openapi.yaml", "openapi: 3.0.0\ninfo:\n  title: Empty\n  version: '1'\n")
        assertNotNull(contract)
        assertTrue(contract!!.endpoints.isEmpty())
    }

    @Test
    fun `self-referencing schema does not recurse forever`() {
        val recursive = """
            openapi: 3.0.0
            info: { title: R, version: '1' }
            paths:
              /nodes:
                post:
                  requestBody:
                    content:
                      application/json:
                        schema:
                          ${'$'}ref: '#/components/schemas/Node'
                  responses:
                    '200': { description: ok }
            components:
              schemas:
                Node:
                  type: object
                  properties:
                    child:
                      ${'$'}ref: '#/components/schemas/Node'
        """.trimIndent()
        val body = parser.parse("openapi.yaml", recursive)!!.endpoints.single().requestBody!!
        assertFalse(body.required)
        assertEquals("object", body.schema.properties!!["child"]!!.type)
    }
}
