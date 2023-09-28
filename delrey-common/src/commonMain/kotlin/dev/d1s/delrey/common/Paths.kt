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

public object Paths {

    public const val ID_PARAMETER: String = "id"

    public const val GET_STATUS_ROUTE: String = "/status"

    public const val POST_RUN: String = "/runs"
    public const val GET_RUN: String = "/runs/{$ID_PARAMETER}"
    public const val DELETE_RUN: String = "/runs/{$ID_PARAMETER}"

    public const val SESSION: String = "/session"
}