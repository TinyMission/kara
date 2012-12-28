package kara

import java.util.HashMap
import kara.config.*
import kara.server.JettyRunner
import org.apache.log4j.BasicConfigurator
import org.apache.log4j.Logger
import org.apache.log4j.Level
import org.apache.log4j.LogManager
import kara.generators.*
import java.util.ArrayList

fun server(appConfig : AppConfig) {
    val jettyRunner = JettyRunner(appConfig)
    jettyRunner.init()
    jettyRunner.start()
}

fun generator(appConfig : AppConfig, task : GeneratorTask, args : List<String>) {
    var generator = Generator(appConfig, task, args)
    generator.exec()
}

fun config(appCongig : AppConfig) {
    println(appCongig.toString())
}

fun help() {
    val s = """Welcome to the Kara command line.

Usage:
    kara [-options] command args

Commands:
    c, config    Show the application's configuration for the current environment
    g, generate  Generates a new project or file (see below)
    h, help      Show this help message
    u, update    Updates the application's Kara dependency to the latest version
    s, server    Run the Kara server on the current directory

Options:
    -d, --debug  Show debug log messages
    -e, --env    Specify the environment (default is --env=development)
    -i, --info   Show info log messages (default)
    -w, --warn   Shwo only warning log messages

Generators:
    project <name>      Generates a new Kara project with the given name.
                        Use the --package=<package> option to specify a package
                        that's different than the project name.
    controller <name>   Generates a new controller with the given name.
                        "Controller" will be automatically appended to the name)
    view <controller> <view>  Generate a new view for the given controller.

"""
    println(s)
}

fun main(args: Array<String>) {
    BasicConfigurator.configure()

    val logger = Logger.getLogger("Kara.Main")!!

    println("################################################################")
    println("#                    Hello, this is Kara!                      #")
    println("#            Pragmatic web development for the JVM             #")
    println("################################################################")
    println("")

    var startServer = false
    var runGenerator = false
    var showHelp = false
    var showConfig = false
    var generatorTask : GeneratorTask? = null
    val generatorArgs = ArrayList<String>()
    var env = "development"

    var logLevel = Level.INFO

    // parse command line arguments
    try {
        for (var arg in args) {
            val comps = arg.split("=")
            var value : String? = null
            if (comps.size == 2) {
                arg = comps[0]
                value = comps[1]
            }
            when (arg) {
                "c", "config" -> showConfig = true
                "s", "server" -> startServer = true
                "g", "generator" -> runGenerator = true
                "h", "help" -> showHelp = true
                "update" -> generatorTask = GeneratorTask.update
                "project" -> generatorTask = GeneratorTask.project
                "controller" -> generatorTask = GeneratorTask.controller
                "view" -> generatorTask = GeneratorTask.view
                "-d", "--debug" -> logLevel = Level.DEBUG
                "-e", "env" -> {
                    if (value == null)
                        throw RuntimeException("Must provide a value for the environment argument")
                    env = value!!
                }
                "-i", "--info" -> logLevel = Level.INFO
                "-w", "--warn" -> logLevel = Level.WARN
                else -> {
                    if (runGenerator) {
                        if (value == null)
                            generatorArgs.add(arg)
                        else
                            generatorArgs.add("$arg=$value")
                    }
                    else
                        logger.error("Unkown argument: $arg")
                }
            }
        }

        // create the app config
        val appConfig = AppConfig(System.getProperty("user.dir")!!, env)

        // set the log level
        LogManager.getRootLogger()?.setLevel(logLevel)

        if (showHelp) {
            help()
        }
        else if (showConfig) {
            config(appConfig)
        }
        else if (startServer) {
            server(appConfig)
        }
        else if (runGenerator) {
            if (generatorTask == null)
                throw RuntimeException("Need to specify a generator task to run. Run 'kara help' for usage.")
            generator(appConfig, generatorTask!!, generatorArgs)
        }
        else {
            throw RuntimeException("No valid command specified! Run 'kara help' for usage.")
        }
    }
    catch (ex : Exception) {
        println("ERROR: ${ex.getMessage()} \n")
        ex.printStackTrace()
    }

}
