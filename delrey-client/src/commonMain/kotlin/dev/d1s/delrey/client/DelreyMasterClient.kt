package dev.d1s.delrey.client

import dev.d1s.delrey.client.session.RunContext
import dev.d1s.delrey.common.*
import dev.d1s.delrey.common.Url
import dev.d1s.exkt.common.replaceIdPlaceholder
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.websocket.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json

public interface DelreyMasterClient {

    public suspend fun getStatus(): Result<Status>

    public suspend fun postRun(physicalRunModification: PhysicalRunModification): Result<Run>

    public suspend fun getRun(id: RunId): Result<Run>

    public suspend fun session(block: suspend RunContext.() -> Unit): Result<Unit>
}

internal class DefaultDelreyMasterClient(
    private val httpBase: Url,
    private val wsBase: Url,
    private val token: String?
) : DelreyMasterClient {

    private val httpClient = HttpClient {
        install(ContentNegotiation) {
            json()
        }

        install(WebSockets) {
            contentConverter = KotlinxWebsocketSerializationConverter(Json)
        }

        defaultRequest {
            url(httpBase)

            token?.let {
                bearerAuth(it)
            }
        }

        expectSuccess = true
    }

    override suspend fun getStatus(): Result<Status> =
        runCatching {
            httpClient.get(Paths.GET_STATUS).body()
        }

    override suspend fun postRun(physicalRunModification: PhysicalRunModification): Result<Run> =
        runCatching {
            requireToken()

            httpClient.post(Paths.POST_RUN) {
                contentType(ContentType.Application.Json)
                setBody(physicalRunModification)
            }.body()
        }

    override suspend fun getRun(id: RunId): Result<Run> =
        runCatching {
            requireToken()

            val path = Paths.GET_RUN.replaceIdPlaceholder(id)
            httpClient.get(path).body()
        }

    override suspend fun session(block: suspend RunContext.() -> Unit): Result<Unit> =
        runCatching {
            val url = URLBuilder(wsBase).apply {
                path(Paths.SESSION)
            }.buildString()


            httpClient.webSocket(url) {
                while (true) {
                    val run = receiveDeserialized<Run>()
                    val context = RunContext(run, session = this)

                    block.invoke(context)
                }
            }
        }

    private fun requireToken() =
        requireNotNull(token) {
            "Delrey Master token is required for this interaction"
        }
}

public fun masterClient(httpBase: Url, wsBase: Url, token: String? = null): DelreyMasterClient =
    DefaultDelreyMasterClient(httpBase, wsBase, token)