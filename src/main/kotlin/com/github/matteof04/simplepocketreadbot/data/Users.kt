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

package com.github.matteof04.simplepocketreadbot.data

import com.github.matteof04.simplepocketreadbot.data.Users.chatId
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.transactions.transaction

object Users: IntIdTable() {
    val chatId = long("chat_id")
    val requestToken = varchar("request_token", 255).default("")
    val accessToken = varchar("access_token", 255).default("")
}

class User(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<User>(Users)

    var chatId by Users.chatId
    var requestToken by Users.requestToken
    var accessToken by Users.accessToken
}

class UserController{
    fun getAccessTokenFromChatId(chatId: Long) = transaction {
        User.find { Users.chatId eq chatId }.firstOrNull()?.accessToken
    }
    fun getRequestTokenFromChatId(chatId: Long) = transaction {
        User.find { Users.chatId eq chatId }.firstOrNull()?.requestToken
    }
    fun setAccessTokenFromChatId(userChatId: Long, accessToken: String) = transaction {
        User.find { Users.chatId eq userChatId }.firstOrNull()?.accessToken = accessToken
    }
    fun setRequestTokenFromChatId(userChatId: Long, requestToken: String) = transaction {
        (User.find { Users.chatId eq userChatId }.firstOrNull() ?: User.new { chatId = userChatId }).requestToken = requestToken
    }
}