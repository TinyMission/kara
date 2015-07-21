package kara.cli

import java.util.HashMap
import kara.*
import kara.internal.*
import kara.server.JettyRunner
import org.apache.log4j.*
import java.util.ArrayList

fun server(appConfig : ApplicationConfig) {
    val jettyRunner = JettyRunner(appConfig)
    jettyRunner.start()
}

fun config(appConfig: ApplicationConfig) {
    println(appConfig.toString())
}

fun main(args: Array<String>) {
    val appConfig = ApplicationConfig.loadFrom(if (args.size() > 0) args[0] else "development.conf")

    val logPath = appConfig.tryGet("kara.logPropertiesPath")

    if (logPath != null) {
        PropertyConfigurator.configureAndWatch(logPath, 5000)
    }
    else {
        BasicConfigurator.configure()
        LogManager.getRootLogger()?.level = Level.INFO
    }

    config(appConfig)
    server(appConfig)
}
