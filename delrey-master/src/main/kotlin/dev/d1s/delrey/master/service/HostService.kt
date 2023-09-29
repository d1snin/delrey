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
import dev.d1s.delrey.common.validation.validateHostAlias
import dev.d1s.delrey.master.repository.HostRepository
import dev.d1s.exkt.ktor.server.statuspages.HttpStatusException
import io.ktor.http.*
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

interface HostService {

    fun addHost(host: Host)

    fun getHost(alias: HostAlias): Host?

    fun getStatus(): Status

    fun removeHost(alias: HostAlias): Boolean
}

class DefaultHostService : HostService, KoinComponent {

    private val hostRepository by inject<HostRepository>()

    override fun addHost(host: Host) {
        val alias = host.alias

        validateHostAlias(alias)

        val existingHost = hostRepository.findByAlias(alias)
        existingHost?.let {
            throw HttpStatusException(
                HttpStatusCode.Conflict,
                "Host with the same alias already registered"
            )
        }

        hostRepository.add(host)
    }

    override fun getHost(alias: HostAlias) =
        hostRepository.findByAlias(alias)

    override fun getStatus(): Status {
        val hostAliases = hostRepository.findAll().map {
            it.alias
        }

        return Status(
            VERSION,
            Status.State.UP,
            hostAliases
        )
    }

    override fun removeHost(alias: HostAlias) =
        hostRepository.removeByAlias(alias)
}