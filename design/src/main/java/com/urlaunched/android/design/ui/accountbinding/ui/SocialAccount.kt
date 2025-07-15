package com.urlaunched.android.design.ui.accountbinding.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.urlaunched.android.design.ui.accountbinding.models.SocialAccountDimens

@Composable
fun SocialAccount(
    modifier: Modifier = Modifier,
    hasAccount: Boolean,
    isCurrentAccount: Boolean,
    hasEmail: Boolean,
    socialAccountDimens: SocialAccountDimens = SocialAccountDimens(),
    onAddAccountClick: () -> Unit,
    leadingIcon: @Composable () -> Unit,
    providerText: @Composable () -> Unit,
    accountEmailText: @Composable () -> Unit,
    addAccountText: @Composable () -> Unit,
    currentAccountContainer: @Composable () -> Unit,
    deleteAccountContainer: @Composable () -> Unit
) {
    AccountContainer(
        modifier = modifier,
        content = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable(
                        enabled = true,
                        onClick = onAddAccountClick
                    )
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(socialAccountDimens.paddingHorizontal),
                    modifier = Modifier.padding(
                        end = socialAccountDimens.paddingEnd,
                        start = socialAccountDimens.paddingStart,
                        top = socialAccountDimens.paddingTop,
                        bottom = socialAccountDimens.paddingBottom
                    )
                ) {
                    leadingIcon()

                    providerText()

                    if (hasEmail) {
                        accountEmailText()
                    } else {
                        addAccountText()
                    }
                }
            }
        }
    )

    CurrentAccountOrDeleteContainer(
        modifier = Modifier.fillMaxWidth(),
        hasAccount = hasAccount,
        isCurrentAccount = isCurrentAccount,
        currentAccountContainer = currentAccountContainer,
        deleteAccountContainer = deleteAccountContainer
    )
}