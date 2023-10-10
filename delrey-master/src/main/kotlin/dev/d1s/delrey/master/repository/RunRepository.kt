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

import dev.d1s.delrey.common.HostAlias
import dev.d1s.delrey.common.Run
import dev.d1s.delrey.common.RunId
import dev.d1s.delrey.common.Runs
import io.github.reactivecircus.cache4k.Cache
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

interface RunRepository {

    fun add(run: Run)

    fun findById(id: RunId): Run?

    fun findAllByHost(alias: HostAlias): Runs

    fun updateById(id: RunId, run: Run)

    fun removeById(id: RunId)
}

class DefaultRunRepository : RunRepository, KoinComponent {

    private val runCache by inject<Cache<RunId, Run>>()

    override fun add(run: Run) {
        runCache.put(run.id, run)
    }

    override fun findById(id: RunId) =
        runCache.get(id)

    override fun findAllByHost(alias: HostAlias): Runs =
        runCache.asMap().values.filter {
            it.host == alias
        }

    override fun updateById(id: RunId, run: Run) {
        runCache.put(id, run)
    }

    override fun removeById(id: RunId) {
        runCache.invalidate(id)
    }
}