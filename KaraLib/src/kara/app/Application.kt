package kara.app

import kara.controllers.Dispatcher
import kara.config.AppConfig

/** The base Kara application class.
 * Each Kara app should extend this class and call run() in their main function.
 */
abstract class Application() {

    public val dispatcher : Dispatcher = Dispatcher(this.javaClass.getPackage()?.getName() as String)

    /** Subclasses should override this to provide application initialization.
     */
    abstract fun init(config : AppConfig)

    fun start(args : Array<String>) {
        init(AppConfig.current)

        println("${args.size}")


    }

}
