package com.urlaunched.android.design.ui.accountbinding.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import com.urlaunched.android.design.resources.dimens.Dimens
import com.urlaunched.android.design.ui.accountbinding.constants.SocialAccountDimens
import com.urlaunched.android.design.ui.clickable.debouncedClickable
import com.urlaunched.android.design.ui.modifiers.ifNotNull
import com.urlaunched.android.design.ui.shadow.models.ShadowStyle
import com.urlaunched.android.design.ui.shadow.shadow

@Composable
fun SocialAccount(
    modifier: Modifier = Modifier,
    isCurrentAccount: Boolean,
    hasAccount: Boolean,
    hasIdentifier: Boolean,
    containerColor: Color = Color.White,
    shape: Shape = RoundedCornerShape(Dimens.cornerRadiusBig),
    shadow: ShadowStyle? = null,
    onAddAccountClick: () -> Unit,
    contentPadding: PaddingValues = SocialAccountDimens.defaultContentPadding,
    supportingContentPadding: PaddingValues = PaddingValues(top = Dimens.spacingSmall),
    providerContent: @Composable RowScope.() -> Unit,
    accountIdentifier: @Composable RowScope.() -> Unit,
    addAccount: @Composable RowScope.() -> Unit,
    deleteAccount: @Composable ColumnScope.() -> Unit,
    currentAccount: @Composable ColumnScope.() -> Unit
) {
    SocialAccount(
        cardModifier = Modifier
            .ifNotNull(shadow) { Modifier.shadow(it) }
            .clip(shape)
            .background(containerColor),
        modifier = modifier,
        enabled = !hasAccount,
        hasIdentifier = hasIdentifier,
        onAddAccountClick = onAddAccountClick,
        contentPadding = contentPadding,
        accountIdentifier = accountIdentifier,
        addAccount = addAccount,
        providerContent = providerContent,
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
fun SocialAccount(
    modifier: Modifier = Modifier,
    cardModifier: Modifier = Modifier,
    hasIdentifier: Boolean,
    enabled: Boolean = true,
    onAddAccountClick: () -> Unit,
    contentPadding: PaddingValues = SocialAccountDimens.defaultContentPadding,
    providerContent: @Composable RowScope.() -> Unit,
    accountIdentifier: @Composable RowScope.() -> Unit,
    addAccount: @Composable RowScope.() -> Unit,
    supportingContent: @Composable ColumnScope.() -> Unit = {}
) {
    Column(
        modifier = modifier
    ) {
        Column(modifier = cardModifier) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .debouncedClickable(
                        enabled = enabled,
                        onClick = onAddAccountClick
                    )
                    .padding(contentPadding)
            ) {
                providerContent()

                Spacer(Modifier.weight(1f))

                if (hasIdentifier) {
                    accountIdentifier()
                } else {
                    addAccount()
                }
            }
        }

        supportingContent()
    }
}

@Preview
@Composable
private fun SocialAccountPreview() {
    SocialAccount(
        hasIdentifier = true,
        hasAccount = true,
        isCurrentAccount = true,
        modifier = Modifier
            .background(Color.LightGray)
            .padding(Dimens.spacingNormal),
        onAddAccountClick = {},
        providerContent = {
            Icon(
                imageVector = Icons.Default.Email,
                contentDescription = null
            )

            Spacer(Modifier.width(Dimens.spacingSmall))

            Text(
                text = "Gmail",
                fontWeight = FontWeight.Bold
            )
        },
        accountIdentifier = {
            Text(
                text = "someone@gmail.com"
            )
        },
        addAccount = {
            Text(
                text = "Add account",
                modifier = Modifier.padding(end = Dimens.spacingSmall)
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