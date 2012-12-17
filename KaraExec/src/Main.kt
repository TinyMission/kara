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

fun server() {
//    val jettyRunner = JettyRunner(AppConfig.current.appRoot, "karademo")
    val jettyRunner = JettyRunner(System.getProperty("user.dir")!!, "karademo")
    jettyRunner.init()
    jettyRunner.start()
}

fun generator(task : GeneratorTask, args : List<String>) {
    var generator = Generator(task, System.getProperty("user.dir")!!, args)
    generator.exec()
}

fun help() {
    val s = """Welcome to the Kara command line.

Usage:
    kara [-options] command args

Commands:
    g, generate  Generates a new project or file (see below)
    h, help      Show this help message
    s, server    Run the Kara server on the current directory

Options:
    -d, --debug  Show debug log messages
    -i, --info   Show info log messages (default)
    -w, --warn   Shwo only warning log messages

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
    var generatorTask : GeneratorTask? = null
    val generatorArgs = ArrayList<String>()

//    var appRoot = System.getProperty("user.dir") as String
//    AppConfig.current = AppConfig(appRoot, "development") // this breaks the runtime for some reason

    var logLevel = Level.INFO

    // parse command line arguments
    try {
        for (arg in args) {
            when (arg) {
                "s", "server" -> startServer = true
                "g", "generator" -> runGenerator = true
                "h", "help" -> showHelp = true
                "project" -> generatorTask = GeneratorTask.project
                "-d", "--debug" -> logLevel = Level.DEBUG
                "-i", "--info" -> logLevel = Level.INFO
                "-w", "--warn" -> logLevel = Level.WARN
                else -> {
                    if (runGenerator)
                        generatorArgs.add(arg)
                    else
                        logger.error("Unkown argument: $arg")
                }
            }
        }

        // set the log level
        LogManager.getRootLogger()?.setLevel(logLevel)

        if (showHelp) {
            help()
        }
        else if (startServer) {
            server()
        }
        else if (runGenerator) {
            if (generatorTask == null)
                throw RuntimeException("Need to specify a generator task to run. Run 'kara help' for usage.")
            generator(generatorTask!!, generatorArgs)
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
