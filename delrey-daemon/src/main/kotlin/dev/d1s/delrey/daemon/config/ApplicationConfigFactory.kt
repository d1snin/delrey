package dev.d1s.delrey.daemon.config

import com.sksamuel.hoplite.ConfigLoaderBuilder
import com.sksamuel.hoplite.sources.CommandLinePropertySource
import com.sksamuel.hoplite.sources.EnvironmentVariablesPropertySource
import org.lighthousegames.logging.logging
import java.lang.management.ManagementFactory

private const val COMMAND_LINE_PREFIX = "delrey.daemon."
private const val COMMAND_LINE_DELIMITER = "="

private const val ENV_VAR_PREFIX = "DELREY_DAEMON__"

interface ApplicationConfigFactory {

    val config: ApplicationConfig
}

class DefaultApplicationConfigFactory : ApplicationConfigFactory {

    private val log = logging()

    override val config = loadConfig()

    private fun loadConfig(): ApplicationConfig {
        log.i {
            "Loading config from environment variables..."
        }

        val commandLinePropertySource = CommandLinePropertySource(
            arguments = ManagementFactory.getRuntimeMXBean().inputArguments.toTypedArray(),
            prefix = COMMAND_LINE_PREFIX,
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