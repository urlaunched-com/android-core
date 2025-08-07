package com.urlaunched.android.ktlintrules

import com.pinterest.ktlint.rule.engine.core.api.AutocorrectDecision
import com.pinterest.ktlint.rule.engine.core.api.ElementType
import com.pinterest.ktlint.rule.engine.core.api.Rule
import com.pinterest.ktlint.rule.engine.core.api.RuleAutocorrectApproveHandler
import com.pinterest.ktlint.rule.engine.core.api.RuleId
import org.jetbrains.kotlin.com.intellij.lang.ASTNode
import org.jetbrains.kotlin.psi.KtNamedFunction
import utils.CustomRulesUtils.isComposable
import utils.CustomRulesUtils.isPreview
import utils.CustomRulesUtils.isPrivate
import utils.CustomRulesUtils.isPublic
import utils.createMessage

class ComposableAccessModifiersRule :
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
            if (!function.isComposable) return

            val containingFile = function.containingKtFile
            val packageDirective = containingFile.packageDirective
            val packageName = packageDirective?.fqName?.asString()
            val isInFeatureModule = packageName?.contains(FEATURE_PACKAGE_NAME) ?: false

            when {
                function.isPreview -> {
                    if (!function.isPrivate) {
                        emit(
                            node.startOffset,
                            createMessage(
                                text = PREVIEW_ERROR_MESSAGE,
                                ruleId = CUSTOM_RULE_ID
                            ),
                            false
                        )
                    }
                }

                isInFeatureModule -> {
                    if (function.isPublic && function.name?.contains(SCREEN_POSTFIX) == false) {
                        emit(
                            node.startOffset,
                            createMessage(
                                text = COMPOSABLE_ACCESS_MODIFIER_ERROR_MESSAGE,
                                ruleId = CUSTOM_RULE_ID
                            ),
                            false
                        )
                    }
                }
            }
        }
    }

    companion object {
        private const val CUSTOM_RULE_ID = "ktlintrules:composable-access-modifier-rule"
        private const val PREVIEW_ERROR_MESSAGE = "Previews should be private"
        private const val COMPOSABLE_ACCESS_MODIFIER_ERROR_MESSAGE =
            "Composable fun in feature module should be internal or private"
        private const val FEATURE_PACKAGE_NAME = "features"
        private const val SCREEN_POSTFIX = "Screen"
    }
}