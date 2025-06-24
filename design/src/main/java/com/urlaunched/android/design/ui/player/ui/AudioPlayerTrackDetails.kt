package com.urlaunched.android.design.ui.player.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.urlaunched.android.design.resources.dimens.Dimens

@Composable
internal fun AudioPlayerTrackDetails(
    modifier: Modifier = Modifier,
    audioTrackDetails: @Composable () -> Unit,
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(Dimens.spacingTinyHalf))

        audioTrackDetails()

        Spacer(modifier = Modifier.height(Dimens.spacingBigSpecial))
    }
}