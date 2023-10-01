package dev.d1s.delrey.daemon.config

import dev.d1s.delrey.common.Url
import dev.d1s.delrey.common.Whoami

data class ApplicationConfig(
    val master: MasterConfig,
    val whoami: Whoami
)

data class MasterConfig(
    val httpBase: Url,
    val wsBase: Url
)