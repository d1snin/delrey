package dev.d1s.delrey.daemon

import org.koin.core.component.KoinComponent
import org.lighthousegames.logging.logging

class DelreyDaemonApplication : KoinComponent {

    private val log = logging()

    fun run() {
        log.i {
            "Delrey Daemon is starting..."
        }


    }
}