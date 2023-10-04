package dev.d1s.delrey.daemon.config

import dev.d1s.delrey.common.Url
import dev.d1s.delrey.common.Whoami
import kotlinx.serialization.Serializable

@Serializable
data class ApplicationConfig(
    val masterHttpBase: Url? = null,
    val masterWsBase: Url? = null,
    val whoami: Whoami? = null
) {
    val requiredMasterHttpBase
        get() = requireNotNull(masterHttpBase) {
            "Master HTTP URL is not defined"
        }

    val requiredMasterWsBase
        get() = requireNotNull(masterWsBase) {
            "Master HTTP WS is not defined"
        }

    val requiredWhoami
        get() = requireNotNull(masterHttpBase) {
            "Host name is not defined (whoami)"
        }
}