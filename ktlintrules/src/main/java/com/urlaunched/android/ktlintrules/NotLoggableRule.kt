package com.urlaunched.android.ktlintrules

import com.pinterest.ktlint.rule.engine.core.api.AutocorrectDecision
import com.pinterest.ktlint.rule.engine.core.api.ElementType
import com.pinterest.ktlint.rule.engine.core.api.Rule
import com.pinterest.ktlint.rule.engine.core.api.RuleAutocorrectApproveHandler
import com.pinterest.ktlint.rule.engine.core.api.RuleId
import org.jetbrains.kotlin.com.intellij.lang.ASTNode
import org.jetbrains.kotlin.psi.KtClass
import org.jetbrains.kotlin.psi.KtNamedFunction
import utils.createMessage

class NotLoggableRule :
    Rule(
        RuleId(CUSTOM_RULE_ID),
        about = About()
    ),
    RuleAutocorrectApproveHandler {
    override fun beforeVisitChildNodes(
        node: ASTNode,
        emit: (offset: Int, errorMessage: String, canBeAutoCorrected: Boolean) -> AutocorrectDecision
    ) {
        if (node.elementType == ElementType.CLASS) {
            val ktClass = node.psi as? KtClass
            val isUseCaseClass = ktClass?.fqName?.asString()?.contains(USE_CASES_PACKAGE) == true

            if (!isUseCaseClass) return

            val firstMethod = ktClass?.body?.children
                ?.filterIsInstance<KtNamedFunction>()
                ?.firstOrNull()

            if (firstMethod == null || firstMethod.name != INVOKE) return

            val hasSensitiveParams = firstMethod.valueParameters.any { argument ->
                sensitiveParams.any { sensitiveParam ->
                    argument.text.lowercase().contains(sensitiveParam.lowercase())
                }
            }

            if (!hasSensitiveParams) return

            val hasNotLoggableAnnotation = firstMethod.annotationEntries.any {
                it.shortName?.asString() == NOT_LOGGABLE_ANNOTATION
            }

            if (!hasNotLoggableAnnotation) {
                emit(
                    node.startOffset,
                    createMessage(
                        text = ERROR_MESSAGE,
                        ruleId = CUSTOM_RULE_ID
                    ),
                    false
                )
            }
        }
    }

    companion object {
        private val sensitiveParams = listOf(
            "email", "password", "phoneNumber", "birthDate", "postIndex", "phone", "birth", "deviceId"
        )
        private const val USE_CASES_PACKAGE = ".domain.usecases"
        private const val NOT_LOGGABLE_ANNOTATION = "NotLoggable"
        private const val ERROR_MESSAGE = "Methods handling sensitive parameters must be annotated with @NotLoggable."
        private const val CUSTOM_RULE_ID = "ktlintrules:notloggablerule"
        private const val INVOKE = "invoke"
    }
}