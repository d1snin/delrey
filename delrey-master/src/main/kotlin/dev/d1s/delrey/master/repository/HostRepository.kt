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

package dev.d1s.delrey.master.repository

import dev.d1s.delrey.common.Host
import dev.d1s.delrey.common.HostAlias
import dev.d1s.delrey.common.Hosts
import io.ktor.util.collections.*
import org.koin.core.component.KoinComponent

interface HostRepository {

    fun add(host: Host)

    fun findByAlias(alias: HostAlias): Host?

    fun findAll(): Hosts

    fun removeByAlias(alias: HostAlias): Boolean
}

class DefaultHostRepository : HostRepository, KoinComponent {

    private val hosts = ConcurrentSet<Host>()

    override fun add(host: Host) {
        hosts += host
    }

    override fun findByAlias(alias: HostAlias) =
        hosts.find {
            it.alias == alias
        }

    override fun findAll(): Hosts = hosts.toList()

    override fun removeByAlias(alias: HostAlias) =
        hosts.removeIf {
            it.alias == alias
        }
}