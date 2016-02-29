package kara.cli

import kara.*
import kara.server.JettyRunner
import org.apache.log4j.*

fun server(appConfig : ApplicationConfig) {
    val jettyRunner = JettyRunner(appConfig)
    jettyRunner.start()
}

fun config(appConfig: ApplicationConfig) {
    println(appConfig.toString())
}

fun main(args: Array<String>) {
    val appConfig = ApplicationConfig.loadFrom(if (args.size > 0) args[0] else "development.conf")

    val logPath = appConfig.tryGet("kara.logPropertiesPath")

    if (logPath != null) {
        PropertyConfigurator.configureAndWatch(logPath, 5000)
    } else if (LogManager.getRootLogger()?.allAppenders?.hasMoreElements()?.not()?:true) {
        BasicConfigurator.configure()
        LogManager.getRootLogger()?.level = Level.INFO
    }

    config(appConfig)
    server(appConfig)
}
