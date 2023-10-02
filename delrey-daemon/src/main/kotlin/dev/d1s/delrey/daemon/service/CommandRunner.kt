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
        val command = context.run.command

        log.d {
            "Running $command"
        }

        try {
            shellRun {
                val callback = CommandProcessCallback(context, commandScope)
                val output = command(command.name, command.arguments, callback)

                log.d {
                    "Handled process output: ${output.length} chars long"
                }

                commandScope.launch {
                    context.modify(output = output)
                }

                output
            }
        } catch (commandFailed: ShellRunException) {
            val status = commandFailed.exitCode

            log.w {
                "Command failed to run with exit code $status"
            }

            commandScope.launch {
                context.modify(
                    output = commandFailed.errorText,
                    status = status
                )
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