package dev.d1s.delrey.daemon.config

import com.sksamuel.hoplite.ConfigLoaderBuilder
import com.sksamuel.hoplite.sources.CommandLinePropertySource
import com.sksamuel.hoplite.sources.EnvironmentVariablesPropertySource
import dev.d1s.delrey.daemon.MainArgs
import dev.d1s.delrey.daemon.service.PersistentConfigService
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

private const val COMMAND_LINE_DELIMITER = "="

private const val ENV_VAR_PREFIX = "DELREY_DAEMON__"

interface ApplicationConfigFactory {

    val config: ApplicationConfig
}

class DefaultApplicationConfigFactory : ApplicationConfigFactory, KoinComponent {

    private val persistentConfigService by inject<PersistentConfigService>()

    override val config = loadConfig()

    private fun loadConfig(): ApplicationConfig {
        val commandLinePropertySource = CommandLinePropertySource(
            arguments = MainArgs,
            prefix = "",
            delimiter = COMMAND_LINE_DELIMITER
        )

        val environmentVariablePropertySource = EnvironmentVariablesPropertySource(
            useUnderscoresAsSeparator = true,
            allowUppercaseNames = true,
            prefix = ENV_VAR_PREFIX
        )

        val loadedConfig: ApplicationConfig = ConfigLoaderBuilder.default()
            .addPropertySource(commandLinePropertySource)
            .addPropertySource(environmentVariablePropertySource)
            .build()
            .loadConfigOrThrow()

        return persistentConfigService.sync(loadedConfig)
    }
}