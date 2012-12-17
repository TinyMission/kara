package kara.config

import kara.controllers.ParamDeserializer
import java.io.File

/** Store application configuration.
 */
public class AppConfig(val appRoot : String, val environment : String = "development") {

    class object {
        public var current : AppConfig = AppConfig(System.getProperty("user.dir") as String, "development")
    }

    /** Returns true if the application is running in development mode.
    * In this case, the application code will be reloaded for each request.
    */
    fun isDevelopment() : Boolean {
        return environment == "development"
    }

    public val paramDeserializer : ParamDeserializer = ParamDeserializer()

    /** The directory where publicly available files (like stylesheets, scripts, and images) will go. */
    public var publicDir : String = "public"

    /** The subdirectory of publicDir where stylesheets are stored. */
    public var stylesheetDir : String = "stylesheets"

    /** The directory where sessions are stored are stored. */
    public var sessionDir : String = "tmp/sessions"

    public val absSessionDir : String
        get() = File(appRoot, sessionDir).toString()
}