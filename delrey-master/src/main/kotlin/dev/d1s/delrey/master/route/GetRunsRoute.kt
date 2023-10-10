package dev.d1s.delrey.master.route

import dev.d1s.delrey.common.Paths
import dev.d1s.delrey.master.service.RunService
import dev.d1s.delrey.master.util.requiredHostQueryParameter
import dev.d1s.exkt.ktor.server.koin.configuration.Route
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.koin.core.qualifier.named

class GetRunsRoute : Route, KoinComponent {

    override val qualifier = named("get-runs-route")

    private val runService by inject<RunService>()

    override fun Routing.apply() {
        authenticate {
            get(Paths.GET_RUNS) {
                val hostname = call.requiredHostQueryParameter

                val foundRuns = runService.getRuns(hostname).getOrThrow()

                call.respond(foundRuns)
            }
        }
    }
}