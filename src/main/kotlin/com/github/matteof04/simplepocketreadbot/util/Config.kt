/*
 * Copyright (C) 2022 Matteo Franceschini <matteof5730@gmail.com>
 *
 * This file is part of simplepocketreadbot.
 * simplepocketreadbot is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * simplepocketreadbot is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License along with simplepocketreadbot.  If not, see <https://www.gnu.org/licenses/>.
 */

package com.github.matteof04.simplepocketreadbot.util

import java.io.File
import java.util.*

class Config(path: String) {
    private val properties = Properties()
    init {
        val file = File(path)
        properties.load(file.inputStream())
    }
    fun getBotToken() = properties.getProperty("botToken", "") ?: ""
    fun getConsumerKey() = properties.getProperty("consumerKey", "") ?: ""
    fun getRedirectUri() = properties.getProperty("redirectUri", "") ?: ""
    fun getJdbcUrl() = properties.getProperty("jdbcUrl", "") ?: ""
}