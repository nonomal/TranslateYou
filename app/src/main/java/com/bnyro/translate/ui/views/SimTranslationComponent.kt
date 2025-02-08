/*
 * Copyright (c) 2023 You Apps
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package com.bnyro.translate.ui.views

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ElevatedFilterChip
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.bnyro.translate.R
import com.bnyro.translate.obj.ListPreferenceOption
import com.bnyro.translate.obj.Translation
import com.bnyro.translate.ui.components.prefs.ListPreferenceDialog
import com.bnyro.translate.ui.models.TranslationModel

@Composable
fun SimTranslationComponent(
    viewModel: TranslationModel
) {
    var selected by remember {
        mutableStateOf(
            viewModel.engine
        )
    }

    LazyRow {
        items(viewModel.enabledSimEngines) {
            ElevatedFilterChip(
                selected = selected == it,
                onClick = {
                    selected = it
                    viewModel.engine = it
                    viewModel.translation = viewModel.translatedTexts[it.name] ?: Translation("")
                },
                label = {
                    Text(it.name)
                },
                modifier = Modifier
                    .padding(5.dp, 0.dp)
            )
        }
    }
}

@Composable
fun SimTranslationDialogComponent(
    viewModel: TranslationModel
) {
    var selected by remember {
        mutableStateOf(viewModel.engine)
    }
    var showSelectionDialog by remember {
        mutableStateOf(false)
    }

    ElevatedFilterChip(
        selected = true,
        onClick = {
            showSelectionDialog = true
        },
        label = {
            Text(selected.name)
        },
        modifier = Modifier
            .padding(5.dp, 0.dp)
    )

    if (showSelectionDialog) {
        ListPreferenceDialog(
            preferenceKey = null,
            onDismissRequest = { showSelectionDialog = false },
            title = stringResource(R.string.selected_engine),
            options = viewModel.enabledSimEngines.mapIndexed { index, engine ->
                ListPreferenceOption(engine.name, index)
            },
            currentValue = viewModel.enabledSimEngines.indexOf(viewModel.engine).takeIf { it >= 0 }
        ) { engineOption ->
            selected = viewModel.enabledSimEngines[engineOption.value]
            viewModel.engine = selected
            viewModel.translation = viewModel.translatedTexts[selected.name] ?: Translation("")
        }
    }
}
