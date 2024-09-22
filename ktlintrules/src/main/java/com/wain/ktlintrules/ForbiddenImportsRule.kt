package com.wain.ktlintrules

import com.pinterest.ktlint.rule.engine.core.api.AutocorrectDecision
import com.pinterest.ktlint.rule.engine.core.api.ElementType
import com.pinterest.ktlint.rule.engine.core.api.Rule
import com.pinterest.ktlint.rule.engine.core.api.RuleAutocorrectApproveHandler
import com.pinterest.ktlint.rule.engine.core.api.RuleId
import com.pinterest.ktlint.rule.engine.core.api.children
import org.jetbrains.kotlin.com.intellij.lang.ASTNode
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.psi.KtPsiFactory
import org.jetbrains.kotlin.resolve.ImportPath

internal const val CUSTOM_RULE_ID = "ktlintrules:forbidenimportsqwerty"

private val restrictedNavGraphImports = listOf(
    "androidx.navigation.compose.composable",
    "com.google.accompanist.navigation.material.bottomSheet"
)

private val restrictedAppImports = listOf(
    "com.urlaunched.android.common.navigation.bottomSheet",
    "com.urlaunched.android.common.navigation.composable"
)


class ForbiddenImportsRule :
    Rule(
        RuleId(CUSTOM_RULE_ID),
        about = About()
    ),
    RuleAutocorrectApproveHandler {
    override fun beforeVisitChildNodes(
        node: ASTNode,
        emit: (offset: Int, errorMessage: String, canBeAutoCorrected: Boolean) -> AutocorrectDecision
    ) {
        if (node.elementType == ElementType.IMPORT_LIST) {
            val moduleName = getModuleName(node)
            val restrictedImports = if (moduleName == APP_MODULE_NAME) {
                restrictedAppImports
            } else {
                restrictedNavGraphImports
            }

            node.children().forEach { childNode ->
                if (childNode.elementType == ElementType.IMPORT_DIRECTIVE) {
                    val importText = childNode.text
                    if (restrictedImports.any { importText.contains(it) }) {
                        emit(childNode.startOffset, "Forbidden import: $importText", false)

                        replaceWrongImports(
                            isAppModule = moduleName == APP_MODULE_NAME,
                            node = childNode
                        )
                    }
                }
            }
        }
    }

    private fun replaceWrongImports(isAppModule: Boolean, node: ASTNode) {
        val ktPsiFactory = KtPsiFactory(node.psi.project)
        val importText = node.text
        val correctImport = when {
            isAppModule && importText.contains("com.urlaunched.android.common.navigation.composable") -> {
                "androidx.navigation.compose.composable"
            }

            isAppModule && importText.contains("com.urlaunched.android.common.navigation.composable") -> {
                "com.google.accompanist.navigation.material.bottomSheet"
            }

            !isAppModule && importText.contains("androidx.navigation.compose.composable") -> {
                "com.urlaunched.android.common.navigation.composable"
            }

            !isAppModule && importText.contains("com.google.accompanist.navigation.material.bottomSheet") -> {
                "com.urlaunched.android.common.navigation.composable"
            }

            else -> "com.urlaunched.android.common.navigation.composable"
        }

        val newImportNode = ktPsiFactory.createImportDirective(
            ImportPath(
                fqName = FqName(correctImport),
                isAllUnder = false
            )
        )
        node.treeParent.replaceChild(node, newImportNode.node)
    }

    private fun getModuleName(node: ASTNode): String? {
        val psiFile = node.psi.containingFile
        val filePath = psiFile.virtualFile?.path ?: return null
        val srcIndex = filePath.indexOf("/src/main/java/")
        if (srcIndex != -1) {
            val modulePath = filePath.substring(0, srcIndex)
            return modulePath.split("/").lastOrNull()?.let { ":$it" }
        }
        return null
    }

    companion object {
        private const val APP_MODULE_NAME = ":app"
    }
}