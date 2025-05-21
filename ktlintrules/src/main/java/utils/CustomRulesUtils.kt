package utils

import org.jetbrains.kotlin.lexer.KtTokens
import org.jetbrains.kotlin.psi.KtAnnotationEntry
import org.jetbrains.kotlin.psi.KtCallableDeclaration
import org.jetbrains.kotlin.psi.KtClass
import org.jetbrains.kotlin.psi.KtFunction
import org.jetbrains.kotlin.psi.KtNamedFunction
import org.jetbrains.kotlin.psi.KtParameter
import org.jetbrains.kotlin.psi.KtPsiUtil
import org.jetbrains.kotlin.psi.psiUtil.visibilityModifierType

object CustomRulesUtils {
    private val KtAnnotationEntry.isPreviewAnnotation: Boolean
        get() = calleeExpression?.text?.contains("Preview") == true

    val KtNamedFunction.isPreview: Boolean
        get() = annotationEntries.any { it.isPreviewAnnotation }

    val KtNamedFunction.isPublic: Boolean
        get() {
            if (KtPsiUtil.isLocal(this)) return false
            val visibilityModifier = visibilityModifierType()
            return visibilityModifier == null || visibilityModifier == KtTokens.PUBLIC_KEYWORD
        }

    val KtNamedFunction.isPrivate: Boolean
        get() {
            if (KtPsiUtil.isLocal(this)) return false
            val visibilityModifier = visibilityModifierType()
            return visibilityModifier == null || visibilityModifier == KtTokens.PRIVATE_KEYWORD
        }

    val KtClass.isInternal: Boolean
        get() {
            if (KtPsiUtil.isLocal(this)) return false
            val visibilityModifier = visibilityModifierType()
            return visibilityModifier == KtTokens.INTERNAL_KEYWORD
        }

    val KtNamedFunction.isComposable: Boolean
        get() = annotationEntries.any { it.calleeExpression?.text == "Composable" }

    fun KtFunction.modifierParameter(): KtParameter? {
        val modifiers = valueParameters.filter { it.isModifier() }
        return modifiers.firstOrNull { it.name == "modifier" } ?: modifiers.firstOrNull()
    }

    private fun KtCallableDeclaration.isModifier(): Boolean = typeReference?.text?.contains("Modifier") ?: false

    fun KtFunction.getPackageName(): String? {
        val containingFile = this.containingKtFile
        val packageDirective = containingFile.packageDirective
        return packageDirective?.fqName?.asString()
    }

    val KtFunction.returnsValue: Boolean
        get() = typeReference != null && typeReference!!.text != "Unit"
}