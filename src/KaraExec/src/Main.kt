package kara.cli

import java.util.HashMap
import kara.*
import kara.internal.*
import kara.server.JettyRunner
import org.apache.log4j.BasicConfigurator
import org.apache.log4j.Logger
import org.apache.log4j.Level
import org.apache.log4j.LogManager
import java.util.ArrayList
import java.net.URL
import org.apache.log4j.PropertyConfigurator
import java.io.File

fun server(appConfig : AppConfig) {
    val jettyRunner = JettyRunner(appConfig)
    jettyRunner.init()
    jettyRunner.start()
}

fun config(appCongig : AppConfig) {
    println(appCongig.toString())
}

fun main(args: Array<String>) {
    val map = HashMap<String, String>()
    for (arg in args) {
        val data = arg.split('=')
        if (data.size == 2) {
            map[data[0]] = data[1]
        }
    }

    val appConfig = AppConfig(map["-env"] ?: "development", map["-jar"]?.let {File(it).toURI().toURL()})

    val logPath = appConfig.tryKey("kara.logPropertiesPath")

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
