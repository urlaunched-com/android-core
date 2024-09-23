package com.urlaunched.android.ktlintrules

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
                        emit(childNode.startOffset, "$ERROR_MESSAGE $importText", true)

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
            isAppModule && importText.contains(URL_COMPOSABLE_IMPORT) -> {
                ANDROID_X_COMPOSABLE_IMPORT
            }

            isAppModule && importText.contains(URL_BOTTOM_SHEET_IMPORT) -> {
                ACCOMPANIST_BOTTOM_SHEET_IMPORT
            }

            !isAppModule && importText.contains(ANDROID_X_COMPOSABLE_IMPORT) -> {
                URL_COMPOSABLE_IMPORT
            }

            !isAppModule && importText.contains(ACCOMPANIST_BOTTOM_SHEET_IMPORT) -> {
                URL_BOTTOM_SHEET_IMPORT
            }

            else -> ANDROID_X_COMPOSABLE_IMPORT
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
        private const val CUSTOM_RULE_ID = "ktlintrules:forbidennavigationimports"
        private const val ERROR_MESSAGE = "Forbidden import found:"
        private const val ANDROID_X_COMPOSABLE_IMPORT = "androidx.navigation.compose.composable"
        private const val ACCOMPANIST_BOTTOM_SHEET_IMPORT = "com.google.accompanist.navigation.material.bottomSheet"
        private const val URL_COMPOSABLE_IMPORT = "com.urlaunched.android.common.navigation.composable"
        private const val URL_BOTTOM_SHEET_IMPORT = "com.urlaunched.android.common.navigation.bottomSheet"

        private val restrictedNavGraphImports = listOf(
            ANDROID_X_COMPOSABLE_IMPORT,
            ACCOMPANIST_BOTTOM_SHEET_IMPORT
        )

        private val restrictedAppImports = listOf(
            URL_BOTTOM_SHEET_IMPORT,
            URL_COMPOSABLE_IMPORT
        )
    }
}