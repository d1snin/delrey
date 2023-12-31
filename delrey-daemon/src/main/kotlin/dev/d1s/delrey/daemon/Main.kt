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

package dev.d1s.delrey.daemon

import dev.d1s.delrey.daemon.di.setupDi
import kotlinx.coroutines.runBlocking

private lateinit var arguments: Array<String>
val MainArgs get() = arguments

fun main(args: Array<String>) {
    arguments = args

    setupDi()

    runBlocking {
        DelreyDaemonApplication().run()
    }
}