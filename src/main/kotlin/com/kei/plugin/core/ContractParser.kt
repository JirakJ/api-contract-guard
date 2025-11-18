package com.kei.plugin.core

import com.intellij.openapi.vfs.VirtualFile
import com.intellij.openapi.vfs.VfsUtil
import com.kei.plugin.models.*
import io.swagger.v3.parser.OpenAPIV3Parser
import io.swagger.v3.oas.models.PathItem
import io.swagger.v3.oas.models.parameters.Parameter as OasParameter

class ContractParser {
    
    fun parse(file: VirtualFile): ApiContract? {
        return try {
            when {
                isOpenApiFile(file) -> parseOpenApi(file)
                isGraphQLFile(file) -> parseGraphQL(file)
                else -> null
            }
        } catch (e: Exception) {
            // Log error and return null
            null
        }
    }
    
    private fun isOpenApiFile(file: VirtualFile): Boolean {
        val name = file.name.lowercase()
        return (name.contains("openapi") || name.contains("swagger")) && 
               (name.endsWith(".yaml") || name.endsWith(".yml") || name.endsWith(".json"))
    }
    
    private fun isGraphQLFile(file: VirtualFile): Boolean {
        return file.name.endsWith(".graphql") || file.name.endsWith(".gql")
    }
    
    private fun parseOpenApi(file: VirtualFile): ApiContract {
        val content = VfsUtil.loadText(file)
        val parseResult = OpenAPIV3Parser().readContents(content, null, null)
        val openAPI = parseResult.openAPI 
            ?: throw IllegalStateException("Failed to parse OpenAPI: ${parseResult.messages}")
        
        val endpoints = openAPI.paths?.flatMap { (path, pathItem) ->
            pathItem.readOperationsMap().mapNotNull { (method, operation) ->
                try {
                    Endpoint(
                        path = path,
                        method = HttpMethod.valueOf(method.name.uppercase()),
                        operationId = operation.operationId,
                        summary = operation.summary,
                        description = operation.description,
                        requestBody = operation.requestBody?.let { parseRequestBody(it) },
                        responses = operation.responses?.mapNotNull { (code, response) ->
                            parseResponse(code, response)
                        } ?: emptyList(),
                        parameters = operation.parameters?.mapNotNull { parseParameter(it) } ?: emptyList()
                    )
                } catch (e: Exception) {
                    null // Skip invalid endpoints
                }
            }
        } ?: emptyList()
        
        return ApiContract(
            type = ContractType.OPENAPI,
            file = file,
            endpoints = endpoints,
            version = openAPI.info?.version,
            title = openAPI.info?.title
        )
    }
    
    private fun parseRequestBody(requestBody: io.swagger.v3.oas.models.parameters.RequestBody): RequestBody? {
        val content = requestBody.content?.entries?.firstOrNull() ?: return null
        val schema = content.value.schema ?: return null
        
        return RequestBody(
            required = requestBody.required ?: false,
            contentType = content.key,
            schema = parseSchema(schema)
        )
    }
    
    private fun parseResponse(code: String, response: io.swagger.v3.oas.models.responses.ApiResponse): Response? {
        val statusCode = code.toIntOrNull() ?: return null
        val content = response.content?.entries?.firstOrNull()
        
        return Response(
            statusCode = statusCode,
            description = response.description,
            contentType = content?.key ?: "application/json",
            schema = content?.value?.schema?.let { parseSchema(it) }
        )
    }
    
    private fun parseParameter(param: OasParameter): Parameter? {
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
            schema = parseSchema(schema),
            description = param.description
        )
    }
    
    private fun parseSchema(schema: io.swagger.v3.oas.models.media.Schema<*>): Schema {
        return Schema(
            type = schema.type ?: "object",
            format = schema.format,
            properties = schema.properties?.mapValues { (_, propSchema) ->
                parseSchema(propSchema)
            },
            required = schema.required,
            items = schema.items?.let { parseSchema(it) },
            enum = schema.enum?.map { it.toString() },
            pattern = schema.pattern,
            minLength = schema.minLength,
            maxLength = schema.maxLength,
            minimum = schema.minimum,
            maximum = schema.maximum,
            description = schema.description
        )
    }
    
    private fun parseGraphQL(file: VirtualFile): ApiContract {
        // TODO v0.2: GraphQL parsing - GraphQL Java API needs proper integration
        // For now, return empty contract structure
        return ApiContract(
            type = ContractType.GRAPHQL,
            file = file,
            endpoints = emptyList(),
            title = file.nameWithoutExtension
        )
    }
}
