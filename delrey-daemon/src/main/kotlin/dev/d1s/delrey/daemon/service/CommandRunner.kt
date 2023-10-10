package dev.d1s.delrey.daemon.service

import dev.d1s.delrey.client.session.RunContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.core.component.KoinComponent
import org.lighthousegames.logging.logging
import java.io.BufferedReader
import java.io.SequenceInputStream
import java.util.concurrent.TimeUnit

interface CommandRunner {

    fun run(context: RunContext)
}

class DefaultCommandRunner : CommandRunner, KoinComponent {

    private val commandScope = CoroutineScope(Dispatchers.IO)

    private val processBuilder
        get() = ProcessBuilder()
            .redirectOutput(ProcessBuilder.Redirect.PIPE)
            .redirectError(ProcessBuilder.Redirect.PIPE)

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

    private suspend fun runCommand(context: RunContext) {
        val command = context.run.command
        val splitCommand = listOf(command.name) + command.arguments

        val process = withContext(Dispatchers.IO) {
            processBuilder.command(splitCommand).start()
        }

        process.handleProcess(context)
    }

    private suspend fun Process.handleProcess(context: RunContext) {
        handlePid(context)

        withContext(Dispatchers.IO) {
            waitFor(PROCESS_TIMEOUT, TimeUnit.HOURS)
        }

        handleStatus(context)

        handleOutput(context)
    }

    private suspend fun Process.handlePid(context: RunContext) {
        val pid = try {
            pid()
        } catch (_: UnsupportedOperationException) {
            null
        }

        log.d {
            "Handled process pid: $pid"
        }

        context.modify(pid = pid)
    }

    private suspend fun Process.handleStatus(context: RunContext) {
        val exitCode = try {
            exitValue()
        } catch (_: IllegalThreadStateException) {
            null
        }

        log.d {
            "Handled process status: $exitCode"
        }

        context.modify(status = exitCode)
    }

    private suspend fun Process.handleOutput(context: RunContext) {
        val outputText = SequenceInputStream(inputStream, errorStream)
            .bufferedReader()
            .use(BufferedReader::readText)
            .trim()

        log.d {
            "Handled process output: ${outputText.length} characters"
        }

        context.modify(output = outputText)
    }

    private suspend fun handleError(context: RunContext, throwable: Throwable) {
        log.e(throwable) {
            "Command failed to run"
        }

        context.modify(error = throwable.message)
    }

    private companion object {

        private const val PROCESS_TIMEOUT = 12L
    }
}