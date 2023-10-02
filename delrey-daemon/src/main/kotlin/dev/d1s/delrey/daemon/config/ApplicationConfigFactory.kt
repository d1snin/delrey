package dev.d1s.delrey.daemon.config

import com.sksamuel.hoplite.ConfigLoaderBuilder
import com.sksamuel.hoplite.sources.CommandLinePropertySource
import com.sksamuel.hoplite.sources.EnvironmentVariablesPropertySource
import dev.d1s.delrey.daemon.MainArgs

private const val COMMAND_LINE_DELIMITER = "="

private const val ENV_VAR_PREFIX = "DELREY_DAEMON__"

interface ApplicationConfigFactory {

    val config: ApplicationConfig
}

class DefaultApplicationConfigFactory() : ApplicationConfigFactory {

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

        return ConfigLoaderBuilder.default()
            .addPropertySource(commandLinePropertySource)
            .addPropertySource(environmentVariablePropertySource)
            .build()
            .loadConfigOrThrow()
    }
}