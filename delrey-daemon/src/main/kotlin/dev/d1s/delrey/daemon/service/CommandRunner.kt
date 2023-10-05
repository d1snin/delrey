package dev.d1s.delrey.daemon.service

import com.lordcodes.turtle.ProcessCallbacks
import com.lordcodes.turtle.ShellRunException
import com.lordcodes.turtle.shellRun
import dev.d1s.delrey.client.session.RunContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.core.component.KoinComponent
import org.lighthousegames.logging.logging

interface CommandRunner {

    fun run(context: RunContext)
}

class DefaultCommandRunner : CommandRunner, KoinComponent {

    private val commandScope = CoroutineScope(Dispatchers.IO)

    private val log = logging()

    override fun run(context: RunContext) {
        log.d {
            "Running ${context.run.command}"
        }

        commandScope.launch {
            try {
                runCommand(context)
            } catch (error: Throwable) {
                handleError(context, error)
            }
        }
    }

    private fun runCommand(context: RunContext) {
        val command = context.run.command

        shellRun {
            val callback = CommandProcessCallback(context, commandScope)
            val output = command(command.name, command.arguments, callback)

            log.d {
                "Handled process output: ${output.length} chars long"
            }

            commandScope.launch {
                try {
                    context.modify(output = output)
                } catch (_: Throwable) {
                    // ignore
                }
            }

            output
        }
    }

    private suspend fun handleError(context: RunContext, throwable: Throwable) {
        log.e(throwable) {
            "Command failed to run"
        }

        context.modify(error = throwable.message)

        when {
            throwable is ShellRunException -> {
                val status = throwable.exitCode

                context.modify(
                    output = throwable.errorText,
                    status = status
                )
            }

            else -> {
                throw throwable
            }
        }
    }

    private class CommandProcessCallback(
        private val context: RunContext,
        private val commandScope: CoroutineScope
    ) : ProcessCallbacks {

        private val log = logging()

        override fun onProcessStart(process: Process) {
            commandScope.launch {
                handlePid(process, context)
                handleStatus(process, context)
            }
        }

        private suspend fun handlePid(process: Process, context: RunContext) {
            val pid = try {
                process.pid()
            } catch (_: UnsupportedOperationException) {
                null
            }

            log.d {
                "Handled process pid: $pid"
            }

            context.modify(pid = pid)
        }

        private suspend fun handleStatus(process: Process, context: RunContext) {
            withContext(Dispatchers.IO) {
                process.waitFor()
            }

            val exitCode = try {
                process.exitValue()
            } catch (_: IllegalThreadStateException) {
                null
            }

            log.d {
                "Handled process status: $exitCode"
            }

            context.modify(status = exitCode)
        }
    }
}