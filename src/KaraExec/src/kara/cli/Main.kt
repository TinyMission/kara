package kara.cli

import kara.ApplicationConfig
import kara.server.JettyRunner

fun server(appConfig : ApplicationConfig) {
    val jettyRunner = JettyRunner(appConfig)
    jettyRunner.start()
}

fun config(appConfig: ApplicationConfig) {
    println(appConfig.toString())
}

fun main(args: Array<String>) {
    val appConfig = ApplicationConfig.loadFrom(if (args.isNotEmpty()) args[0] else "development.conf")

    config(appConfig)
    server(appConfig)
}
