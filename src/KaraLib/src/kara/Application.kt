package kara

import java.util.ArrayList
import java.net.URLClassLoader
import kara.internal.*

import java.io.File
import org.apache.log4j.Logger
import java.util.HashSet
import java.nio.file.*
import java.nio.file.StandardWatchEventKinds.*
import java.net.URLDecoder
import java.nio.file.attribute.BasicFileAttributes

/** The base Kara application class.
 */
open class Application(public val config: ApplicationConfig) {
    private var _context: ApplicationContext? = null
    private val watchKeys = ArrayList<WatchKey>()
    private val contextLock = Object()

    open val context: ApplicationContext
        get() = synchronized(contextLock) {
            if (config.isDevelopment()) {
                val changes = watchKeys.flatMap { it.pollEvents()!! }
                if (changes.size() > 0) {
                    logger.info("Changes in application detected.")
                    var count = changes.size()
                    while (true) {
                        Thread.sleep(200)
                        val moreChanges = watchKeys.flatMap { it.pollEvents()!! }
                        if (moreChanges.size() == 0)
                            break
                        logger.info("Waiting for more changes.")
                        count += moreChanges.size()
                    }

                    logger.info("Changes to ${count} files caused ApplicationContext restart.")
                    changes.take(5).forEach { logger.info("...  ${it.context()}") }
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

    public fun requestClassloader(): ClassLoader = classLoader(config)

    open fun createContext(): ApplicationContext {
        val classLoader = requestClassloader()
        val resourceTypes = config.routePackages.flatMap { scanPackageForResources(it, classLoader) }

        if (config.isDevelopment())
            watchUrls(resourceTypes)

        return ApplicationContext(this, config.routePackages, classLoader, resourceTypes)
    }

    open fun destroyContext() {
        try {
            _context?.dispose()
        } catch(e: Throwable) {
            logger.error("Failed to destroy application context", e)
        }
        watchKeys.forEach { it.cancel() }
        watchKeys.clear()
    }

    fun watchUrls(resourceTypes: List<Class<out Resource>>) {
        val paths = HashSet<Path>()
        val visitor = object : SimpleFileVisitor<Path?>() {
            override fun preVisitDirectory(dir: Path?, attrs: BasicFileAttributes): FileVisitResult {
                paths.add(dir!!)
                return FileVisitResult.CONTINUE
            }
            override fun visitFile(file: Path?, attrs: BasicFileAttributes): FileVisitResult {
                val dir = file?.getParent()
                if (dir != null)
                    paths.add(dir)
                return FileVisitResult.CONTINUE
            }
        }
        val loaders = resourceTypes.map { it.getClassLoader() }.toSet()
        for (loader in loaders) {
            if (loader is URLClassLoader) {
                val loaderUrls = loader.getURLs()
                for (url in loaderUrls) {
                    logger.debug("Evaluating URL '${url}' to watch for changes.")
                    url.getPath()?.let {
                        val folder = File(URLDecoder.decode(it, "utf-8"))
                        if (folder.exists()) {
                            Files.walkFileTree(folder.toPath(), visitor)
                        }
                    }
                }
            }
        }

        val watcher = FileSystems.getDefault()!!.newWatchService();
        paths.forEach {
            logger.debug("Watching ${it} for changes.")
        }
        watchKeys.addAll(paths.map { it.register(watcher, ENTRY_CREATE, ENTRY_DELETE, ENTRY_MODIFY)!! })
    }

    open fun start() {
        context // Init context eagerly
    }

    open fun shutDown() {
        destroyContext()
    }

    default object {
        val logger = Logger.getLogger(javaClass)!!

        fun classLoader(config: ApplicationConfig): ClassLoader {
            val rootClassloader = javaClass.getClassLoader()!!
            val classPath = config.classPath
            return when {
                classPath.isEmpty() -> rootClassloader
                else -> URLClassLoader(classPath, rootClassloader)
            }
        }

        public fun load(config: ApplicationConfig): Application {
            val application = Application(config)
            application.start()
            return application
        }
    }
}
