package dev.d1s.delrey.daemon

import dev.d1s.delrey.client.DelreyMasterClient
import dev.d1s.delrey.common.VERSION
import dev.d1s.delrey.daemon.service.SessionListener
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.lighthousegames.logging.logging

class DelreyDaemonApplication : KoinComponent {

    private val sessionListener by inject<SessionListener>()

    private val client by inject<DelreyMasterClient>()

    private val log = logging()

    suspend fun run() {
        log.i {
            "Delrey Daemon v$VERSION is starting..."
        }

        checkCompatibility()

        val job = sessionListener.listen()
        job.join()
    }

    private suspend fun checkCompatibility() {
        val status = client.getStatus().getOrThrow()

        val masterVersion = status.version

        log.i {
            "Delrey Master Server version: $masterVersion; State: ${status.state}"
        }

        if (VERSION != status.version) {
            error("This Delrey Daemon binary is not compatible with Delrey Master server")
        }
    }
}