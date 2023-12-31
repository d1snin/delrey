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

package dev.d1s.delrey.master.util

import dev.d1s.delrey.common.HostAlias
import dev.d1s.delrey.common.Paths
import dev.d1s.delrey.common.Whoami
import io.ktor.server.application.*
import io.ktor.server.plugins.*

val ApplicationCall.requiredIdParameter: String
    get() = requiredParameter(Paths.ID_PARAMETER)

val ApplicationCall.requiredWhoamiQueryParameter: Whoami
    get() = requiredQueryParameter(Paths.WHOAMI_QUERY_PARAMETER)

val ApplicationCall.waitQueryParameter: Boolean
    get() = request.queryParameters[Paths.WAIT_QUERY_PARAMETER]?.toBooleanStrictOrNull() ?: false

val ApplicationCall.requiredHostQueryParameter: HostAlias
    get() = requiredQueryParameter(Paths.HOST_QUERY_PARAMETER)

private fun ApplicationCall.requiredParameter(parameter: String) =
    requiredValue(parameters[parameter], message = "Parameter '$parameter' not found")

private fun ApplicationCall.requiredQueryParameter(parameter: String) =
    requiredValue(request.queryParameters[parameter], message = "Query parameter '$parameter' not found")

private fun requiredValue(value: String?, message: String) =
    value ?: throw BadRequestException(message)