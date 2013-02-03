package kara.config

import kara.controllers.ParamDeserializer
import java.io.File
import java.util.HashMap
import kara.middleware.MiddlewareList

/**
 * Store application configuration.
 */
public class AppConfig(appRoot : String, val environment : String = "development") : Config() {

    {
        this["kara.appRoot"] = appRoot
        this["kara.port"] = "8080"

        // read the main appconfig file and also look for an environment-specific one
        var file = File(appRoot, "config/appconfig.json")
        if (file.exists())
            ConfigReader(this).read(file)
        file = File(appRoot, "config/appconfig.${environment}.json")
        if (file.exists())
            ConfigReader(this).read(file)
    }

    /** Returns true if the application is running in the development environment. */
    fun isDevelopment() : Boolean {
        return environment == "development"
    }

    /** Returns true if the application is running in the test environment. */
    fun isTest() : Boolean {
        return environment == "test"
    }

    /** Returns true if the application is running in the production environment. */
    fun isProduction() : Boolean {
        return environment == "production"
    }

    public val appRoot : String
        get() = this["kara.appRoot"]

    public val appPackage : String
        get() = this["kara.appPackage"]

    public val appPackagePath : String
        get() = this.appPackage.replace(".", "/")

    /** Deserializes parameters into objects. */
    public val paramDeserializer : ParamDeserializer = ParamDeserializer()

    /** Stores all middleware instances for the application. */
    public val middleware : MiddlewareList = MiddlewareList()

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
        //Is you see error here, click "Setup Kotlin JDK Annotations"
        get() = File(appRoot, sessionDir).toString()


    /** The port to run the server on. */
    public val port : String
        get() = this["kara.port"]

}
