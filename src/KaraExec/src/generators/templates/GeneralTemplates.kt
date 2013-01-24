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
public class Application(config: AppConfig) : kara.app.Application(config) {

    /**
     * Application-specific initialization code goes here.
     */
    {

    }

}

"""
}

fun routeTemplate(gen : Generator) : String {
    return """
package ${gen.appPackage}.routes

import ${gen.appPackage}.views.*
import ${gen.appPackage}.views.${gen.routeSlug}.*
import kara.controllers.*

object ${gen.routeClassName} {
    val layout = DefaultLayout()
    Get("/") class Index() : Request({
        IndexView()
    })

}

"""
}
