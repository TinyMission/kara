package kara

import java.io.File
import java.util.HashMap
import kara.internal.*
import java.net.URL
import java.util.ArrayList
import java.net.URLClassLoader
import kara.config.Config

/**
 * Store application configuration.
 */
public open class ApplicationConfig(configPath: String) : Config() {
    {
        Config.readConfig(this, configPath, javaClass.getClassLoader()!!)
    }

    /** Returns true if the application is running in the development environment. */
    fun isDevelopment(): Boolean = get("kara.environment") == "development"

    /** Returns true if the application is running in the test environment. */
    fun isTest(): Boolean = get("kara.environment") == "test"

    /** Returns true if the application is running in the production environment. */
    fun isProduction(): Boolean = get("kara.environment") == "production"

    public val applicationPackageName: String
        get() = this["kara.appPackage"]

    public val applicationClassName: String
        get() = tryGet("kara.appClass") ?: "$applicationPackageName.Application"

    /** Directories where publicly available files (like stylesheets, scripts, and images) will go. */
    public val publicDirectories: Array<String>
        get() = tryGet("kara.publicDir")?.split(';') ?: array<String>()

    public val routePackages: List<String>
        get() = tryGet("kara.routePackages")?.split(',')?.toList()?.map { "${it.trim()}" }
        ?: listOf("${applicationPackageName}.routes", "${applicationPackageName}.styles")


    public val hotPackages: List<String>
        get() = tryGet("kara.hotPackages")?.split(',')?.toList()?.map { "${it.trim()}.*" }  ?: listOf<String>()

    public val staticPackages: List<String>
        get() = tryGet("kara.staticPackages")?.split(',')?.toList()?.map { "${it.trim()}.*" }  ?: listOf<String>()

    /** The port to run the server on. */
    public val port: String
        get() = tryGet("kara.port") ?: "8080"

    public open val classPath : Array<URL>
        get() {
            val urls = ArrayList<URL>()
            tryGet("kara.classpath")?.let {
                urls.addAll(it.split(':') map {File(it).toURL()})
            }
            return urls.copyToArray()
        }
}
