package kara.generators

import org.apache.log4j.Logger
import java.io.File
import kara.generators.Permissions
import org.apache.velocity.VelocityContext
import org.apache.velocity.app.Velocity
import java.io.*
import java.util.Properties
import kara.config.AppConfig

/** Possbile named tasks that the generator can perform. */
enum class GeneratorTask(val name : String) {
    project: GeneratorTask("project")
    controller: GeneratorTask("controller")
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

    val context = VelocityContext()

    /** Package for the root application */
    public var appPackage : String = ""

    /** The app package converted to a path. */
    val appPackagePath : String
        get() = appPackage.replace(".", "/")

    /** Executes the generator task. */
    public fun exec() {
        // get the Kara home
        var home = System.getenv("KARA_HOME")
        if (home == null)
            throw RuntimeException("The KARA_HOME environment variable needs to be defined to use generators.")
        karaHome = home!!

        // setup velocity
        val velocityProps = Properties()
        velocityProps.set("file.resource.loader.path", File(karaHome, "KaraExec/templates/").toString())
        Velocity.init(velocityProps)
        context.put("gen", this)

        // parse the arguments
        parseArgs()

        when (task) {
            GeneratorTask.project -> {
                // ensure there's a project name
                if (args.size == 0)
                    throw RuntimeException("Need to provide a project name.")
                execProject(args[0])
            }
            GeneratorTask.controller -> {
                // ensure there's a controller name
                if (args.size == 0)
                    throw RuntimeException("Need to provide a controller name.")
                execController(args[0])
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
                        appPackage = value
                    }
                    else -> throw RuntimeException("Unkown generator argument $name")
                }
            }
        }
    }


    /** Executes the task to create a new project. */
    fun execProject(val projectName : String) {
        if (appPackage.length == 0)
            appPackage = projectName

        // create the project root directory
        val projectRoot = File(appConfig.appRoot, projectName)
        if (projectRoot.exists()) {
            permissions.ask("project_overwrite", "$projectRoot already exists and its contents could be overwritten.")
        }
        else {
            logger.info("Creating project directory $projectRoot")
            projectRoot.mkdir()
        }
        val appRoot = projectRoot.toString()

        // setup the project directory structure
        createDir("bin")
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

        // render the templates
        renderTemplate("build.xml")
        renderTemplate("appconfig.json")
        renderTemplate("src.appPackage.Application.kt")

        // make the default controller and view
        execController("Home")
    }


    /** Executes the task to create a new controller. */
    fun execController(var controllerName : String) {
        controllerName = controllerName.capitalize()

        val controllerSlug = controllerName.toLowerCase()
        context.put("controllerSlug", controllerSlug)
        val controllerClassName = "${controllerName}Controller"
        context.put("controllerClassName", controllerClassName)

        ensureDir("src/$appPackagePath/controllers")

        renderTemplate("src.appPackage.controllers.Controller.kt", "src/$appPackagePath/controllers/${controllerClassName}.kt")
        execView(controllerName, "Index")
    }


    /** Executes the task to create a new view. */
    fun execView(var controllerName : String, var viewName : String) {
        controllerName = controllerName.capitalize()
        viewName = viewName.capitalize()
        context.put("viewName", viewName)

        val controllerSlug = controllerName.toLowerCase()
        context.put("controllerSlug", controllerSlug)
        val controllerClassName = "${controllerName}Controller"
        context.put("controllerClassName", controllerClassName)

        ensureDir("src/$appPackagePath/views")
        ensureDir("src/$appPackagePath/views/$controllerSlug")

        renderTemplate("src.appPackage.views.View.kt", "src/$appPackagePath/views/$controllerSlug/${viewName}.kt")
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
    fun renderTemplate(template : String, var outPath : String = "") {
        if (!Velocity.templateExists("$template.vm"))
            throw RuntimeException("Template $template.vm doesn't exist.")

        // compute the output file path
        if (outPath.length == 0) {
            val comps = template.split("\\.")
            val fileName = "${comps[comps.size-2]}.${comps[comps.size-1]}"
            val outDir = template.replace(fileName, "").replace(".", "/").replace("appPackage", appPackagePath)
            var outFile = File(appConfig.appRoot, "$outDir/$fileName")
            if (outFile.exists()) {
                permissions.ask("file_overwrite", "$outFile already exists and will be overwritten.")
            }
            outPath = outFile.toString()
        }
        else {
            outPath = File(appConfig.appRoot, outPath).toString()
        }

        // render the template
        logger.info("Creating $outPath")
        val writer = FileWriter(outPath)
        Velocity.mergeTemplate("$template.vm", context, writer)
        writer.flush()

    }
}
