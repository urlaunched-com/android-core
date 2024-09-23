package com.urlaunched.android.ktlintrules

import com.pinterest.ktlint.cli.ruleset.core.api.RuleSetProviderV3
import com.pinterest.ktlint.rule.engine.core.api.RuleProvider
import com.pinterest.ktlint.rule.engine.core.api.RuleSetId

internal const val CUSTOM_RULE_SET_ID = "url-rule-set-id"

class RuleSetProvider : RuleSetProviderV3(RuleSetId(CUSTOM_RULE_SET_ID)) {
    override fun getRuleProviders(): Set<RuleProvider> = setOf(
        RuleProvider { ForbiddenImportsRule() }
    )
}