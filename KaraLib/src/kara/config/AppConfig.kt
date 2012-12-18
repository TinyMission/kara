package kara.config

import kara.controllers.ParamDeserializer
import java.io.File
import java.util.HashMap

/**
 * Store application configuration.
 */
public class AppConfig(appRoot : String, val environment : String = "development") : Config() {

    {
        this["kara.appRoot"] = appRoot
        val file = File(appRoot, "appconfig.json")
        if (file.exists())
            ConfigReader(this).read(file)
    }

    /** Returns true if the application is running in development mode.
    * In this case, the application code will be reloaded for each request.
    */
    fun isDevelopment() : Boolean {
        return environment == "development"
    }

    public val appRoot : String
        get() = this["kara.appRoot"]

    public val appPackage : String
        get() = this["kara.package"]

    public val paramDeserializer : ParamDeserializer = ParamDeserializer()

    /** The directory where publicly available files (like stylesheets, scripts, and images) will go. */
    public val publicDir : String
        get() = this["kara.publicDir"]

    /** The subdirectory of publicDir where stylesheets are stored. */
    public val stylesheetDir : String
        get() = this["kara.stylesheetDir"]

    /** The directory where sessions are stored are stored. */
    public val sessionDir : String
        get() = this["kara.sessionDir"]

    public val absSessionDir : String
        get() = File(appRoot, sessionDir).toString()
}