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

object StringHelper {
    const val urlString = "https?:\\/\\/(www\\.)?[-a-zA-Z0-9@:%._\\+~#=]{1,256}\\.[a-zA-Z0-9()]{1,6}\\b([-a-zA-Z0-9()@:%_\\+.~#?&//=]*)"
}

fun String.isValidUrl() =  StringHelper.urlString.toRegex().find(this) != null
fun String.extractUrl() = StringHelper.urlString.toRegex().find(this)?.value