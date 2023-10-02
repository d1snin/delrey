package dev.d1s.delrey.daemon.di

import dev.d1s.delrey.client.masterClient
import dev.d1s.delrey.daemon.DelreyDaemonApplication
import dev.d1s.delrey.daemon.config.ApplicationConfig
import dev.d1s.delrey.daemon.config.ApplicationConfigFactory
import dev.d1s.delrey.daemon.config.DefaultApplicationConfigFactory
import dev.d1s.delrey.daemon.service.CommandRunner
import dev.d1s.delrey.daemon.service.DefaultCommandRunner
import dev.d1s.delrey.daemon.service.DefaultSessionListener
import dev.d1s.delrey.daemon.service.SessionListener
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
            client()
            commandRunner()
            sessionListener()
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

fun Module.client() {
    single {
        val config = get<ApplicationConfig>()
        val masterConfig = config.master

        masterClient(masterConfig.httpBase, masterConfig.wsBase, config.whoami)
    }
}

fun Module.commandRunner() {
    singleOf<CommandRunner>(::DefaultCommandRunner)
}

fun Module.sessionListener() {
    singleOf<SessionListener>(::DefaultSessionListener)
}