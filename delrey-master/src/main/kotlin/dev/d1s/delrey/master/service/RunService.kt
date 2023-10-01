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

package dev.d1s.delrey.master.service

import dev.d1s.delrey.common.PhysicalRunModification
import dev.d1s.delrey.common.Run
import dev.d1s.delrey.common.RunId
import dev.d1s.delrey.common.RunModification
import dev.d1s.delrey.master.repository.RunRepository
import io.ktor.server.plugins.*
import io.ktor.server.websocket.*
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.lighthousegames.logging.logging
import java.util.*

interface RunService {

    suspend fun launch(modification: RunModification): Result<Run>

    fun getRun(runId: RunId): Result<Run>

    fun updateRun(physicalModification: PhysicalRunModification): Result<Run>
}

class DefaultRunService : RunService, KoinComponent {

    private val runRepository by inject<RunRepository>()

    private val hostService by inject<HostService>()

    private val randomId get() = UUID.randomUUID().toString()

    private val log = logging()

    override suspend fun launch(modification: RunModification): Result<Run> =
        runCatching {
            log.d {
                "Launching run: $modification"
            }

            val (command, hostAlias) = modification
            val host = hostService.getHost(hostAlias).getOrThrow()


            val run = Run(
                id = randomId,
                command = command,
                host = hostAlias,
                pid = null,
                status = null,
                output = null
            )

            runRepository.add(run)

            val serverSession = (host.session as WebSocketServerSession)
            serverSession.sendSerialized(run)

            run
        }

    override fun getRun(runId: RunId): Result<Run> =
        runCatching {
            runRepository.findById(runId)
                ?: throw NotFoundException("Run not found by id '$runId'")
        }

    override fun updateRun(physicalModification: PhysicalRunModification): Result<Run> =
        runCatching {
            val runId = physicalModification.id

            log.d {
                "Updating run with id '$runId'"
            }

            val run = getRun(runId).getOrThrow()

            val hostAlias = run.host
            hostService.getHost(hostAlias).getOrThrow()

            val newRun = Run(
                runId,
                run.command,
                hostAlias,
                physicalModification.pid,
                physicalModification.status,
                physicalModification.output
            )

            runRepository.updateById(runId, newRun)

            newRun
        }
}