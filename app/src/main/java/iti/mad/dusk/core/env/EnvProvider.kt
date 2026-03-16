package iti.mad.dusk.core.env

import io.github.cdimascio.dotenv.Dotenv
import io.github.cdimascio.dotenv.dotenv
import javax.inject.Inject

class EnvProvider @Inject constructor() {

    private val dotenv: Dotenv = dotenv {
        this.directory = "/assets"
        this.filename = "env"

    }

    val owmApiKey: String
        get() = dotenv["OWM_API_KEY"]
            ?: throw IllegalStateException(
                "OWM_API_KEY not found in assets/env. " +
                        "Please add: OWM_API_KEY=your_key_here"
            )

    val clarityId: String
        get() = dotenv["CLARITY_ID"]
            ?: throw IllegalStateException(
                "CLARITY_ID not found in assets/env. " +
                        "Please add: CLARITY_ID=your_id_here"
            )

    val mabBoxToken: String
        get() = dotenv["MAPBOX_TOKEN"]
            ?: throw IllegalStateException(
                "CLARITY_ID not found in assets/env. " +
                        "Please add: CLARITY_ID=your_id_here"
            )

    init {
        validateRequiredKeys()
    }

    private fun validateRequiredKeys() {
        val requiredKeys = listOf("OWM_API_KEY", "CLARITY_ID", "MAPBOX_TOKEN")
        val missingKeys = requiredKeys.filter { dotenv[it] == null }

        if (missingKeys.isNotEmpty()) {
            throw IllegalStateException(
                "Missing required environment variables in assets/env: " +
                        "${missingKeys.joinToString(", ")}. " +
                        "Please create the file and add all required keys."
            )
        }
    }
}