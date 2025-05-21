package com.urlaunched.android.ktlintrules

import com.pinterest.ktlint.rule.engine.core.api.AutocorrectDecision
import com.pinterest.ktlint.rule.engine.core.api.ElementType
import com.pinterest.ktlint.rule.engine.core.api.Rule
import com.pinterest.ktlint.rule.engine.core.api.RuleAutocorrectApproveHandler
import com.pinterest.ktlint.rule.engine.core.api.RuleId
import org.jetbrains.kotlin.com.intellij.lang.ASTNode
import org.jetbrains.kotlin.psi.KtNamedFunction
import utils.CustomRulesUtils.getPackageName
import utils.CustomRulesUtils.isComposable
import utils.CustomRulesUtils.isPreview
import utils.CustomRulesUtils.modifierParameter
import utils.CustomRulesUtils.returnsValue
import utils.createMessage

class ComposableModifierMissingRule :
    Rule(
        RuleId(CUSTOM_RULE_ID),
        about = About()
    ),
    RuleAutocorrectApproveHandler {
    override fun beforeVisitChildNodes(
        node: ASTNode,
        emit: (offset: Int, errorMessage: String, canBeAutoCorrected: Boolean) -> AutocorrectDecision
    ) {
        if (node.elementType == ElementType.FUN) {
            val function = node.psi as? KtNamedFunction ?: return
            if (!function.isComposable ||
                function.returnsValue ||
                function.isPreview ||
                function.receiverTypeReference?.text == MODIFIER_TYPE_REFERENCE ||
                function.getPackageName()?.contains(DESIGN_RESOURCES_PACKAGE) == true ||
                modifierMissingExceptions.any { exception ->
                    function.name?.contains(exception) == true
                }
            ) {
                return
            } else {
                if (function.modifierParameter() != null) {
                    return
                } else {
                    emit(
                        node.startOffset,
                        createMessage(
                            text = MODIFIER_MISSING_ERROR,
                            ruleId = CUSTOM_RULE_ID
                        ),
                        false
                    )
                }
            }
        }
    }

    companion object {
        private const val CUSTOM_RULE_ID = "ktlintrules:composable-modifier-missing-rule"
        private const val MODIFIER_MISSING_ERROR =
            "This @Composable function emits content but doesn't have a modifier parameter."
        private const val DESIGN_RESOURCES_PACKAGE = "core.designsystem.resources"
        private val modifierMissingExceptions =
            listOf("Screen", "Route", "SideEffects", "DeepLink", "remember", "Theme")
        private const val MODIFIER_TYPE_REFERENCE = "Modifier"
    }
}