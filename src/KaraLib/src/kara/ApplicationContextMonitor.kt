package kara

/** Implement ApplicationContextMonitor to be notified about context creation and destruction
 */
trait ApplicationContextMonitor {
    fun created(context: ApplicationContext)
    fun destroyed(context: ApplicationContext)
}

