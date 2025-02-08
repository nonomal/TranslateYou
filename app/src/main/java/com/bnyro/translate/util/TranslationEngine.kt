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

package com.bnyro.translate.util

import com.bnyro.translate.const.ApiKeyState
import com.bnyro.translate.db.obj.Language
import com.bnyro.translate.obj.Translation
import java.io.File
import okhttp3.HttpUrl.Companion.toHttpUrlOrNull

abstract class TranslationEngine(
    val name: String,
    val defaultUrl: String,
    val urlModifiable: Boolean,
    val apiKeyState: ApiKeyState,
    val autoLanguageCode: String?,
    val supportsSimTranslation: Boolean = true,
    val supportsAudio: Boolean = false,
    val supportedEngines: List<String> = emptyList(),
) {

    abstract fun createOrRecreate(): TranslationEngine

    abstract suspend fun getLanguages(): List<Language>

    abstract suspend fun translate(query: String, source: String, target: String): Translation

    val urlPrefKey = this.name + Preferences.instanceUrlKey
    val apiPrefKey = this.name + Preferences.apiKey
    val simPrefKey = this.name + Preferences.simultaneousTranslationKey
    val selEnginePrefKey = this.name + Preferences.selectedEngine

    open fun getUrl(): String {
        return Preferences.get(
            urlPrefKey,
            this.defaultUrl
        ).toHttpUrlOrNull()?.toString() ?: defaultUrl
    }

    open suspend fun getAudioFile(lang: String, query: String): File? = null

    fun getApiKey() = Preferences.get(
        apiPrefKey,
        ""
    )

    fun sourceOrAuto(source: String): String {
        return source.ifEmpty { autoLanguageCode }.orEmpty()
    }

    fun isSimultaneousTranslationEnabled() = Preferences.get(
        simPrefKey,
        false
    )

    fun getSelectedEngine() = Preferences.get(selEnginePrefKey, supportedEngines.first())

    override fun equals(other: Any?): Boolean {
        if (other is TranslationEngine) {
            return this.name == other.name && this.getUrl() == other.getUrl() && this.getApiKey() == other.getApiKey()
        }

        return false
    }
}
