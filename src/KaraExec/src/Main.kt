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

    val appConfig = AppConfig(map["-env"] ?: "development", map["-jar"]?.let {URL("file:$it")})

    config(appConfig)
    server(appConfig)
}
