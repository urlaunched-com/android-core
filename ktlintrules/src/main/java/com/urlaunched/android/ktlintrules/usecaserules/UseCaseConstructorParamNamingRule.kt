package com.urlaunched.android.ktlintrules.usecaserules

import com.pinterest.ktlint.rule.engine.core.api.AutocorrectDecision
import com.pinterest.ktlint.rule.engine.core.api.ElementType
import com.pinterest.ktlint.rule.engine.core.api.RuleAutocorrectApproveHandler
import com.pinterest.ktlint.rule.engine.core.api.RuleId
import org.jetbrains.kotlin.com.intellij.lang.ASTNode
import org.jetbrains.kotlin.psi.KtClass
import org.jetbrains.kotlin.psi.psiUtil.startOffset
import utils.createMessage

class UseCaseConstructorParamNamingRule :
    com.pinterest.ktlint.rule.engine.core.api.Rule(
        RuleId(CUSTOM_RULE_ID),
        about = About()
    ),
    RuleAutocorrectApproveHandler {
    override fun beforeVisitChildNodes(
        node: ASTNode,
        emit: (offset: Int, errorMessage: String, canBeAutoCorrected: Boolean) -> AutocorrectDecision
    ) {
        if (node.elementType == ElementType.CLASS) {
            val psiClass = node.psi as? KtClass ?: return
            val primaryConstructor = psiClass.primaryConstructor ?: return

            primaryConstructor.valueParameters.forEach { param ->
                val paramName = param.name ?: return@forEach
                val paramType = param.typeReference?.text ?: return@forEach

                if (paramType.endsWith(USE_CASE_POSTFIX)) {
                    val expectedParamName = paramType.replaceFirstChar { it.lowercase() }
                    if (paramName != expectedParamName) {
                        emit(
                            param.startOffset,
                            createMessage(
                                text = ERROR_MESSAGE.format(paramName, expectedParamName, paramType),
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
        private const val CUSTOM_RULE_ID = "ktlintrules:usecase-constructor-rule"
        private const val USE_CASE_POSTFIX = "UseCase"
        private const val ERROR_MESSAGE = "Constructor parameter '%s' should be named '%s' for the use case type '%s'"
    }
}