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

package dev.d1s.delrey.common

import kotlinx.serialization.Serializable

public typealias RunId = String

public typealias Pid = Long
public typealias Output = String
public typealias ExitCode = Int
public typealias ErrorMessage = String

public interface AbstractRun {

    public val command: Command

    public val host: HostAlias
}

public interface PhysicalRun {

    public val pid: Pid?

    public val output: Output?

    public val status: ExitCode?

    public val error: ErrorMessage?

    public val finished: Boolean
}

@Serializable
public data class Run(
    val id: RunId,
    override val command: Command,
    override val host: HostAlias,
    override val pid: Pid?,
    override val output: Output?,
    override val status: ExitCode?,
    override val error: ErrorMessage?,
    override val finished: Boolean
) : AbstractRun, PhysicalRun

@Serializable
public data class RunModification(
    override val command: Command,
    override val host: HostAlias
) : AbstractRun

@Serializable
public data class PhysicalRunModification(
    val id: RunId,
    override val pid: Pid?,
    override val output: Output?,
    override val status: ExitCode?,
    override val error: ErrorMessage?,
    override val finished: Boolean
) : PhysicalRun