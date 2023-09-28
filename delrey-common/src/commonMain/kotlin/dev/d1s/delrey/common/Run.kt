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
public typealias ExitCode = Int
public typealias Output = String

public interface AbstractRun

@Serializable
public data class Run(
    val id: RunId,
    val command: Command,
    val pid: Pid?,
    val status: ExitCode?,
    val output: Output?
) : AbstractRun

@Serializable
public data class ModifiedRun(
    val command: Command
) : AbstractRun

@Serializable
public data class AcceptedRun(
    val id: RunId
) : AbstractRun