package com.kei.plugin.quickfixes

import com.intellij.codeInspection.LocalQuickFix
import com.intellij.codeInspection.ProblemDescriptor
import com.intellij.lang.javascript.psi.JSObjectLiteralExpression
import com.intellij.lang.javascript.psi.JSProperty
import com.intellij.lang.javascript.psi.impl.JSPsiElementFactory
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiElement

class AddMissingFieldFix(
    private val fieldName: String,
    private val fieldType: String
) : LocalQuickFix {
    
    override fun getName() = "Add missing field '$fieldName'"
    
    override fun getFamilyName() = "Kei contract validation"
    
    override fun applyFix(project: Project, descriptor: ProblemDescriptor) {
        val element = descriptor.psiElement as? JSObjectLiteralExpression ?: return
        
        val defaultValue = getDefaultValue(fieldType)
        
        // Create new property using JSPsiElementFactory
        try {
            val propertyText = "$fieldName: $defaultValue"
            val newProperty = JSPsiElementFactory.createJSExpression(propertyText, element.containingFile) as? JSProperty
            
            if (newProperty != null) {
                // Add to existing object
                val properties = element.properties
                if (properties.isNotEmpty()) {
                    element.addAfter(newProperty, properties.last())
                } else {
                    element.add(newProperty)
                }
            }
        } catch (e: Exception) {
            // Fallback: do nothing on error
        }
    }
    
    private fun findProperty(element: PsiElement, name: String): JSProperty? {
        if (element is JSProperty && element.name == name) {
            return element
        }
        for (child in element.children) {
            val found = findProperty(child, name)
            if (found != null) return found
        }
        return null
    }
    
    private fun getDefaultValue(type: String): String {
        return when (type.lowercase()) {
            "string" -> "''"
            "integer", "number" -> "0"
            "boolean" -> "false"
            "array" -> "[]"
            "object" -> "{}"
            else -> "null"
        }
    }
}
