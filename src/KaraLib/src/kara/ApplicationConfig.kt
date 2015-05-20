package kara

import kara.config.Config
import kara.internal.Servlet
import kara.internal.logger
import java.io.File
import java.net.URL
import java.util.ArrayList
import kotlin.properties.Delegates

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

    private val _publicDirectories by Delegates.blockingLazy {
        readPublicDirProperty().map {
            val dirPath = it.trim()
            val dir = File(dirPath)
            val context = ActionContext.current().request?.getServletContext()
            if ((dir.getParent() == null || !dir.isDirectory()) && context != null) {
                logger.warn("Can't find public dir $dirPath. Trying to resolve it via servlet context.")
                return@map context.getRealPath(dirPath)?.let { path ->
                    if (File(path).isDirectory()) {
                        path
                    } else {
                        logger.warn("Resolved path is not directory $path")
                        null
                    }
                }
            }
            it
        }.filterNotNull().orEmpty()
    }
    /** Directories where publicly available files (like stylesheets, scripts, and images) will go. */
    public val publicDirectories: List<String>
        get() = ActionContext.tryGet()?.let { _publicDirectories } ?: readPublicDirProperty()

    private fun readPublicDirProperty() = tryGet("kara.publicDir")?.split(';')?.filter { it.isNotBlank() }.orEmpty()

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
