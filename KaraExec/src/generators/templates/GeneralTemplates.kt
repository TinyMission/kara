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
public class Application() : kara.app.Application() {

    /**
     * Application-specific initialization code goes here.
     */
    public override fun init(config : AppConfig) {
    }

}

"""
}


fun controllerTemplate(gen : Generator) : String {
    return """
package ${gen.appPackage}.controllers

import kara.controllers.*
import ${gen.appPackage}.views.*
import ${gen.appPackage}.views.${gen.controllerSlug}.*

class ${gen.controllerClassName}() : BaseController(DefaultLayout()) {
    Get("/") fun index() : ActionResult {
        return Index()
    }

}

"""
}
