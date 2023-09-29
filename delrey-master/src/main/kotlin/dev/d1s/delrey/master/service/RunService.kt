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
import org.koin.core.component.KoinComponent

interface RunService {

    fun launch(modification: RunModification): Run

    fun getRun(runId: RunId): Run?

    fun updateRun(physicalModification: PhysicalRunModification): Run?

    fun cancel(runId: RunId)
}

class DefaultRunService : RunService, KoinComponent {

    override fun launch(modification: RunModification): Run {
        TODO("Not yet implemented")
    }

    override fun getRun(runId: RunId): Run? {
        TODO("Not yet implemented")
    }

    override fun updateRun(physicalModification: PhysicalRunModification): Run? {
        TODO("Not yet implemented")
    }

    override fun cancel(runId: RunId) {
        TODO("Not yet implemented")
    }
}