package kara.generators

import org.apache.log4j.Logger
import java.io.File
import kara.generators.Permissions
import java.io.*
import java.util.Properties
import kara.*
import kara.internal.*
import generators.templates.*
import com.google.common.io.Files
import kara.server.JettyAppConfig

/** Possbile named tasks that the generator can perform. */
enum class GeneratorTask(val name : String) {
    project: GeneratorTask("project")
    route: GeneratorTask("route")
    view: GeneratorTask("view")
    fun toString() : String {
        return name
    }
}


/**
 * Actually performs the file and directory generation.
 */
class Generator(val appConfig : JettyAppConfig, val task : GeneratorTask, val args : List<String>) {
    val logger = Logger.getLogger("Generator")!!
    val permissions = Permissions()
    var karaHome = ""

    val appPackage : String
        get() = appConfig.appPackage

    /** The app package converted to a path. */
    val appPackagePath : String
        get() = appConfig.appPackage.replace(".", "/")

    /** Some strings used in various templates. */

    var routeSlug: String = ""

    var routeClassName: String = ""

    var viewName : String = ""

    var stylesheetName : String = ""

    var ide = "default"

    var projectName = ""

    /** Executes the generator task. */
    public fun exec() {
        // get the Kara home
        var home = System.getenv("KARA_HOME")
        if (home == null)
            throw RuntimeException("The KARA_HOME environment variable needs to be defined to use generators.")
        karaHome = home!!

        // parse the arguments
        parseArgs()

        when (task) {
            GeneratorTask.project -> {
                // ensure there's a project name
                if (args.size == 0)
                    throw RuntimeException("Need to provide a project name.")
                projectName = args[0]
                execProject(args[0])
            }
            GeneratorTask.route -> {
                // ensure there's a route name
                if (args.size == 0)
                    throw RuntimeException("Need to provide a route name.")
                for (arg in args)
                    execRoute(arg)
            }
            GeneratorTask.view -> {
                // ensure there's a route name and a view name
                if (args.size != 2)
                    throw RuntimeException("Need to provide a route name and a view name.")
                execView(args[0], args[1])
            }
            else -> throw RuntimeException("Unkown generator task ${task.toString()}")
        }
    }

    /** Parses the command line arguments */
    fun parseArgs() {
        for (arg in args) {
            if (arg.contains("=")) { // named argument
                val comps = arg.split("=")
                if (comps.size != 2)
                    throw RuntimeException("Argument $arg is invalid. Must be of the form name=value")
                val name = comps[0].replace("-", "")
                val value = comps[1]
                when (name) {
                    "package", "p" -> {
                        logger.info("Using application package $value")
                        appConfig["kara.appPackage"] = value
                    }
                    "ide", "i" -> {
                        logger.info("Using $value for IDE files")
                        if (value !in "idea") {
                            throw RuntimeException("$value not supported")
                        }
                        ide = value
                    }
                    else -> throw RuntimeException("Unkown generator argument $name")
                }
            }
        }
    }


    /** Executes the task to create a new project. */
    fun execProject(projectName : String) {
        if (!appConfig.contains("kara.package"))
            appConfig["kara.package"] = projectName

        // create the project root directory
        val projectRoot = File(appConfig.appRoot, projectName)
        if (projectRoot.exists()) {
            permissions.ask("project_overwrite", "$projectRoot already exists and its contents could be overwritten.")
        }
        else {
            logger.info("Creating project directory $projectRoot")
            projectRoot.mkdir()
        }
       appConfig["kara.appRoot"] = projectRoot.toString()

        // setup the project directory structure
        createDir("bin")
        createDir("config")
        createDir("lib")
        createDir("public")
        createDir("public/images")
        createDir("public/javascripts")
        createDir("public/stylesheets")
        createDir("public/system")
        createDir("src")
        createDir("src/$appPackagePath")
        createDir("src/$appPackagePath/routes")
        createDir("src/$appPackagePath/models")
        createDir("src/$appPackagePath/styles")
        createDir("src/$appPackagePath/views")
        createDir("tmp")

        createIDEFiles(ide)

        // render the templates
        renderTemplate(buildxmlTemplate(), "build.xml")
        renderTemplate(ivyTemplate(projectName), "ivy.xml")
        renderTemplate(appconfigTemplate(), "config/appconfig.json")
        renderTemplate(appconfigDevelopmentTemplate(), "config/appconfig.development.json")
        renderTemplate(applicationTemplate(), "src/$appPackagePath/Application.kt")

        // make the default routes and view
        execRoute("Home")

        println("\nYour project has been created! Now you're ready to import it into your favorite IDE and start coding.\n")
    }


