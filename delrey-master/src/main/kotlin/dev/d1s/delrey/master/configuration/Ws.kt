package dev.d1s.delrey.master.configuration

import dev.d1s.exkt.ktor.server.koin.configuration.ApplicationConfigurer
import io.ktor.serialization.kotlinx.*
import io.ktor.server.application.*
import io.ktor.server.config.*
import io.ktor.server.websocket.*
import kotlinx.serialization.json.Json
import org.koin.core.module.Module

object Ws : ApplicationConfigurer {

    override fun Application.configure(module: Module, config: ApplicationConfig) {
        install(WebSockets) {
            contentConverter = KotlinxWebsocketSerializationConverter(Json)
        }
    }
}