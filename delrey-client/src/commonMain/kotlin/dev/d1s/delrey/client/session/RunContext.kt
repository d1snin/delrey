package dev.d1s.delrey.client.session

import dev.d1s.delrey.common.*
import io.ktor.client.plugins.websocket.*

public class RunContext(
    public val run: Run,
    private val session: DefaultClientWebSocketSession
) {
    private var modification = PhysicalRunModification(
        id = run.id,
        pid = null,
        output = null,
        status = null
    )

    public suspend fun modify(
        pid: Pid? = modification.pid,
        output: Output? = modification.output,
        status: ExitCode? = modification.status
    ) {
        val physicalRunModification = modification.copy(
            pid = pid,
            output = output,
            status = status
        )

        modification = physicalRunModification

        session.sendSerialized(physicalRunModification)
    }
}