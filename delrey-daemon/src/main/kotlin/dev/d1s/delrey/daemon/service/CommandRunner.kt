package dev.d1s.delrey.daemon.service

import dev.d1s.delrey.client.session.RunContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.lighthousegames.logging.logging
import java.io.BufferedReader
import java.io.SequenceInputStream
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

interface CommandRunner {

    fun run(context: RunContext)
}

class DefaultCommandRunner : CommandRunner, KoinComponent {

    private val executor = Executors.newCachedThreadPool()

    private val clientScope = CoroutineScope(Dispatchers.IO)

    private val log = logging()

    override fun run(context: RunContext) {
        log.d {
            "Running ${context.run.command}"
        }

        executor.execute {
            try {
                runCommand(context)
            } catch (error: Throwable) {
                handleError(context, error)
            }

            context.launchModification {
                modify(finished = true)
            }
        }
    }

    private fun runCommand(context: RunContext) {
        val command = context.run.command

        val process = Runtime.getRuntime().exec(command)

        process.handleProcess(context)
    }

    private fun Process.handleProcess(context: RunContext) {
        handlePid(context)

        waitFor(PROCESS_TIMEOUT, TimeUnit.HOURS)

        handleStatus(context)

        handleOutput(context)
    }

    private fun Process.handlePid(context: RunContext) {
        val pid = try {
            pid()
        } catch (_: UnsupportedOperationException) {
            null
        }

        log.d {
            "Handled process pid: $pid"
        }

        context.launchModification {
            modify(pid = pid)
        }
    }

    private fun Process.handleStatus(context: RunContext) {
        val exitCode = try {
            exitValue()
        } catch (_: IllegalThreadStateException) {
            null
        }

        log.d {
            "Handled process status: $exitCode"
        }

        context.launchModification {
            modify(status = exitCode)
        }
    }

    private fun Process.handleOutput(context: RunContext) {
        val outputText = SequenceInputStream(inputStream, errorStream)
            .bufferedReader()
            .use(BufferedReader::readText)
            .trim()

        log.d {
            "Handled process output: ${outputText.length} characters"
        }

        context.launchModification {
            modify(output = outputText)
        }
    }

    private fun handleError(context: RunContext, throwable: Throwable) {
        log.e(throwable) {
            "Command failed to run"
        }

        context.launchModification {
            modify(error = throwable.message)
        }
    }

    private fun RunContext.launchModification(block: suspend RunContext.() -> Unit) {
        clientScope.launch {
            block()
        }
    }

    private companion object {

        private const val PROCESS_TIMEOUT = 12L
    }
}