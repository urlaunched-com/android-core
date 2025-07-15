package com.urlaunched.android.design.ui.accountbinding.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.urlaunched.android.design.resources.dimens.Dimens
import com.urlaunched.android.design.ui.accountbinding.models.EmailAccountColors
import com.urlaunched.android.design.ui.accountbinding.models.EmailAccountDimens

@Composable
fun EmailAccount(
    modifier: Modifier = Modifier,
    hasAccount: Boolean,
    isCurrentAccount: Boolean,
    hasEmail: Boolean,
    emailAccountColors: EmailAccountColors,
    emailAccountDimens: EmailAccountDimens = EmailAccountDimens(),
    onEditEmailClick: () -> Unit,
    onAddAccountClick: () -> Unit,
    onEditPasswordClick: () -> Unit,
    emailProviderText: @Composable () -> Unit,
    accountEmailText: @Composable () -> Unit,
    passwordText: @Composable () -> Unit,
    trailingIcon: @Composable () -> Unit,
    addAccountText: @Composable () -> Unit,
    currentAccountContainer: @Composable () -> Unit,
    deleteAccountContainer: @Composable () -> Unit
) {
    AccountContainer(
        modifier = modifier,
        content = {
            Column(modifier = Modifier.fillMaxWidth()) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(Dimens.spacingSmall),
                    modifier = Modifier
                        .clickable {
                            if (hasAccount) {
                                onEditEmailClick()
                            } else {
                                onAddAccountClick()
                            }
                        }
                        .padding(Dimens.spacingNormal)
                ) {
                    emailProviderText()

                    if (hasEmail) {
                        accountEmailText()

                        trailingIcon()
                    } else {
                        addAccountText()
                    }
                }

                if (hasAccount) {
                    HorizontalDivider(
                        modifier = Modifier.fillMaxWidth(),
                        color = emailAccountColors.driverColor,
                        thickness = emailAccountDimens.dividerHeight
                    )

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable(onClick = onEditPasswordClick)
                            .padding(Dimens.spacingNormal)
                    ) {
                        passwordText()

                        trailingIcon()
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