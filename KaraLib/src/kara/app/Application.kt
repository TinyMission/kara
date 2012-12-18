package kara.app

import kara.controllers.Dispatcher
import kara.config.AppConfig

/** The base Kara application class.
 * Each Kara app should extend this class and provide custom initialization.
 */
abstract class Application() {

    public val dispatcher : Dispatcher = Dispatcher()

    /** Subclasses should override this to provide application initialization.
     */
    abstract fun init(config : AppConfig)

}
