package kara.generators

import org.apache.log4j.Logger
import java.io.File
import kara.generators.Permissions
import java.io.*
import java.util.Properties
import kara.config.AppConfig
import generators.templates.*
import com.google.common.io.Files

/** Possbile named tasks that the generator can perform. */
enum class GeneratorTask(val name : String) {
    project: GeneratorTask("project")
    controller: GeneratorTask("controller")
    update: GeneratorTask("update")
    view: GeneratorTask("view")
    fun toString() : String {
        return name
    }
}


/**
 * Actually performs the file and directory generation.
 */
class Generator(val appConfig : AppConfig, val task : GeneratorTask, val args : List<String>) {
    val logger = Logger.getLogger("Generator")!!
    val permissions = Permissions()
    var karaHome = ""

    val appPackage : String
        get() = appConfig.appPackage

    /** The app package converted to a path. */
    val appPackagePath : String
        get() = appConfig.appPackage.replace(".", "/")

    /** Some strings used in various templates. */

    var controllerSlug : String = ""

    var controllerClassName : String = ""

    var viewName : String = ""

    var stylesheetName : String = ""

    var ide = ""

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
            GeneratorTask.controller -> {
                // ensure there's a controller name
                if (args.size == 0)
                    throw RuntimeException("Need to provide a controller name.")
                for (arg in args)
                    execController(arg)
            }
            GeneratorTask.update -> {
                execUpdate()
            }
            GeneratorTask.view -> {
                // ensure there's a controller name and a view name
                if (args.size != 2)
                    throw RuntimeException("Need to provide a controller name and a view name.")
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
    fun execProject(val projectName : String) {
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
        createDir("src/$appPackagePath/controllers")
        createDir("src/$appPackagePath/models")
        createDir("src/$appPackagePath/styles")
        createDir("src/$appPackagePath/views")
        createDir("tmp")

        createIDEFiles(ide)
        // copy the necessary libraries
        copyFile("lib/kotlin-runtime.jar", "lib/kotlin-runtime.jar")
        copyFile("out/jars/KaraLib.jar", "lib/KaraLib.jar")

        // render the templates
        renderTemplate(moduleImlTemplate(this), "${appConfig.appPackage}.iml")
        renderTemplate(buildxmlTemplate(this), "build.xml")
        renderTemplate(appconfigTemplate(this), "config/appconfig.json")
        renderTemplate(appconfigDevelopmentTemplate(this), "config/appconfig.development.json")
        renderTemplate(applicationTemplate(this), "src/$appPackagePath/Application.kt")

        // make the default controller and view
        execController("Home")
        renderTemplate(routesTemplate(this), "src/$appPackagePath/Routes.kt")

        println("\nYour project has been created! Now you're ready to import it into your favorite IDE and start coding.\n")
    }


    /** Executes the task to create a new controller. */
    fun execController(var controllerName : String) {
        controllerName = controllerName.capitalize()

        controllerSlug = controllerName.toLowerCase()
        controllerClassName = "${controllerName}Controller"

        ensureDir("src/$appPackagePath/controllers")

        renderTemplate(controllerTemplate(this), "src/$appPackagePath/controllers/${controllerClassName}.kt")
        execLayout("Default")
        execView(controllerName, "Index")
    }


    /** Executes the task to create a new view. */
    fun execLayout(var name : String) {
        viewName = name.capitalize() + "Layout"
        stylesheetName = name.capitalize() + "Styles"

        ensureDir("src/$appPackagePath/views")
        ensureDir("src/$appPackagePath/styles")

        renderTemplate(layoutTemplate(this), "src/$appPackagePath/views/${viewName}.kt")
        renderTemplate(stylesheetTemplate(this), "src/$appPackagePath/styles/${stylesheetName}.kt")
    }


    /** Executes the task to create a new view. */
    fun execView(var controllerName : String, var vName : String) {
        controllerName = controllerName.capitalize()
        viewName = vName.capitalize()

        controllerSlug = controllerName.toLowerCase()
        controllerClassName = "${controllerName}Controller"

        ensureDir("src/$appPackagePath/views")
        ensureDir("src/$appPackagePath/views/$controllerSlug")

        val outPath = "src/$appPackagePath/views/$controllerSlug/${viewName}.kt"
        var isLanding = controllerName == "Home" && vName == "Index"
        renderTemplate(viewTemplate(this, outPath, isLanding), outPath)
    }


    /** Updates the target project's Kara dependency to the latest version. */
    fun execUpdate() {
        copyFile("out/jars/KaraLib.jar", "lib/KaraLib.jar")
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
    fun renderTemplate(template : String, var outPath : String) {
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
            "idea" -> createIDEAFiles()
            else -> {
                throw RuntimeException("$ide not supported")
            }
        }
    }

    fun createIDEAFiles() {
        // TODO: .idea works on Windows too?
        ensureDir(".idea")
        ensureDir(".idea/scopes")
        ensureDir(".idea/copyright")
        ensureDir(".idea/libraries")
        renderTemplate(gradleTemplate(this), ".idea/gradle.xml")
        renderTemplate(compilerTemplate(this), ".idea/compiler.xml")
        renderTemplate(encodingsTemplate(this), ".idea/encodings.xml")
        renderTemplate(uiDesignerTemplate(this), ".idea/uiDesigner.xml")
        renderTemplate(vcsTemplate(this), ".idea/vcs.xml")
        renderTemplate(miscTemplate(this), ".idea/misc.xml")
        renderTemplate(scopeTemplate(this), ".idea/scopes/scope_settings.xml")
        renderTemplate(nameTemplate(this), ".idea/.name")
        renderTemplate(copyrightTemplate(this), ".idea/copyright/profile_settings.xml")
        renderTemplate(modulesTemplate(this), ".idea/modules.xml")
        renderTemplate(libraryKaraTemplate(this), ".idea/libraries/KaraLib.xml")
    }

}

