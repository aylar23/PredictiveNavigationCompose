package com.aylar.predictivenavigationcompose.screens

import androidx.activity.BackEventCompat
import androidx.activity.OnBackPressedCallback
import androidx.activity.compose.BackHandler
import androidx.activity.compose.LocalOnBackPressedDispatcherOwner
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aylar.predictivenavigationcompose.R

@Composable
fun TextScreen(
    onButtonClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val shortText = stringResource(R.string.tap_to_expand)
    val longText = stringResource(R.string.long_text)

    var isTextExpanded by remember { mutableStateOf(false) }
    var backProgress: Float? by remember {
        mutableStateOf(null)
    }
    var textHeightExpanded by remember {
        mutableStateOf(0.dp)
    }
    var textHeightCollapsed by remember {
        mutableStateOf(0.dp)
    }

    val onBackCallback = remember {
        object : OnBackPressedCallback(true) {
            override fun handleOnBackProgressed(backEvent: BackEventCompat) {
                backProgress = backEvent.progress
            }

            override fun handleOnBackPressed() {
                isTextExpanded = false
                backProgress = null
            }

            override fun handleOnBackCancelled() {
                backProgress = null
            }
        }
    }

    val backPressedDispatcher = LocalOnBackPressedDispatcherOwner.current!!.onBackPressedDispatcher
    DisposableEffect(backPressedDispatcher, isTextExpanded) {
        if(isTextExpanded) {
            backPressedDispatcher.addCallback(onBackCallback)
        }

        onDispose {
            onBackCallback.remove()
        }
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = modifier.fillMaxSize()
    ) {
        Text(
            text = "Screen A",
            fontSize = 42.sp
        )
        Button(
            onClick = {
                onButtonClick()
            }
        ) {
            Text(text = "Next")
        }

        val density = LocalDensity.current
        Text(
            text = if (isTextExpanded) longText else shortText,
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .onGloballyPositioned {
                    when {
                        isTextExpanded && backProgress == null -> {
                            textHeightExpanded = with(density) {
                                it.size.height.toDp()
                            }
                        }
                        !isTextExpanded -> {
                            textHeightCollapsed = with(density) {
                                it.size.height.toDp()
                            }
                        }
                    }
                }
                .then(
                    if(backProgress != null) {
                        Modifier
                            .heightIn(min = textHeightCollapsed)
                            .height(
                                textHeightExpanded * (1f - (backProgress ?: 0f))
                            )
                    } else Modifier
                )
                .fillMaxWidth()
                .clickable { isTextExpanded = !isTextExpanded }
                .animateContentSize()
                .padding(32.dp)
        )
    }
}