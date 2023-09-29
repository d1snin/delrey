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

import dev.d1s.delrey.common.Run
import dev.d1s.delrey.common.RunId
import io.ktor.util.collections.*
import org.koin.core.component.KoinComponent

interface RunRepository {

    fun add(run: Run)

    fun findById(id: RunId): Run?

    fun updateById(id: RunId, run: Run): Boolean

    fun removeById(id: RunId): Boolean
}

class DefaultRunRepository : RunRepository, KoinComponent {

    private val runs = ConcurrentSet<Run>()

    override fun add(run: Run) {
        runs += run
    }

    override fun findById(id: RunId) =
        runs.find {
            it.id == id
        }

    override fun updateById(id: RunId, run: Run): Boolean {
        val removed = removeById(id)

        if (!removed) {
            return false
        }

        add(run)

        return true
    }

    override fun removeById(id: RunId) =
        runs.removeIf {
            it.id == id
        }
}