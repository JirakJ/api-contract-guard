package com.kei.plugin.quickfixes

import com.intellij.codeInspection.LocalQuickFix
import com.intellij.codeInspection.ProblemDescriptor
import com.intellij.lang.javascript.psi.JSObjectLiteralExpression
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiFileFactory
import com.intellij.psi.util.PsiTreeUtil
import com.kei.plugin.validation.ContractRules

/**
 * Adds a missing required field (with a type-appropriate placeholder value) to an object literal.
 */
class AddMissingFieldFix(
    private val fieldName: String,
    private val fieldType: String?,
) : LocalQuickFix {

    override fun getName() = "Add missing field '$fieldName'"

    override fun getFamilyName() = "Add missing contract field"

    override fun applyFix(project: Project, descriptor: ProblemDescriptor) {
        val literal = descriptor.psiElement as? JSObjectLiteralExpression ?: return
        val newText = ContractRules.appendProperty(literal.text, fieldName, ContractRules.defaultValue(fieldType))
        val dummy = PsiFileFactory.getInstance(project)
            .createFileFromText("dummy.js", literal.containingFile.language, "let obj = $newText;")
        val replacement = PsiTreeUtil.findChildOfType(dummy, JSObjectLiteralExpression::class.java) ?: return
        literal.replace(replacement)
    }
}
