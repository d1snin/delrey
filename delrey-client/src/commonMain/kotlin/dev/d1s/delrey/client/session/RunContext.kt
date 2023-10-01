package dev.d1s.delrey.client.session

import dev.d1s.delrey.common.*
import io.ktor.client.plugins.websocket.*

public class RunContext(
    public val run: Run,
    private val session: DefaultClientWebSocketSession
) {
    public suspend fun modify(
        pid: Pid?,
        status: ExitCode?,
        output: Output?
    ) {
        val physicalRunModification = PhysicalRunModification(
            id = run.id,
            pid,
            status,
            output
        )

        session.sendSerialized(physicalRunModification)
    }
}