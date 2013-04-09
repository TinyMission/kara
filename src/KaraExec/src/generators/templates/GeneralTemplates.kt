package generators.templates

import kara.generators.Generator

/**
 * Template strings used for the general files.
 */


fun Generator.appconfigTemplate() : String {
    return """
{
     "kara": {
         "appPackage": "${appPackage}",
         "publicDir": "public",
         "stylesheetDir": "stylesheets",
         "sessionDir": "tmp/sessions"
     }
}
"""
}

fun Generator.appconfigDevelopmentTemplate() : String {
    return """
{
     "kara": {
         "port": 3000
     }
}
"""
}


fun Generator.buildxmlTemplate() : String {
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


fun Generator.applicationTemplate() : String {
    return """
package ${appPackage}

import kara.*

/**
 * This is the primary class for your application.
 */
public class Application(config: AppConfig) : kara.Application(config) {

    /**
     * Application-specific initialization code goes here.
     */
    {

    }

}

"""
}

fun Generator.routeTemplate() : String {
    return """
package ${appPackage}.routes

import ${appPackage}.views.*
import ${appPackage}.views.${routeSlug}.*
import kara.*

object ${routeClassName} {
    val layout = DefaultLayout()
    Get("/") class Index() : Request({
        IndexView()
    })

}

"""
}
