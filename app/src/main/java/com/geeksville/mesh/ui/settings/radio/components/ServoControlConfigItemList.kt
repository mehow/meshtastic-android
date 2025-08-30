/*
 * Copyright (c) 2025 Meshtastic LLC
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

package com.geeksville.mesh.ui.settings.radio.components

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.geeksville.mesh.ModuleConfigProtos
import com.geeksville.mesh.R
import com.geeksville.mesh.copy
import com.geeksville.mesh.moduleConfig
import com.geeksville.mesh.ui.common.components.EditListPreference
import com.geeksville.mesh.ui.common.components.EditTextPreference
import com.geeksville.mesh.ui.common.components.PreferenceCategory
import com.geeksville.mesh.ui.common.components.PreferenceFooter
import com.geeksville.mesh.ui.common.components.SwitchPreference
import com.geeksville.mesh.ui.settings.radio.RadioConfigViewModel

@Composable
fun ServoControlConfigScreen(viewModel: RadioConfigViewModel = hiltViewModel()) {
    val state by viewModel.radioConfigState.collectAsStateWithLifecycle()

    if (state.responseState.isWaiting()) {
        PacketResponseStateDialog(state = state.responseState, onDismiss = viewModel::clearPacketResponse)
    }

    ServoControlConfigItemList(
        servoControlConfig = state.moduleConfig.servoControl,
        enabled = state.connected,
        onSaveClicked = { servoControlInput ->
            val config = moduleConfig { servoControl = servoControlInput }
            viewModel.setModuleConfig(config)
        },
    )
}

@Composable
fun ServoControlConfigItemList(
    servoControlConfig: ModuleConfigProtos.ModuleConfig.ServoControlConfig,
    enabled: Boolean,
    onSaveClicked: (ModuleConfigProtos.ModuleConfig.ServoControlConfig) -> Unit,
) {
    val focusManager = LocalFocusManager.current
    var servoControlInput by rememberSaveable { mutableStateOf(servoControlConfig) }

    LazyColumn(modifier = Modifier.fillMaxSize()) {
        item { PreferenceCategory(text = stringResource(R.string.servo_control_config)) }

        item {
            SwitchPreference(
                title = stringResource(R.string.servo_control_enabled),
                checked = servoControlInput.enabled,
                enabled = enabled,
                onCheckedChange = { servoControlInput = servoControlInput.copy { this.enabled = it } },
            )
        }
        item { HorizontalDivider() }

        item {
            EditTextPreference(
                title = stringResource(R.string.gpio_pin),
                value = servoControlInput.gpioPin,
                enabled = enabled,
                keyboardActions = KeyboardActions(onDone = { focusManager.clearFocus() }),
                onValueChanged = { servoControlInput = servoControlInput.copy { gpioPin = it } },
            )
        }

        item {
            EditTextPreference(
                title = stringResource(R.string.open_position),
                value = servoControlInput.openPosition,
                enabled = enabled,
                keyboardActions = KeyboardActions(onDone = { focusManager.clearFocus() }),
                onValueChanged = { servoControlInput = servoControlInput.copy { openPosition = it } },
            )
        }

        item {
            EditTextPreference(
                title = stringResource(R.string.closed_position),
                value = servoControlInput.closedPosition,
                enabled = enabled,
                keyboardActions = KeyboardActions(onDone = { focusManager.clearFocus() }),
                onValueChanged = { servoControlInput = servoControlInput.copy { closedPosition = it } },
            )
        }

        item {
            EditListPreference(
                title = stringResource(R.string.authorized_key),
                list = servoControlInput.authorizedKeyList,
                maxCount = 3,
                enabled = enabled,
                keyboardActions = KeyboardActions(onDone = { focusManager.clearFocus() }),
                onValuesChanged = {
                    servoControlInput =
                        servoControlInput.copy {
                            authorizedKey.clear()
                            authorizedKey.addAll(it)
                        }
                },
            )
        }

        item {
            PreferenceFooter(
                enabled = enabled && servoControlInput != servoControlConfig,
                onCancelClicked = {
                    focusManager.clearFocus()
                    servoControlInput = servoControlConfig
                },
                onSaveClicked = {
                    focusManager.clearFocus()
                    onSaveClicked(servoControlInput)
                },
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun ServoControlConfigPreview() {
    ServoControlConfigItemList(
        servoControlConfig = ModuleConfigProtos.ModuleConfig.ServoControlConfig.getDefaultInstance(),
        enabled = true,
        onSaveClicked = {},
    )
}
