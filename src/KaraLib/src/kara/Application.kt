package kara

import kara.internal.ResourceDescriptor
import kara.internal.scanPackageForResources
import kotlinx.reflection.urlDecode
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.io.File
import java.net.URLClassLoader
import java.nio.file.*
import java.nio.file.StandardWatchEventKinds.*
import java.nio.file.attribute.BasicFileAttributes
import java.util.*
import kotlin.reflect.KAnnotatedElement

/** The base Kara application class.
 */
open class Application(val config: ApplicationConfig, val appContext: String = "") {
    private var _context: ApplicationContext? = null
    private val watchKeys = ArrayList<WatchKey>()
    private val contextLock = Object()

    open val context: ApplicationContext
        get() = synchronized(contextLock) {
            if (config.isDevelopment()) {
                val changes = watchKeys.flatMap { it.pollEvents()!! }
                if (changes.isNotEmpty()) {
                    logger.info("Changes in application detected.")
                    var count = changes.size
                    while (true) {
                        Thread.sleep(200)
                        val moreChanges = watchKeys.flatMap { it.pollEvents()!! }
                        if (moreChanges.isEmpty())
                            break
                        logger.info("Waiting for more changes.")
                        count += moreChanges.size
                    }

                    logger.info("Changes to $count files caused ApplicationContext restart.")
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
            context
        }

    fun requestClassloader(): ClassLoader = classLoader(config, appContext)

    open fun createContext(): ApplicationContext {
        val classLoader = requestClassloader()
        val cache = hashMapOf<Pair<Int, String>, List<Class<*>>>()
        val start = System.currentTimeMillis()
        val resourceTypes = config.routePackages.flatMap {
            scanPackageForResources(it, classLoader, cache)
        }
        logger.info("Recources scan took " + (System.currentTimeMillis() - start))

        if (config.isDevelopment())
            watchUrls(resourceTypes)

        return ApplicationContext(config, config.routePackages, classLoader, cache, resourceTypes)
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

    fun watchUrls(resourceTypes: List<Pair<KAnnotatedElement, ResourceDescriptor>>) {
        val paths = HashSet<Path>()
        val visitor = object : SimpleFileVisitor<Path?>() {
            override fun preVisitDirectory(dir: Path?, attrs: BasicFileAttributes): FileVisitResult {
                paths.add(dir!!)
                return FileVisitResult.CONTINUE
            }
            override fun visitFile(file: Path?, attrs: BasicFileAttributes): FileVisitResult {
                val dir = file?.parent
                if (dir != null)
                    paths.add(dir)
                return FileVisitResult.CONTINUE
            }
        }
        val loaders = resourceTypes.map { it.first.javaClass.classLoader }.toSet()
        for (loader in loaders) {
            if (loader is URLClassLoader) {
                val loaderUrls = loader.urLs
                for (url in loaderUrls) {
                    logger.debug("Evaluating URL '$url' to watch for changes.")
                    url.path?.let {
                        val folder = File(urlDecode(it))
                        if (folder.exists()) {
                            Files.walkFileTree(folder.toPath(), visitor)
                        }
                    }
                }
            }
        }

        val watcher = FileSystems.getDefault()!!.newWatchService()
        paths.forEach {
            logger.debug("Watching $it for changes.")
        }
        watchKeys.addAll(paths.map { it.register(watcher, ENTRY_CREATE, ENTRY_DELETE, ENTRY_MODIFY)!! })
    }

    open fun start() {
        context // Init context eagerly
    }

    open fun shutDown() {
        destroyContext()
    }

    companion object {
        val logger: Logger = LoggerFactory.getLogger(Application::class.java)

        fun classLoader(config: ApplicationConfig, appContext: String): ClassLoader {
            val classPath = config.classPath(appContext)
            return when {
                classPath.isEmpty() -> config.appClassloader
                else -> URLClassLoader(classPath, config.appClassloader)
            }
        }

        fun load(config: ApplicationConfig, appContext: String): Application {
            val application = Application(config, appContext)
            application.start()
            return application
        }
    }
}
