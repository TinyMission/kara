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

    private fun buildClasspath() : Array<URL> {
        val answer = ArrayList<URL>()
        answer.add(File(appRoot, "bin").toURI().toURL())
        appendJars(File(appRoot, "lib"), answer)
        return Array<URL>(answer.size) {answer.get(it)}
    }

    private fun appendJars(dir: File, answer : MutableList<URL>) {
        dir.listFiles()?.forEach { file ->
            val name = file.getName()
            when {
                name == "src" || name == "sources" -> {}
                name.endsWith("-src.jar") || name.endsWith("-sources.jar") -> {}
                file.isDirectory() -> appendJars(file, answer)
                name.endsWith(".jar") -> {
                    answer.add(file.toURI().toURL())
                }
                else -> {}
            }
        }
    }
}
