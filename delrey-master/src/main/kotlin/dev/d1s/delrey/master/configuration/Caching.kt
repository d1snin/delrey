package dev.d1s.delrey.master.configuration

import dev.d1s.delrey.common.Run
import dev.d1s.delrey.common.RunId
import dev.d1s.exkt.ktor.server.koin.configuration.ApplicationConfigurer
import io.github.reactivecircus.cache4k.Cache
import io.ktor.server.application.*
import io.ktor.server.config.*
import org.koin.core.module.Module
import kotlin.time.Duration.Companion.hours

object Caching : ApplicationConfigurer {

    override fun Application.configure(module: Module, config: ApplicationConfig) {
        module.runCache()
    }

    private fun Module.runCache() {
        val expiration = 24.hours

        val runCache = Cache.Builder<RunId, Run>()
            .expireAfterWrite(expiration)
            .build()

        single {
            runCache
        }
    }
}