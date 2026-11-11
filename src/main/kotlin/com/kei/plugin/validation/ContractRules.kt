package com.kei.plugin.validation

/**
 * PSI-independent rules used by the JavaScript/TypeScript contract inspection and its quick fix.
 */
object ContractRules {

    private val AXIOS_METHODS = setOf("get", "post", "put", "delete", "patch", "head", "options")
    private val BODY_METHODS = setOf("POST", "PUT", "PATCH")
    private val IDENTIFIER = Regex("^[A-Za-z_$][A-Za-z0-9_$]*$")

    /**
     * HTTP method for an axios-style call expression text (`axios.post`, `this.axios.get`), or null.
     */
    fun axiosMethod(calleeText: String): String? {
        val receiver = calleeText.substringBeforeLast('.', missingDelimiterValue = "")
        val name = calleeText.substringAfterLast('.')
        val isAxios = receiver == "axios" || receiver.endsWith(".axios")
        return if (isAxios && name in AXIOS_METHODS) name.uppercase() else null
    }

    fun methodTakesBody(method: String): Boolean = method.uppercase() in BODY_METHODS

    /**
     * Whether a JS literal of [actualType] satisfies a contract [expectedType]. Integers satisfy `number`;
     * `null` is accepted for any type (nullability is not modelled); unknown/absent expected types accept anything.
     */
    fun isTypeCompatible(actualType: String, expectedType: String?): Boolean = when {
        expectedType == null -> true
        actualType == expectedType -> true
        actualType == "null" -> true
        actualType == "integer" && expectedType == "number" -> true
        else -> false
    }

    /**
     * Type of a JS literal from its source text: quoted strings, numbers, booleans and `null`.
     */
    fun literalType(text: String, quoted: Boolean): String? = when {
        quoted -> "string"
        text.toLongOrNull() != null -> "integer"
        text.toDoubleOrNull() != null -> "number"
        text == "true" || text == "false" -> "boolean"
        text == "null" -> "null"
        else -> null
    }

    /** Placeholder value inserted by the quick fix for a missing field of [type]. */
    fun defaultValue(type: String?): String = when (type?.lowercase()) {
        "string" -> "''"
        "integer", "number" -> "0"
        "boolean" -> "false"
        "array" -> "[]"
        "object" -> "{}"
        else -> "null"
    }

    /** Property key as written in an object literal: bare identifier or quoted string. */
    fun propertyKey(name: String): String =
        if (IDENTIFIER.matches(name)) name else "'" + name.replace("\\", "\\\\").replace("'", "\\'") + "'"

    /**
     * Returns the object literal source with `key: value` appended as the last property, keeping the
     * literal syntactically valid (adds a separating comma, respects an existing trailing comma).
     */
    fun appendProperty(objectLiteral: String, name: String, value: String): String {
        val close = objectLiteral.lastIndexOf('}')
        require(objectLiteral.trimStart().startsWith("{") && close > 0) { "Not an object literal: $objectLiteral" }
        val body = objectLiteral.substring(objectLiteral.indexOf('{') + 1, close)
        val property = "${propertyKey(name)}: $value"
        val trimmedBody = body.trimEnd()
        val insertion = when {
            trimmedBody.isBlank() -> " $property "
            trimmedBody.endsWith(",") -> "$trimmedBody $property "
            else -> "$trimmedBody, $property "
        }
        return objectLiteral.substring(0, objectLiteral.indexOf('{') + 1) + insertion + objectLiteral.substring(close)
    }
}
