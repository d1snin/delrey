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

package dev.d1s.delrey.common.validation

import dev.d1s.delrey.common.Command
import dev.d1s.delrey.common.RunModification
import dev.d1s.exkt.konform.isNotBlank
import io.konform.validation.Validation

public val validateRun: Validation<RunModification> = Validation {
    RunModification::command {
        run(validateCommand)
    }

    RunModification::host {
        run(validateHostAlias)
    }
}

private val validateCommand: Validation<Command> = Validation {
    Command::name {
        isNotBlank() hint "command name must not be blank"
    }

    Command::arguments onEach {
        isNotBlank() hint "command argument must not be blank"
    }
}