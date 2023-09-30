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

import dev.d1s.delrey.common.Host
import dev.d1s.delrey.common.HostAlias
import dev.d1s.delrey.common.Status
import dev.d1s.delrey.common.VERSION
import dev.d1s.delrey.common.validation.validateHost
import dev.d1s.delrey.master.repository.HostRepository
import dev.d1s.exkt.ktor.server.statuspages.HttpStatusException
import io.ktor.http.*
import io.ktor.server.plugins.*
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.lighthousegames.logging.logging

interface HostService {

    fun addHost(host: Host): Result<Unit>

    fun getHost(alias: HostAlias): Result<Host>

    fun getStatus(): Result<Status>

    fun removeHost(alias: HostAlias): Result<Host>
}

class DefaultHostService : HostService, KoinComponent {

    private val hostRepository by inject<HostRepository>()

    private val log = logging()

    override fun addHost(host: Host): Result<Unit> =
        runCatching {
            val alias = host.alias

            log.d {
                "Registering host aliased as '$alias'..."
            }

            validateHost(host)

            val existingHost = hostRepository.findByAlias(alias)
            existingHost?.let {
                throw HttpStatusException(
                    HttpStatusCode.Conflict,
                    "Host with the same alias already registered"
                )
            }

            hostRepository.add(host)
        }

    override fun getHost(alias: HostAlias): Result<Host> =
        runCatching {
            hostRepository.findByAlias(alias)
                ?: throw NotFoundException("Host aliased as '$alias' is not registered")
        }


    override fun getStatus(): Result<Status> =
        runCatching {
            val hostAliases = hostRepository.findAll().map {
                it.alias
            }

            Status(
                VERSION,
                Status.State.UP,
                hostAliases
            )
        }

    override fun removeHost(alias: HostAlias): Result<Host> =
        runCatching {
            log.d {
                "Trying to remove host aliased as '$alias'..."
            }

            val host = getHost(alias).getOrThrow()

            hostRepository.removeByAlias(host.alias)

            host
        }
}