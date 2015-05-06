package kara

import java.io.File
import java.net.URL
import java.util.ArrayList
import kara.config.Config

/**
 * Store application configuration.
 */
public open class ApplicationConfig() : Config() {

    public companion object {
        public fun loadFrom(configPath: String): ApplicationConfig {
            val config = ApplicationConfig()
            Config.readConfig(config, configPath, javaClass.getClassLoader()!!)
            return config
        }
    }

    /** Returns true if the application is running in the development environment. */
    public fun isDevelopment(): Boolean = get("kara.environment") == "development"

    /** Returns true if the application is running in the test environment. */
    public fun isTest(): Boolean = get("kara.environment") == "test"

    /** Returns true if the application is running in the production environment. */
    public fun isProduction(): Boolean = get("kara.environment") == "production"

    public val applicationPackageName: String
        get() = this["kara.appPackage"]

    /** Directories where publicly available files (like stylesheets, scripts, and images) will go. */
    public val publicDirectories: List<String>
        get() = tryGet("kara.publicDir")?.split(';') ?: emptyList<String>()

    public val routePackages: List<String>
        get() = tryGet("kara.routePackages")?.split(',')?.toList()?.map { "${it.trim()}" }
                ?: listOf("${applicationPackageName}.routes", "${applicationPackageName}.styles")


    /** The port to run the server on. */
    public val port: String
        get() = tryGet("kara.port") ?: "8080"

    public open val classPath: Array<URL>
        get() {
            val urls = ArrayList<URL>()
            tryGet("kara.classpath")?.let {
                urls.addAll(it.split(':')
                        .flatMap {
                            when {
                                it.endsWith("/**") -> {
                                    val answer = ArrayList<File>()
                                    File(it.removeSuffix("/**")).recurse { file -> if (file.isFile() && file.getName().endsWith(".jar"))  answer.add(file) }
                                    answer
                                }

                                it.endsWith("/*") -> {
                                    File(it.removeSuffix("/*")).listFiles { it.isFile() && it.getName().endsWith(".jar") }?.toList() ?: listOf()
                                }

                                else -> {
                                    listOf(File(it))
                                }
                            }
                        }
                        .map { it.toURI().toURL() })
            }
            return urls.toTypedArray()
        }
}
