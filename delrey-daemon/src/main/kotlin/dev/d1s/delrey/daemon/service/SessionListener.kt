package dev.d1s.delrey.daemon.service

import dev.d1s.delrey.client.DelreyMasterClient
import kotlinx.coroutines.*
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.lighthousegames.logging.logging
import kotlin.time.Duration.Companion.seconds

interface SessionListener {

    fun listen(): Job
}

class DefaultSessionListener : SessionListener, KoinComponent {

    private val client by inject<DelreyMasterClient>()

    private val commandRunner by inject<CommandRunner>()

    private val sessionScope = CoroutineScope(Dispatchers.IO)

    private val reconnectDuration = 10.seconds

    private val log = logging()

    override fun listen() =
        sessionScope.launch {
            log.i {
                "Communicating with Delrey Master..."
            }

            suspend fun tryOpenSession() {
                try {
                    openSession()
                } catch (e: Throwable) {
                    e.printStackTrace()

                    log.w {
                        "Error while communicating with Delrey Master. Reconnecting in $reconnectDuration"
                    }

                    delay(reconnectDuration)
                    tryOpenSession()
                }
            }

            tryOpenSession()
        }

    private suspend fun openSession() {
        client.session { context ->
            log.i {
                "Received run: ${context.run}"
            }

            commandRunner.run(context)
        }.getOrThrow()
    }
}