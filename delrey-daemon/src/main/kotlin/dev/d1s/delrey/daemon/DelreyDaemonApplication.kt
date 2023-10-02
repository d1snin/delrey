package dev.d1s.delrey.daemon

import dev.d1s.delrey.daemon.service.SessionListener
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class DelreyDaemonApplication : KoinComponent {

    private val sessionListener by inject<SessionListener>()

    suspend fun run() {
        val job = sessionListener.listen()
        job.join()
    }
}