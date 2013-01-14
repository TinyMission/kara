package generators.templates

import kara.generators.Generator

/**
 * Template strings used for the general files.
 */


fun appconfigTemplate(gen : Generator) : String {
    return """
{
     "kara": {
         "appPackage": "${gen.appPackage}",
         "publicDir": "public",
         "stylesheetDir": "stylesheets",
         "sessionDir": "tmp/sessions"
     }
}
"""
}

fun appconfigDevelopmentTemplate(gen : Generator) : String {
    return """
{
     "kara": {
         "port": 3000
     }
}
"""
}


fun buildxmlTemplate(gen : Generator) : String {
    return """
<project>
    <target name="restart">
        <exec executable="touch">
            <arg value="tmp/restart.txt"/>
        </exec>
    </target>
</project>
"""
}


fun applicationTemplate(gen : Generator) : String {
    return """
package ${gen.appPackage}

import kara.config.*

/**
 * This is the primary class for your application.
 */
public class Application() : kara.app.Application(Routes) {

    /**
     * Application-specific initialization code goes here.
     */
    public override fun init(config : AppConfig) {
    }

}

"""
}

fun routesTemplate(gen : Generator) : String {
    return """
package ${gen.appPackage}


import kara.controllers.*
import ${gen.appPackage}.controllers.*

object Routes {
    Get("/") class Index() : Request({
        ${gen.controllerClassName}.index()
    })
}

"""
}

fun controllerTemplate(gen : Generator) : String {
    return """
package ${gen.appPackage}.controllers

import ${gen.appPackage}.views.*
import ${gen.appPackage}.views.${gen.controllerSlug}.*
import kara.controllers.ActionResult

object ${gen.controllerClassName} {
    fun index() : ActionResult {
        return Index()
    }

}

"""
}
