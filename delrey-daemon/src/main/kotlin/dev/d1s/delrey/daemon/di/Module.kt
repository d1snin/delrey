package dev.d1s.delrey.daemon.di

import dev.d1s.delrey.daemon.DelreyDaemonApplication
import dev.d1s.delrey.daemon.config.ApplicationConfigFactory
import dev.d1s.delrey.daemon.config.DefaultApplicationConfigFactory
import org.koin.core.context.GlobalContext.startKoin
import org.koin.core.module.Module
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module
import org.koin.logger.SLF4JLogger

fun setupDi() {
    startKoin {
        logger(SLF4JLogger())

        val mainModule = module {
            application()
            config()
        }

        modules(mainModule)
    }
}

fun Module.application() {
    singleOf(::DelreyDaemonApplication)
}

fun Module.config() {
    singleOf<ApplicationConfigFactory>(::DefaultApplicationConfigFactory)

    single {
        get<ApplicationConfigFactory>().config
    }
}