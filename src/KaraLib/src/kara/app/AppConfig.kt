package kara

import java.io.File
import java.util.HashMap
import kara.internal.*
import java.net.URL
import java.util.ArrayList
import java.net.URLClassLoader

/**
 * Store application configuration.
 */
public open class AppConfig(val environment : String = "development") : Config() {
    {
        this["kara.port"] = "8080"

        val configResolver: (jsonFile: String) -> URL? = {
            javaClass.getResource("/config/$it")
        }

        // read the main appconfig file and also look for an environment-specific one
        configResolver("appconfig.json")?.let {ConfigReader(this).read(it)}
        configResolver("appconfig.${environment}.json")?.let {ConfigReader(this).read(it)}
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

    public val appPackage : String
        get() = this["kara.appPackage"]

    public val appClass: String
        get() = if (contains("kara.appClass")) this["kara.appClass"] else "$appPackage.Application"

    /** Stores all middleware instances for the application. */
    public val middleware : MiddlewareList = MiddlewareList()

    /** The directory where publicly available files (like stylesheets, scripts, and images) will go. */
    public val publicDir : String
        get() = this["kara.publicDir"]

    public val routePackages: List<String>?
        get() {
            return if (contains("kara.routePackages")) {
                this["kara.routePackages"].split(',').toList() map {"${it.trim()}"}
            }
            else {
                null
            }
        }

    public val hotPackages: List<String>?
        get() {
            return if (contains("kara.hotPackages")) {
                this["kara.hotPackages"].split(',').toList() map {"${it.trim()}.*"}
            }
            else {
                null
            }
        }

    public val staticPackages: List<String>?
        get() {
            return if (contains("kara.staticPackages")) {
                this["kara.staticPackages"].split(',').toList() map {"${it.trim()}.*"}
            }
            else {
                null
            }
        }

    /** The port to run the server on. */
    public val port : String
        get() = this["kara.port"]


    public fun requestClassloader(current: ClassLoader): ClassLoader {
        if (isDevelopment()) {
            return URLClassLoader(buildClasspath(), current)
        }

        return current
    }

    public fun applicationClassloader(current: ClassLoader): ClassLoader {
        if (isDevelopment()) {
            val hot = hotPackages
            val static = staticPackages
            if (hot != null) {
                if (static == null)
                    return RestrictedClassLoader(hot, ArrayList<String>(), buildClasspath(), current)
                else
                    return RestrictedClassLoader(hot, static, buildClasspath(), current)
            }
        }

        return URLClassLoader(buildClasspath(), current)
    }

    protected open fun buildClasspath() : Array<URL> {
        val cl = javaClass.getClassLoader() as URLClassLoader
        return cl.getURLs()!!
    }
}
