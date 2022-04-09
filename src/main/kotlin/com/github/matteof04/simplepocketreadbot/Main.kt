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

package com.github.matteof04.simplepocketreadbot

import com.github.kotlintelegrambot.bot
import com.github.kotlintelegrambot.dispatch
import com.github.kotlintelegrambot.dispatcher.callbackQuery
import com.github.kotlintelegrambot.dispatcher.command
import com.github.kotlintelegrambot.dispatcher.message
import com.github.kotlintelegrambot.entities.ChatId
import com.github.kotlintelegrambot.entities.InlineKeyboardMarkup
import com.github.kotlintelegrambot.entities.keyboard.InlineKeyboardButton
import com.github.kotlintelegrambot.extensions.filters.Filter
import com.github.matteof04.pocketapi.PocketApi
import com.github.matteof04.pocketapi.entities.Tags
import com.github.matteof04.simplepocketreadbot.data.UserController
import com.github.matteof04.simplepocketreadbot.data.Users
import com.github.matteof04.simplepocketreadbot.util.Config
import com.github.matteof04.simplepocketreadbot.util.extractUrl
import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import de.comahe.i18n4k.Locale
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction
import org.slf4j.LoggerFactory
import x.y.SimplePocketReadBot
import java.io.File

fun main() {
    println("""
        SimplePocketReadBot  Copyright (C) 2022  Matteo Franceschini
        This program comes with ABSOLUTELY NO WARRANTY.
        This is free software, and you are welcome to redistribute it
        under GNU AGPLv3.
    """.trimIndent())
    if(!File(System.getProperty("user.dir").plus("/botConfig.properties")).exists()){
        File(System.getProperty("user.dir").plus("/botConfig.properties")).createNewFile()
        LoggerFactory.getLogger("MAIN").error("Config file not found! Empty config file created.")
        return
    }
    val config = Config(System.getProperty("user.dir").plus("/botConfig.properties"))
    val hikariConfig = HikariConfig()
    hikariConfig.jdbcUrl = config.getJdbcUrl()
    Database.connect(HikariDataSource(hikariConfig))
    transaction {
        SchemaUtils.create(Users)
    }
    val userController = UserController()
    val pocketApi = PocketApi(config.getConsumerKey())
    val bot = bot {
        token = config.getBotToken()
        dispatch {
            command("start") {
                val language = Locale(message.from?.languageCode ?: "en")
                try{
                    val requestTokenResponse = pocketApi.getRequestToken(config.getRedirectUri())
                    userController.setRequestTokenFromChatId(message.chat.id, requestTokenResponse.requestToken)
                    val text = SimplePocketReadBot.greeting("https://getpocket.com/auth/authorize?request_token=${requestTokenResponse.requestToken}&redirect_uri=${config.getRedirectUri()}", language)
                    val authorizedText = SimplePocketReadBot.authorized(language)
                    val keyboard = InlineKeyboardMarkup.createSingleButton(InlineKeyboardButton.CallbackData(text = authorizedText, callbackData = "authorized"))
                    bot.sendMessage(chatId = ChatId.fromId(message.chat.id), text = text, replyMarkup = keyboard)
                }catch(_:Exception){
                    bot.sendMessage(ChatId.fromId(message.chat.id), SimplePocketReadBot.failure(language))
                }
            }
            callbackQuery("authorized") {
                val chatId = callbackQuery.message?.chat?.id ?: return@callbackQuery
                val language = Locale(callbackQuery.message?.from?.languageCode ?: "en")
                val requestToken = userController.getRequestTokenFromChatId(chatId) ?: return@callbackQuery
                try {
                    val authorizeResponse = pocketApi.getAccessToken(requestToken)
                    userController.setAccessTokenFromChatId(chatId, authorizeResponse.accessToken)
                    bot.sendMessage(ChatId.fromId(chatId), SimplePocketReadBot.successfullyAuthorized(language))
                }catch (_: Exception){
                    bot.sendMessage(ChatId.fromId(chatId), SimplePocketReadBot.authorizeFirst(language))
                }
            }
            message(Filter.Command.not().and(Filter.Text.or(Filter.Forward))) {
                val url = message.text?.extractUrl() ?: message.caption?.extractUrl()
                val language = Locale(message.from?.languageCode ?: "en")
                if(url != null){
                    val accessToken = userController.getAccessTokenFromChatId(message.chat.id)
                    if(accessToken != null) {
                        try {
                            val addResponse = pocketApi.getAuthorizedApi(accessToken).add(url, "", "", Tags())
                            if(addResponse.status == 1) bot.sendMessage(ChatId.fromId(message.chat.id), SimplePocketReadBot.addedSuccessfull(addResponse.item.title, language)) else throw Exception()
                        }catch (e:Exception){
                            println(e.localizedMessage)
                            bot.sendMessage(ChatId.fromId(message.chat.id), SimplePocketReadBot.failure(language))
                        }
                    }else{
                        bot.sendMessage(ChatId.fromId(message.chat.id), SimplePocketReadBot.authorizeFirst(language))
                    }
                }else{
                    bot.sendMessage(ChatId.fromId(message.chat.id), SimplePocketReadBot.noUrlFound(language))
                }
            }
        }
    }
    bot.startPolling()
}