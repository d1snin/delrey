/*
 * Copyright 2023 Mikhail Titov
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package dev.d1s.delrey.master.route

import dev.d1s.delrey.common.Host
import dev.d1s.delrey.common.Paths
import dev.d1s.delrey.common.PhysicalRunModification
import dev.d1s.delrey.master.service.HostService
import dev.d1s.delrey.master.service.RunService
import dev.d1s.delrey.master.util.requiredWhoamiQueryParameter
import dev.d1s.exkt.ktor.server.koin.configuration.Route
import io.ktor.server.routing.*
import io.ktor.server.websocket.*
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.koin.core.qualifier.named
import org.lighthousegames.logging.logging

class SessionRoute : Route, KoinComponent {

    override val qualifier = named("session-route")

    private val hostService by inject<HostService>()

    private val runService by inject<RunService>()

    private val log = logging()

    override fun Routing.apply() {
        webSocket(Paths.SESSION) {
            val hostAlias = call.requiredWhoamiQueryParameter

            log.d {
                "Handling client session. Who: $hostAlias"
            }

            val host = Host(hostAlias, session = this)

            hostService.addHost(host).getOrThrow()

            while (true) {
                try {
                    val modification = receiveDeserialized<PhysicalRunModification>()

                    log.d {
                        "Received run modification: $modification"
                    }

                    runService.updateRun(modification).getOrThrow()
                } catch (e: Throwable) {
                    log.w {
                        "Failed to receive physical modification. See you soon, $hostAlias."
                    }

                    hostService.removeHost(hostAlias).getOrThrow()

                    throw e
                }
            }
        }
    }
}