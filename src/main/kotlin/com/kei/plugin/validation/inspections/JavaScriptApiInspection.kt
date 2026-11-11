package com.kei.plugin.validation.inspections

import com.intellij.codeInspection.LocalInspectionTool
import com.intellij.codeInspection.ProblemHighlightType
import com.intellij.codeInspection.ProblemsHolder
import com.intellij.lang.javascript.psi.JSArrayLiteralExpression
import com.intellij.lang.javascript.psi.JSCallExpression
import com.intellij.lang.javascript.psi.JSElementVisitor
import com.intellij.lang.javascript.psi.JSExpression
import com.intellij.lang.javascript.psi.JSLiteralExpression
import com.intellij.lang.javascript.psi.JSObjectLiteralExpression
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiElementVisitor
import com.kei.plugin.core.ContractIndexService
import com.kei.plugin.models.Endpoint
import com.kei.plugin.models.Schema
import com.kei.plugin.quickfixes.AddMissingFieldFix
import com.kei.plugin.settings.KeiSettings
import com.kei.plugin.validation.ContractRules

/**
 * Validates `fetch(url, { method, body })` and `axios.<method>(url, body)` calls in JavaScript/TypeScript
 * against the endpoints of the indexed OpenAPI contracts.
 */
class JavaScriptApiInspection : LocalInspectionTool() {

    override fun buildVisitor(holder: ProblemsHolder, isOnTheFly: Boolean): PsiElementVisitor {
        val project = holder.project
        val settings = KeiSettings.getInstance(project).state
        if (!settings.enableValidation) return PsiElementVisitor.EMPTY_VISITOR
        val service = ContractIndexService.getInstance(project)
        if (!service.hasContracts()) return PsiElementVisitor.EMPTY_VISITOR

        return object : JSElementVisitor() {
            override fun visitJSCallExpression(node: JSCallExpression) {
                super.visitJSCallExpression(node)
                val call = describe(node) ?: return
                val endpoint = service.findContract(call.method, call.url)
                if (endpoint == null) {
                    if (settings.warnOnMissingContract) {
                        holder.registerProblem(
                            call.urlElement,
                            "No contract endpoint found for ${call.method} ${call.url}",
                            ProblemHighlightType.WEAK_WARNING,
                        )
                    }
                    return
                }
                validateBody(node, call, endpoint, holder)
            }
        }
    }

    private class Call(val method: String, val url: String, val urlElement: PsiElement, val body: PsiElement?)

    private fun describe(node: JSCallExpression): Call? {
        val callee = node.methodExpression?.text ?: return null
        val args = node.arguments
        val urlElement = args.firstOrNull() ?: return null
        val url = stringValue(urlElement) ?: return null

        return if (callee == "fetch") {
            val options = args.getOrNull(1) as? JSObjectLiteralExpression
            val method = options?.findProperty("method")?.value?.let(::stringValue)?.uppercase() ?: "GET"
            Call(method, url, urlElement, options?.findProperty("body")?.value)
        } else {
            ContractRules.axiosMethod(callee)?.let { method ->
                val body = if (ContractRules.methodTakesBody(method)) args.getOrNull(1) else null
                Call(method, url, urlElement, body)
            }
        }
    }

    private fun validateBody(node: JSCallExpression, call: Call, endpoint: Endpoint, holder: ProblemsHolder) {
        val requestBody = endpoint.requestBody ?: return
        if (!ContractRules.methodTakesBody(call.method)) return
        if (call.body == null) {
            if (requestBody.required) {
                holder.registerProblem(node, "Missing required request body", ProblemHighlightType.GENERIC_ERROR_OR_WARNING)
            }
            return
        }
        val literal = unwrapJsonStringify(call.body) as? JSObjectLiteralExpression ?: return
        validateObjectLiteral(literal, requestBody.schema, holder)
    }

    private fun unwrapJsonStringify(body: PsiElement): PsiElement? =
        if (body is JSCallExpression && body.methodExpression?.text == "JSON.stringify") body.arguments.firstOrNull() else body

    private fun validateObjectLiteral(obj: JSObjectLiteralExpression, schema: Schema, holder: ProblemsHolder) {
        val properties = obj.properties.associate { it.name to it.value }

        schema.required.orEmpty().filterNot { properties.containsKey(it) }.forEach { field ->
            holder.registerProblem(
                obj,
                "Missing required field: '$field'",
                ProblemHighlightType.GENERIC_ERROR_OR_WARNING,
                AddMissingFieldFix(field, schema.properties?.get(field)?.type),
            )
        }

        properties.forEach { (name, value) ->
            val expected = schema.properties?.get(name) ?: return@forEach
            if (value == null) return@forEach
            val actual = inferType(value)
            if (actual != null && !ContractRules.isTypeCompatible(actual, expected.type)) {
                holder.registerProblem(
                    value,
                    "Field '$name' expected type '${expected.type}', got '$actual'",
                    ProblemHighlightType.GENERIC_ERROR_OR_WARNING,
                )
            }
            val allowed = expected.enum
            val literal = stringValue(value)
            if (allowed != null && literal != null && literal !in allowed) {
                holder.registerProblem(
                    value,
                    "Invalid enum value '$literal'. Expected one of: ${allowed.joinToString(", ")}",
                    ProblemHighlightType.GENERIC_ERROR_OR_WARNING,
                )
            }
        }
    }

    private fun inferType(value: JSExpression): String? = when (value) {
        is JSLiteralExpression -> ContractRules.literalType(value.text, value.isQuotedLiteral)
        is JSArrayLiteralExpression -> "array"
        is JSObjectLiteralExpression -> "object"
        else -> null
    }

    private fun stringValue(element: PsiElement?): String? =
        (element as? JSLiteralExpression)?.takeIf { it.isQuotedLiteral }?.stringValue
}
