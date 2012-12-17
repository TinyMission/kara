package kara.server

import java.util.ArrayList
import java.nio.file.*
import java.nio.file.StandardWatchEventKinds.*
import org.apache.log4j.Logger


/** Interface for objects that will get told when a watched file has been modified.
 */
trait FileWatchListener {
    fun onFileWatch(dir : String, fileName : String)
}


/** A runnable that watches a certain file for changes and reports them to a set of listeners.
 */
class FileWatcher(val fullPath : String, val fileName : String) : Runnable {

    val logger = Logger.getLogger(this.javaClass)!!

    val listeners : MutableList<FileWatchListener> = ArrayList<FileWatchListener>()

    /** listener will now receive a notification any time the wacthed file changes.
     */
    public fun addListener(listener : FileWatchListener) {
        listeners.add(listener)
    }

    /** Removes the given listener from this watcher's list of listeners.
     */
    public fun removeListener(listener : FileWatchListener) {
        listeners.remove(listener)
    }

    public override fun run() {
        val watcher = FileSystems.getDefault()?.newWatchService() as WatchService
        val path = FileSystems.getDefault()?.getPath(fullPath) as Path
        path.register(watcher, ENTRY_CREATE, ENTRY_MODIFY)
        logger.info("monitoring ${path} for changes to ${fileName} ...");

        while (true) {
            val key = watcher.take()
            if (key != null) {
                val watchKey = key as WatchKey
                for (var event in watchKey.pollEvents()!!) {
                    if (event?.context().toString().equalsIgnoreCase(fileName)) {
                        for (val listener in listeners) {
                            listener.onFileWatch(fullPath, fileName)
                        }
                    }
                }
                watchKey.reset()
            }
        }
    }

}
