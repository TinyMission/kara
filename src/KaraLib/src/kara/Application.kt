package kara

import java.util.regex.Pattern
import java.util.ArrayList
import java.net.URLClassLoader
import kara.internal.*
import java.net.URL

import javax.servlet.*
import java.io.File
import org.apache.log4j.Logger
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse
import kotlin.properties.*
import java.util.HashSet
import java.nio.file.*
import java.nio.file.StandardWatchEventKinds.*
import java.net.URLDecoder
import java.nio.file.attribute.BasicFileAttributes

/** The base Kara application class.
 */
abstract class Application(val config: ApplicationConfig, private vararg val routes: Any) {
    val logger = Logger.getLogger(this.javaClass)!!
    private var _context: ApplicationContext? = null
    private val watchKeys = ArrayList<WatchKey>()
    private val contextLock = Object()

    val context: ApplicationContext
        get() = synchronized(contextLock) {
            if (config.isDevelopment()) {
                if (watchKeys.flatMap { it.pollEvents()!! }.size() > 0) {
                    destroyContext()
                    _context = null
                }
            }

            var context = _context
            if (context == null) {
                context = createContext()
                _context = context
            }
            context!!
        }

    public fun requestClassloader(current: ClassLoader): ClassLoader =
            if (config.isDevelopment()) {
                URLClassLoader(config.classPath, current)
            } else
                current

    open fun createContext(): ApplicationContext {
        val classLoader = requestClassloader(javaClass.getClassLoader()!!)
        val resourceTypes = if (routes.size != 0) {
            scanObjects(routes, classLoader)
        } else {
            config.routePackages.flatMap { scanPackageForResources(it, classLoader) }
        }
        if (config.isDevelopment())
            watchUrls(resourceTypes)
        return ApplicationContext(config.routePackages, classLoader, resourceTypes)
    }

    open fun destroyContext() {
        _context?.dispose()
        watchKeys.forEach { it.cancel() }
    }

    fun watchUrls(resourceTypes: List<Class<out Resource>>) {
        val paths = HashSet<Path>()
        for (loader in resourceTypes.map { it.getClassLoader() }.toSet()) {
            if (loader is URLClassLoader) {
                val loaderUrls = loader.getURLs()
                if (loaderUrls != null) {
                    for (url in loaderUrls) {
                        url.getPath()?.let {
                            val folder = File(URLDecoder.decode(it, "utf-8")).toPath()?.getParent()
                            val visitor = object : SimpleFileVisitor<Path?>() {
                                override fun preVisitDirectory(dir: Path?, attrs: BasicFileAttributes): FileVisitResult {
                                    paths.add(dir!!)
                                    return FileVisitResult.CONTINUE
                                }
                            }
                            Files.walkFileTree(folder, visitor)
                        }
                    }
                }
            }
        }

        val watcher = FileSystems.getDefault()!!.newWatchService();
        watchKeys.addAll(paths.map { it.register(watcher, ENTRY_CREATE, ENTRY_DELETE, ENTRY_MODIFY)!! })
    }

    open fun start() {
    }

    open fun shutDown() {
        destroyContext()
    }
}
