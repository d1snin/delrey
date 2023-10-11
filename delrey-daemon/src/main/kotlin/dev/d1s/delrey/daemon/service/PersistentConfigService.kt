package dev.d1s.delrey.daemon.service

import dev.d1s.delrey.daemon.config.ApplicationConfig
import kweb.shoebox.shoebox
import org.koin.core.component.KoinComponent
import org.lighthousegames.logging.logging
import java.nio.file.Files
import java.nio.file.Paths
import kotlin.io.path.div

interface PersistentConfigService {

    fun sync(applicationConfig: ApplicationConfig): ApplicationConfig
}

class DefaultPersistentConfigService : PersistentConfigService, KoinComponent {

    private val delreyConfigurationPath by lazy {
        val homeDirectory = System.getProperty("user.home")
        val path = Paths.get(homeDirectory, COMMON_CONFIG_DIRECTORY, DAEMON_CONFIG_DIRECTORY)
        Files.createDirectories(path)
    }

    private val configStore =
        try {
            initConfigStore()
        } catch (e: RuntimeException) {
            if (e.message?.contains("locked by") == true) {
                val lockFilePath = delreyConfigurationPath / "shoebox.lock"
                Files.delete(lockFilePath)

                initConfigStore()
            } else {
                throw e
            }
        }

    private val log = logging()

    private var defaultConfig
        get() = configStore[DEFAULT_CONFIG]
        set(value) {
            log.d {
                "Writing persistent config $value"
            }

            configStore[DEFAULT_CONFIG] = requireNotNull(value)
        }

    override fun sync(applicationConfig: ApplicationConfig): ApplicationConfig {
        val persistentConfig = defaultConfig

        log.d {
            "Syncing loaded config $applicationConfig with persistent config $persistentConfig"
        }

        val config = ApplicationConfig(
            applicationConfig.masterHttpBase
                ?: persistentConfig?.requiredMasterHttpBase,
            applicationConfig.masterWsBase
                ?: persistentConfig?.requiredMasterWsBase,
            applicationConfig.whoami
                ?: persistentConfig?.requiredWhoami
        )

        log.d {
            "Synced config: $config"
        }

        defaultConfig = config

        return config
    }

    private fun initConfigStore() =
        shoebox(delreyConfigurationPath, ApplicationConfig.serializer())

    private companion object {

        private const val COMMON_CONFIG_DIRECTORY = ".config"
        private const val DAEMON_CONFIG_DIRECTORY = "DelreyConfig"

        private const val DEFAULT_CONFIG = "default"
    }
}