    /** Executes the task to create a new route. */
    fun execRoute(routeName: String) {
        routeSlug = routeName.capitalize().toLowerCase()
        routeClassName = "${routeName}"

        ensureDir("src/$appPackagePath/routes")

        renderTemplate(routeTemplate(), "src/$appPackagePath/routes/${routeClassName}.kt")
        execLayout("Default")
        execView(routeName, "Index")
    }


    /** Executes the task to create a new view. */
    fun execLayout(name : String) {
        viewName = name.capitalize() + "Layout"
        stylesheetName = name.capitalize() + "Styles"

        ensureDir("src/$appPackagePath/views")
        ensureDir("src/$appPackagePath/styles")

        renderTemplate(layoutTemplate(), "src/$appPackagePath/views/${viewName}.kt")
        renderTemplate(stylesheetTemplate(), "src/$appPackagePath/styles/${stylesheetName}.kt")
    }


    /** Executes the task to create a new view. */
    fun execView(_routeName: String, vName : String) {
        var routeName = _routeName.capitalize()
        viewName = "${vName.capitalize()}View"

        routeSlug = routeName.toLowerCase()
        routeClassName = "${routeName}"

        ensureDir("src/$appPackagePath/views")
        ensureDir("src/$appPackagePath/views/$routeSlug")

        val outPath = "src/$appPackagePath/views/$routeSlug/${viewName}.kt"
        var isLanding = routeName == "Home" && vName == "Index"
        renderTemplate(viewTemplate(outPath, isLanding), outPath)
    }


    /** Esures the given relative directory inside the application root exists (it won't warn if it does). */
    fun ensureDir(dir : String) {
        val absDir = File(appConfig.appRoot, dir)
        if (!absDir.exists()) {
            logger.info("Creating directory $absDir")
            absDir.mkdirs()
        }
    }


    /** Creates the given relative directory inside the application root. */
    fun createDir(dir : String) {
        val absDir = File(appConfig.appRoot, dir)
        if (absDir.exists()) {
            permissions.ask("dir_overwrite", "$absDir already exists and its contents could be overwritten.")
        }
        else {
            logger.info("Creating directory $absDir")
            absDir.mkdirs()
        }
    }

    /** Renders a template to the target project. */
    fun renderTemplate(template : String, outPath : String) {
        // write the template to the file
        val outFile = File(appConfig.appRoot, outPath)
        if (outFile.exists()) {
            permissions.ask("file_overwrite", "$outPath already exists and will be overwritten.")
        }
        logger.info("Creating $outPath")
        outFile.writeText(template, "UTF-8")
    }

    /** Copies the file at srcPath to dstPath */
    fun copyFile(srcPath : String, dstPath : String) {
        val srcFile = File(karaHome, srcPath)
        if (!srcFile.exists())
            throw RuntimeException("File $srcPath does not exist in the Kara distribution")
        val dstFile = File(appConfig.appRoot, dstPath)
        logger.info("Copying file ${srcFile} to ${dstFile}")
        Files.copy(srcFile, dstFile)
    }

    fun createIDEFiles(ide: String) {
           when (ide) {
            "default", "idea" -> createIDEAFiles()
            else -> {
                throw RuntimeException("$ide not supported")
            }
        }
    }
}

