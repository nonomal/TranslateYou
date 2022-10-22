package com.bnyro.translate.ui.views

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.ContentPaste
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.bnyro.translate.R
import com.bnyro.translate.ui.components.ButtonWithIcon
import com.bnyro.translate.ui.components.StyledIconButton
import com.bnyro.translate.ui.components.StyledTextField
import com.bnyro.translate.ui.models.MainModel
import com.bnyro.translate.util.ClipboardHelper
import com.bnyro.translate.util.SimTranslationComponent
import com.bnyro.translate.util.SpeechHelper

@Composable
fun TranslationComponent(
    focusRequester: FocusRequester
) {
    val viewModel: MainModel = viewModel()
    val context = LocalContext.current

    val clipboardHelper = ClipboardHelper(
        LocalContext.current.applicationContext
    )
    var hasClip by remember {
        mutableStateOf(
            clipboardHelper.hasClip()
        )
    }

    Column(
        modifier = Modifier
            .padding(15.dp)
            .fillMaxSize()
    ) {
        StyledTextField(
            text = viewModel.insertedText,
            onValueChange = {
                viewModel.insertedText = it
                if (it == "") hasClip = clipboardHelper.hasClip()
                viewModel.enqueueTranslation()
            },
            placeholder = stringResource(R.string.enter_text),
            modifier = Modifier.focusRequester(focusRequester)
        )

        Divider(
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier
                .padding(10.dp)
                .align(alignment = Alignment.CenterHorizontally)
                .size(70.dp, 1.dp)
        )

        if (viewModel.translation != "" && SpeechHelper.ttsAvailable) {
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.CenterEnd
            ) {
                StyledIconButton(
                    imageVector = Icons.Default.VolumeUp
                ) {
                    SpeechHelper.speak(
                        context,
                        viewModel.translation,
                        viewModel.targetLanguage.code
                    )
                }
            }
        }

        if (hasClip && viewModel.insertedText == "") {
            Row {
                ButtonWithIcon(
                    text = stringResource(R.string.paste),
                    icon = Icons.Default.ContentPaste
                ) {
                    viewModel.insertedText = clipboardHelper.get() ?: ""
                    viewModel.enqueueTranslation()
                }

                Spacer(
                    modifier = Modifier
                        .width(0.dp)
                )

                ButtonWithIcon(
                    text = stringResource(R.string.forget),
                    icon = Icons.Default.Clear
                ) {
                    clipboardHelper.clear()
                    hasClip = false
                    viewModel.clearTranslation()
                }
            }
        }

        StyledTextField(
            text = viewModel.translation,
            onValueChange = {},
            readOnly = true,
            modifier = Modifier
                .weight(1.0f)
        )

        if (viewModel.simTranslationEnabled) {
            SimTranslationComponent()
        } else {
            Divider(
                color = Color.Gray,
                modifier = Modifier
                    .align(alignment = Alignment.CenterHorizontally)
                    .size(70.dp, 2.dp)
            )
        }
    }
}
