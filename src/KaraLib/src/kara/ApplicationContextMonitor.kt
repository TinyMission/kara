package kara

/** Implement ApplicationContextMonitor to be notified about context creation and destruction
 */
interface ApplicationContextMonitor {
    fun created(context: ApplicationContext)
    fun destroyed(context: ApplicationContext)
    val priority : Int get() = 100
}

