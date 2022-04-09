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

plugins {
    kotlin("jvm") version "1.6.10"
    kotlin("plugin.serialization") version "1.6.10"
    id("de.comahe.i18n4k") version "0.3.0"
    application
}

group = "com.github.matteof04"
version = "1.0.0"

i18n4k {
    sourceCodeLocales = listOf("en", "it")
}

application {
    mainClass.set("com.github.matteof04.simplepocketreadbot.MainKt")
}

repositories {
    mavenCentral()
    maven(url = "https://jitpack.io")
}

val logbackVersion: String by project
val exposedVersion: String by project
val hikariVersion: String by project
val sqliteVersion: String by project
val ktbVersion: String by project

dependencies {
    implementation("ch.qos.logback:logback-classic:$logbackVersion")
    implementation("org.jetbrains.exposed:exposed-core:$exposedVersion")
    implementation("org.jetbrains.exposed:exposed-dao:$exposedVersion")
    implementation("org.jetbrains.exposed:exposed-jdbc:$exposedVersion")
    implementation("com.zaxxer:HikariCP:$hikariVersion")
    implementation("org.xerial:sqlite-jdbc:$sqliteVersion")
    implementation("io.github.kotlin-telegram-bot:kotlin-telegram-bot:$ktbVersion"){
        exclude(module = "webhook")
    }
    implementation("de.comahe.i18n4k:i18n4k-core-jvm:0.3.0")
    implementation("com.github.matteof04:PocketApi:1.0.0")
}

tasks.withType<Tar> {
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
}

tasks.withType<Zip>{
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
}

tasks.withType<Jar>{
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
}