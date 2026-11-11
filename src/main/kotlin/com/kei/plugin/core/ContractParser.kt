package com.kei.plugin.core

import com.intellij.openapi.diagnostic.Logger
import com.kei.plugin.models.ApiContract
import com.kei.plugin.models.ContractType
import com.kei.plugin.models.Endpoint
import com.kei.plugin.models.HttpMethod
import com.kei.plugin.models.Parameter
import com.kei.plugin.models.ParameterLocation
import com.kei.plugin.models.RequestBody
import com.kei.plugin.models.Response
import com.kei.plugin.models.Schema
import io.swagger.parser.OpenAPIParser
import io.swagger.v3.oas.models.OpenAPI
import io.swagger.v3.parser.core.models.ParseOptions
import io.swagger.v3.oas.models.media.Schema as OasSchema
import io.swagger.v3.oas.models.parameters.Parameter as OasParameter
import io.swagger.v3.oas.models.parameters.RequestBody as OasRequestBody
import io.swagger.v3.oas.models.responses.ApiResponse as OasResponse

/**
 * Parses OpenAPI 3.x and Swagger 2.0 documents (YAML or JSON) into the plugin's contract model.
 * Pure: works on text, never touches the network ($ref to remote URLs is not resolved).
 */
class ContractParser {

    /**
     * Returns the parsed contract, or null when the content is not a usable OpenAPI/Swagger document.
     */
    fun parse(filePath: String, content: String): ApiContract? {
        if (!ContractFiles.looksLikeOpenApi(content)) return null
        val openApi = readOpenApi(content) ?: return null

        return ApiContract(
            type = ContractType.OPENAPI,
            filePath = filePath,
            endpoints = endpoints(openApi),
            version = openApi.info?.version,
            title = openApi.info?.title,
        )
    }

    // The third-party parser may throw arbitrary runtime exceptions on malformed input.
    private fun readOpenApi(content: String): OpenAPI? = try {
        val options = ParseOptions().apply {
            isResolve = false
            isResolveFully = false
        }
        OpenAPIParser().readContents(content, null, options)?.openAPI
    } catch (e: RuntimeException) {
        LOG.debug("Not a parseable OpenAPI document", e)
        null
    }

    private fun endpoints(openApi: OpenAPI): List<Endpoint> =
        openApi.paths.orEmpty().flatMap { (path, pathItem) ->
            pathItem.readOperationsMap().orEmpty().map { (method, operation) ->
                val pathParameters = pathItem.parameters.orEmpty()
                Endpoint(
                    path = path,
                    method = HttpMethod.valueOf(method.name.uppercase()),
                    operationId = operation.operationId,
                    summary = operation.summary,
                    description = operation.description,
                    requestBody = operation.requestBody?.let { parseRequestBody(it, openApi) },
                    responses = operation.responses.orEmpty().mapNotNull { (code, response) ->
                        parseResponse(code, response, openApi)
                    },
                    parameters = (pathParameters + operation.parameters.orEmpty()).mapNotNull { parseParameter(it, openApi) },
                )
            }
        }

    private fun parseRequestBody(requestBody: OasRequestBody, openApi: OpenAPI): RequestBody? {
        val content = requestBody.content?.entries?.firstOrNull() ?: return null
        val schema = content.value.schema ?: return null
        return RequestBody(
            required = requestBody.required ?: false,
            contentType = content.key,
            schema = parseSchema(schema, openApi, 0),
        )
    }

    private fun parseResponse(code: String, response: OasResponse, openApi: OpenAPI): Response? {
        val statusCode = code.toIntOrNull() ?: return null
        val content = response.content?.entries?.firstOrNull()
        return Response(
            statusCode = statusCode,
            description = response.description,
            contentType = content?.key ?: "application/json",
            schema = content?.value?.schema?.let { parseSchema(it, openApi, 0) },
        )
    }

    private fun parseParameter(param: OasParameter, openApi: OpenAPI): Parameter? {
        val location = when (param.`in`) {
            "path" -> ParameterLocation.PATH
            "query" -> ParameterLocation.QUERY
            "header" -> ParameterLocation.HEADER
            "cookie" -> ParameterLocation.COOKIE
            else -> return null
        }
        val schema = param.schema ?: return null
        return Parameter(
            name = param.name,
            location = location,
            required = param.required ?: false,
            schema = parseSchema(schema, openApi, 0),
            description = param.description,
        )
    }

    /** Converts a schema, following local `#/components/schemas/X` references (cycle-safe via depth limit). */
    private fun parseSchema(raw: OasSchema<*>, openApi: OpenAPI, depth: Int): Schema {
        val schema = resolveRef(raw, openApi)
        val recurse = depth < MAX_SCHEMA_DEPTH
        return Schema(
            type = schema.type ?: "object",
            format = schema.format,
            properties = if (recurse) schema.properties?.mapValues { (_, p) -> parseSchema(p, openApi, depth + 1) } else null,
            required = schema.required,
            items = if (recurse) schema.items?.let { parseSchema(it, openApi, depth + 1) } else null,
            enum = schema.enum?.map { it.toString() },
            pattern = schema.pattern,
            minLength = schema.minLength,
            maxLength = schema.maxLength,
            minimum = schema.minimum,
            maximum = schema.maximum,
            description = schema.description,
        )
    }

    private fun resolveRef(schema: OasSchema<*>, openApi: OpenAPI): OasSchema<*> {
        val ref = schema.`$ref` ?: return schema
        val name = ref.substringAfterLast('/')
        return openApi.components?.schemas?.get(name) ?: schema
    }

    companion object {
        private val LOG = Logger.getInstance(ContractParser::class.java)
        private const val MAX_SCHEMA_DEPTH = 8
    }
}
