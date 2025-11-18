package com.kei.plugin.models

import com.intellij.openapi.vfs.VirtualFile

data class ApiContract(
    val type: ContractType,
    val file: VirtualFile,
    val endpoints: List<Endpoint>,
    val version: String? = null,
    val title: String? = null
)

enum class ContractType {
    OPENAPI,
    GRAPHQL,
    CODE_ANNOTATION
}

data class Endpoint(
    val path: String,
    val method: HttpMethod,
    val operationId: String?,
    val summary: String? = null,
    val description: String? = null,
    val requestBody: RequestBody? = null,
    val responses: List<Response> = emptyList(),
    val parameters: List<Parameter> = emptyList()
)

enum class HttpMethod {
    GET, POST, PUT, DELETE, PATCH, HEAD, OPTIONS, TRACE
}

data class RequestBody(
    val required: Boolean = false,
    val contentType: String = "application/json",
    val schema: Schema
)

data class Response(
    val statusCode: Int,
    val description: String? = null,
    val contentType: String = "application/json",
    val schema: Schema? = null
)

data class Parameter(
    val name: String,
    val location: ParameterLocation,
    val required: Boolean = false,
    val schema: Schema,
    val description: String? = null
)

enum class ParameterLocation {
    PATH, QUERY, HEADER, COOKIE
}

data class Schema(
    val type: String, // string, number, integer, boolean, object, array
    val format: String? = null,
    val properties: Map<String, Schema>? = null,
    val required: List<String>? = null,
    val items: Schema? = null, // for arrays
    val enum: List<String>? = null,
    val pattern: String? = null,
    val minLength: Int? = null,
    val maxLength: Int? = null,
    val minimum: Number? = null,
    val maximum: Number? = null,
    val description: String? = null
)
