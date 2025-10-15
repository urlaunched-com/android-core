package com.urlaunched.android.design.ui.accountbinding.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.rounded.KeyboardArrowRight
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.urlaunched.android.design.resources.dimens.Dimens
import com.urlaunched.android.design.ui.accountbinding.models.AccountBindingDimens
import com.urlaunched.android.design.ui.accountbinding.models.AccountBindingTextStyles
import com.urlaunched.android.design.ui.accountbinding.models.AccountBindingTitles
import com.urlaunched.android.design.ui.accountbinding.models.AccountCardStyle
import com.urlaunched.android.design.ui.accountbinding.models.AccountData
import com.urlaunched.android.design.ui.accountbinding.models.AccountProvider
import com.urlaunched.android.design.ui.accountbinding.models.AccountsSection
import com.urlaunched.android.design.ui.accountbinding.models.EmailAccountProvider
import com.urlaunched.android.design.ui.accountbinding.models.PasswordBasedAccountProvider
import com.urlaunched.android.design.ui.accountbinding.models.SocialAccountProvider
import com.urlaunched.android.design.ui.clickable.debouncedClickable

@Composable
fun AccountBindingContainer(
    modifier: Modifier = Modifier,
    sections: List<AccountsSection>,
    onUnbindAccountClick: (provider: AccountProvider) -> Unit,
    onEditPasswordClick: (provider: AccountProvider) -> Unit,
    onEditIdentifierClick: (provider: AccountProvider) -> Unit,
    onAddAccountClick: (provider: AccountProvider) -> Unit,
    titles: AccountBindingTitles,
    cardStyle: AccountCardStyle = AccountCardStyle(),
    dimens: AccountBindingDimens = AccountBindingDimens(),
    textStyles: AccountBindingTextStyles = AccountBindingTextStyles(),
    divider: @Composable () -> Unit = { HorizontalDivider() },
    trailingIcon: @Composable RowScope.() -> Unit,
    footerSection: @Composable ColumnScope.() -> Unit = {}
) {
    val providerContent: @Composable RowScope.(AccountProvider) -> Unit = { provider: AccountProvider ->
        provider.iconResId?.let { resId ->
            Icon(
                painter = painterResource(id = resId),
                contentDescription = null,
                tint = Color.Unspecified,
                modifier = Modifier
                    .padding(cardStyle.providerIconPadding)
                    .size(cardStyle.providerIconSize)
            )
        }

        Text(
            text = stringResource(id = provider.nameResId),
            style = textStyles.providerStyle
        )
    }

    val deleteAccount = @Composable { provider: AccountProvider ->
        Text(
            text = titles.deleteAccountTitle,
            style = textStyles.deleteAccountStyle,
            modifier = Modifier
                .debouncedClickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                    role = Role.Button,
                    onClick = {
                        onUnbindAccountClick(provider)
                    }
                )
        )
    }

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(dimens.sectionsSpacing)
    ) {
        sections.forEach { section ->
            Column(
                verticalArrangement = Arrangement.spacedBy(dimens.accountsSpacing)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = section.title,
                        style = textStyles.sectionTitleStyle
                    )

                    section.trailingContent?.invoke(this@Row)
                }

                section.accounts.forEach { (provider, data) ->
                    val accountIdentifier = data?.identifier
                    val hasIdentifier = accountIdentifier != null
                    val isCurrent = data?.isCurrent == true
                    val hasAccount = data != null

                    when (provider) {
                        is PasswordBasedAccountProvider -> {
                            PasswordBasedAccount(
                                isCurrentAccount = isCurrent,
                                hasAccount = hasAccount,
                                hasIdentifier = hasIdentifier,
                                onAddAccountClick = {
                                    onAddAccountClick(provider)
                                },
                                onEditPasswordClick = {
                                    onEditPasswordClick(provider)
                                },
                                onEditIdentifierClick = {
                                    onEditIdentifierClick(provider)
                                },
                                divider = divider,
                                trailingIcon = {
                                    Spacer(Modifier.width(cardStyle.trailingIconSpacing))

                                    trailingIcon()
                                },
                                providerContent = {
                                    providerContent(provider)
                                },
                                accountIdentifier = {
                                    Text(
                                        text = accountIdentifier.orEmpty(),
                                        style = textStyles.accountIdentifierStyle,
                                        overflow = TextOverflow.Ellipsis,
                                        maxLines = 1
                                    )
                                },
                                passwordContent = {
                                    Text(
                                        text = titles.passwordTitle,
                                        style = textStyles.passwordStyle
                                    )
                                },
                                noAccountContent = {
                                    Text(
                                        text = titles.addAccountTitle,
                                        style = textStyles.addAccountStyle,
                                        overflow = TextOverflow.Ellipsis,
                                        maxLines = 1
                                    )
                                },
                                currentAccount = {
                                    Text(
                                        text = titles.currentAccountTitle,
                                        style = textStyles.currentAccountStyle
                                    )
                                },
                                deleteAccount = {
                                    deleteAccount(provider)
                                },
                                containerColor = cardStyle.containerColor,
                                shape = cardStyle.shape,
                                shadow = cardStyle.shadow,
                                contentPadding = cardStyle.passwordBasedContentPadding,
                                supportingContentPadding = cardStyle.supportingContentPadding
                            )
                        }

                        else -> {
                            SocialAccount(
                                hasAccount = hasAccount,
                                isCurrentAccount = isCurrent,
                                hasIdentifier = hasAccount,
                                containerColor = cardStyle.containerColor,
                                shape = cardStyle.shape,
                                shadow = cardStyle.shadow,
                                onAddAccountClick = {
                                    onAddAccountClick(provider)
                                },
                                contentPadding = cardStyle.socialContentPadding,
                                supportingContentPadding = cardStyle.supportingContentPadding,
                                providerContent = {
                                    providerContent(provider)
                                },
                                accountIdentifier = {
                                    Text(
                                        text = accountIdentifier.orEmpty(),
                                        style = textStyles.accountIdentifierStyle,
                                        overflow = TextOverflow.Ellipsis,
                                        maxLines = 1
                                    )
                                },
                                addAccount = {
                                    Text(
                                        text = titles.addAccountTitle,
                                        style = textStyles.addAccountStyle,
                                        overflow = TextOverflow.Ellipsis,
                                        maxLines = 1
                                    )
                                },
                                deleteAccount = {
                                    deleteAccount(provider)
                                },
                                currentAccount = {
                                    Text(
                                        text = titles.currentAccountTitle,
                                        style = textStyles.currentAccountStyle
                                    )
                                }
                            )
                        }
                    }
                }
            }
        }

        footerSection()
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFEFEFEF)
@Composable
private fun AccountBindingContainerPreview() {
    val emailAccount = remember { AccountData(identifier = "someone@gmail.com", isCurrent = true) }
    val googleAccount = remember { AccountData(identifier = "someone@gmail.com", isCurrent = false) }
    val appleAccount = remember { null }
    val facebookAccount = remember { null }

    val titleInfoBadge: @Composable RowScope.() -> Unit = {
        Box(
            modifier = Modifier
                .clip(CircleShape)
                .clickable {}
                .padding(Dimens.spacingTiny)
        ) {
            Icon(
                imageVector = Icons.Default.Info,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.error,
                modifier = Modifier.size(20.dp)
            )
        }
    }

    AccountBindingContainer(
        modifier = Modifier
            .fillMaxSize()
            .padding(Dimens.spacingNormal),
        sections = listOf(
            AccountsSection(
                title = "Account with mail:",
                accounts = mapOf(
                    EmailAccountProvider() to emailAccount
                ),
                trailingContent = titleInfoBadge
            ),
            AccountsSection(
                title = "Social accounts:",
                accounts = mapOf(
                    SocialAccountProvider.Google to googleAccount,
                    SocialAccountProvider.Apple to appleAccount,
                    SocialAccountProvider.Facebook to facebookAccount
                )
            )
        ),
        textStyles = AccountBindingTextStyles(
            sectionTitleStyle = MaterialTheme.typography.titleMedium,
            providerStyle = MaterialTheme.typography.bodyLarge,
            accountIdentifierStyle = MaterialTheme.typography.bodyMedium,
            passwordStyle = MaterialTheme.typography.bodyLarge,
            addAccountStyle = MaterialTheme.typography.labelLarge.copy(color = MaterialTheme.colorScheme.primary),
            currentAccountStyle = MaterialTheme.typography.labelMedium,
            deleteAccountStyle = MaterialTheme.typography.labelMedium.copy(color = MaterialTheme.colorScheme.error)
        ),
        trailingIcon = {
            Icon(
                imageVector = Icons.Rounded.KeyboardArrowRight,
                contentDescription = null
            )
        },
        titles = AccountBindingTitles(
            passwordTitle = "Password",
            addAccountTitle = "Add account",
            currentAccountTitle = "Your current account",
            deleteAccountTitle = "Delete"
        ),
        onUnbindAccountClick = {},
        onEditPasswordClick = {},
        onEditIdentifierClick = {},
        onAddAccountClick = {}
    )
}