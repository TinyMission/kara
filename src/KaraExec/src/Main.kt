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

fun server(appConfig : AppConfig) {
    val jettyRunner = JettyRunner(appConfig)
    jettyRunner.init()
    jettyRunner.start()
}

fun config(appCongig : AppConfig) {
    println(appCongig.toString())
}

fun main(args: Array<String>) {
    val env = if (args.size > 0) args[0] else "development"
    val appConfig = AppConfig(env)

    config(appConfig)
    server(appConfig)
}
