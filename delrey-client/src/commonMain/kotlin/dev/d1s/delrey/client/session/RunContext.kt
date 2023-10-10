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
        status = null,
        error = null,
        finished = false
    )

    public suspend fun modify(
        pid: Pid? = modification.pid,
        output: Output? = modification.output,
        status: ExitCode? = modification.status,
        error: ErrorMessage? = modification.error,
        finished: Boolean = modification.finished
    ) {
        // Ты всегда находишься как-будто не здесь.
        // Когда я пытаюсь поговорить с тобой о чем-то серьезном, ты
        // никогда не сможешь сосредоточиться этом. Ты постоянно уходишь в какой
        // то свой мир, в то, во что тебе хочется уйти. В такие моменты я чувствую себя
        // будто совсем ненужным...
        // Все, что ты отвечаешь на любые мои попытки совместного рассуждения - "Я не знаю, что тебе на это ответить".
        // Может быть, я слишком сложен?

        val physicalRunModification = modification.copy(
            pid = pid,
            output = output,
            status = status,
            error = error,
            finished = finished
        )

        modification = physicalRunModification

        session.sendSerialized(physicalRunModification)
    }
}