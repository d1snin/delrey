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

import dev.d1s.delrey.common.*
import dev.d1s.delrey.master.repository.RunRepository
import io.ktor.server.plugins.*
import io.ktor.server.websocket.*
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.lighthousegames.logging.logging
import java.util.*

interface RunService {

    suspend fun run(modification: RunModification, wait: Boolean): Result<Run>

    suspend fun launch(modification: RunModification): Result<Run>

    suspend fun execute(modification: RunModification): Result<Run>

    fun getRun(runId: RunId): Result<Run>

    fun getRuns(hostAlias: HostAlias): Result<Runs>

    fun updateRun(physicalModification: PhysicalRunModification): Result<Run>
}

class DefaultRunService : RunService, KoinComponent {

    private val runRepository by inject<RunRepository>()

    private val hostService by inject<HostService>()

    private val randomId get() = UUID.randomUUID().toString()

    private val log = logging()
    override suspend fun run(modification: RunModification, wait: Boolean): Result<Run> =
        if (wait) {
            execute(modification)
        } else {
            launch(modification)
        }

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
                output = null,
                status = null,
                error = null,
                finished = false
            )

            runRepository.add(run)

            val serverSession = (host.session as WebSocketServerSession)
            serverSession.sendSerialized(run)

            run
        }

    override suspend fun execute(modification: RunModification): Result<Run> =
        runCatching {
            var run = launch(modification).getOrThrow()
            val runId = run.id

            do {
                run = getRun(runId).getOrThrow()
            } while (!run.finished)

            run
        }

    override fun getRun(runId: RunId): Result<Run> =
        runCatching {
            runRepository.findById(runId)
                ?: throw NotFoundException("Run not found by id '$runId'")
        }

    override fun getRuns(hostAlias: HostAlias): Result<Runs> =
        runCatching {
            val host = hostService.getHost(hostAlias).getOrThrow()

            runRepository.findAllByHost(host.alias)
        }

    override fun updateRun(physicalModification: PhysicalRunModification): Result<Run> =
        runCatching {
            val runId = physicalModification.id

            log.d {
                "Updating run with id '$runId'. Physical modification: $physicalModification"
            }

            val run = getRun(runId).getOrThrow()

            val hostAlias = run.host
            hostService.getHost(hostAlias).getOrThrow()

            val newRun = Run(
                runId,
                run.command,
                hostAlias,
                physicalModification.pid,
                physicalModification.output,
                physicalModification.status,
                physicalModification.error,
                physicalModification.finished
            )

            runRepository.updateById(runId, newRun)

            newRun
        }
}