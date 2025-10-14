package com.urlaunched.android.design.ui.accountbinding.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.KeyboardArrowRight
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import com.urlaunched.android.design.resources.dimens.Dimens
import com.urlaunched.android.design.ui.clickable.debouncedClickable
import com.urlaunched.android.design.ui.modifiers.ifNotNull
import com.urlaunched.android.design.ui.shadow.models.ShadowStyle
import com.urlaunched.android.design.ui.shadow.shadow

@Composable
fun PasswordBasedAccount(
    modifier: Modifier = Modifier,
    isCurrentAccount: Boolean,
    hasAccount: Boolean,
    hasCredential: Boolean,
    containerColor: Color = Color.White,
    shape: Shape = RoundedCornerShape(Dimens.cornerRadiusBig),
    shadow: ShadowStyle? = null,
    onEditCredentialClick: () -> Unit,
    onAddAccountClick: () -> Unit,
    onEditPasswordClick: () -> Unit,
    contentPadding: PaddingValues = PaddingValues(Dimens.spacingNormal),
    supportingContentPadding: PaddingValues = PaddingValues(top = Dimens.spacingSmall),
    divider: @Composable () -> Unit = {
        HorizontalDivider()
    },
    providerContent: @Composable RowScope.() -> Unit,
    accountCredential: @Composable RowScope.() -> Unit,
    passwordContent: @Composable RowScope.() -> Unit,
    trailingIcon: @Composable RowScope.() -> Unit,
    noAccountContent: @Composable RowScope.() -> Unit,
    deleteAccount: @Composable ColumnScope.() -> Unit,
    currentAccount: @Composable ColumnScope.() -> Unit
) {
    PasswordBasedAccount(
        cardModifier = Modifier
            .ifNotNull(shadow) { Modifier.shadow(it) }
            .clip(shape)
            .background(containerColor),
        modifier = modifier,
        hasAccount = hasAccount,
        hasCredential = hasCredential,
        onEditCredentialClick = onEditCredentialClick,
        onAddAccountClick = onAddAccountClick,
        onEditPasswordClick = onEditPasswordClick,
        contentPadding = contentPadding,
        divider = divider,
        providerContent = providerContent,
        accountCredential = accountCredential,
        passwordContent = passwordContent,
        trailingIcon = trailingIcon,
        noAccountContent = noAccountContent,
        supportingContent = {
            if (hasAccount) {
                Column(
                    horizontalAlignment = Alignment.End,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(supportingContentPadding)
                ) {
                    if (isCurrentAccount) {
                        currentAccount()
                    } else {
                        deleteAccount()
                    }
                }
            }
        }
    )
}

@Composable
fun PasswordBasedAccount(
    modifier: Modifier = Modifier,
    cardModifier: Modifier = Modifier,
    hasAccount: Boolean,
    hasCredential: Boolean,
    onEditCredentialClick: () -> Unit,
    onAddAccountClick: () -> Unit,
    onEditPasswordClick: () -> Unit,
    contentPadding: PaddingValues = PaddingValues(Dimens.spacingNormal),
    divider: @Composable () -> Unit = {
        HorizontalDivider()
    },
    providerContent: @Composable RowScope.() -> Unit,
    accountCredential: @Composable RowScope.() -> Unit,
    passwordContent: @Composable RowScope.() -> Unit,
    trailingIcon: @Composable RowScope.() -> Unit,
    noAccountContent: @Composable RowScope.() -> Unit,
    supportingContent: @Composable ColumnScope.() -> Unit = {}
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.End
    ) {
        Column(modifier = cardModifier) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .debouncedClickable {
                        if (hasAccount) {
                            onEditCredentialClick()
                        } else {
                            onAddAccountClick()
                        }
                    }
                    .padding(contentPadding)
            ) {
                providerContent()

                Spacer(Modifier.weight(1f))

                if (hasCredential) {
                    accountCredential()

                    trailingIcon()
                } else {
                    noAccountContent()
                }
            }

            if (hasAccount) {
                divider()

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier
                        .fillMaxWidth()
                        .debouncedClickable(onClick = onEditPasswordClick)
                        .padding(contentPadding)
                ) {
                    passwordContent()

                    trailingIcon()
                }
            }
        }

        supportingContent()
    }
}

@Preview
@Composable
private fun PasswordBasedAccountPreview() {
    PasswordBasedAccount(
        isCurrentAccount = true,
        hasAccount = true,
        hasCredential = true,
        modifier = Modifier
            .background(Color.LightGray)
            .padding(Dimens.spacingNormal),
        onEditCredentialClick = { },
        onAddAccountClick = { },
        onEditPasswordClick = { },
        providerContent = {
            Text(
                text = "Gmail",
                fontWeight = FontWeight.Bold
            )
        },
        accountCredential = {
            Text(
                text = "someone@gmail.com",
                textAlign = TextAlign.End,
                modifier = Modifier.padding(end = Dimens.spacingSmall)
            )
        },
        passwordContent = {
            Text("Password")
        },
        trailingIcon = {
            Icon(
                imageVector = Icons.Rounded.KeyboardArrowRight,
                contentDescription = null
            )
        },
        noAccountContent = {
            Text(
                text = "Add account"
            )
        },
        currentAccount = {
            Text(
                text = "Current account"
            )
        },
        deleteAccount = {
            Text(
                text = "Delete",
                color = Color.Red
            )
        }
    )
}