package com.kei.plugin.quickfixes

import com.intellij.codeInspection.LocalQuickFix
import com.intellij.codeInspection.ProblemDescriptor
import com.intellij.lang.javascript.psi.JSObjectLiteralExpression
import com.intellij.lang.javascript.psi.JSProperty
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFileFactory

class AddMissingFieldFix(
    private val fieldName: String,
    private val fieldType: String
) : LocalQuickFix {
    
    override fun getName() = "Add missing field '$fieldName'"
    
    override fun getFamilyName() = "Kei contract validation"
    
    override fun applyFix(project: Project, descriptor: ProblemDescriptor) {
        val element = descriptor.psiElement as? JSObjectLiteralExpression ?: return
        
        val defaultValue = getDefaultValue(fieldType)
        
        // Create new property in a dummy file via the public PSI factory
        try {
            val dummyObject = "let obj = { $fieldName: $defaultValue }"
            val dummyFile = PsiFileFactory.getInstance(project)
                .createFileFromText("dummy.js", element.containingFile.language, dummyObject)
            
            // Find the property in the created tree
            val newProperty = findProperty(dummyFile, fieldName)
            
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
