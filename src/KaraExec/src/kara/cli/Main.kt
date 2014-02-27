package kara.cli

import java.util.HashMap
import kara.*
import kara.internal.*
import kara.server.JettyRunner
import org.apache.log4j.*
import java.util.ArrayList
import java.net.URL
import java.io.File

fun server(appConfig : ApplicationConfig) {
    val jettyRunner = JettyRunner(appConfig)
    jettyRunner.start()
}

fun config(appConfig: ApplicationConfig) {
    println(appConfig.toString())
}

fun main(args: Array<String>) {
    val map = HashMap<String, String>()
    for (arg in args) {
        val data = arg.split('=')
        if (data.size == 2) {
            map[data[0]] = data[1]
        }
    }

    val appConfig = ApplicationConfig(map["-env"] ?: "development", map["-jar"]?.let {File(it).toURI().toURL()})

    val logPath = appConfig.tryGet("kara.logPropertiesPath")

    if (logPath != null) {
        PropertyConfigurator.configureAndWatch(logPath, 5000)
    }
    else {
        BasicConfigurator.configure()
        LogManager.getRootLogger()?.setLevel(Level.INFO)
    }

    config(appConfig)
    server(appConfig)
}
