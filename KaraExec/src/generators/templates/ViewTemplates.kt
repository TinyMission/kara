package generators.templates

import kara.generators.Generator


fun generalViewBody(filePath : String) : String {
    return """h2("This is a Kara view")
p("You can find me here: $filePath")
"""
}

fun landingViewBody(filePath : String) : String {
    return """h2("Welcome to Kara")
p("Your app is up and running, now it's time to make something!")
p("Start by editing this file here: $filePath")
"""
}


/**
 * Templates for generating views.
 */

fun viewTemplate(gen : Generator, filePath : String, isLanding : Boolean = false) : String {
    return """
package ${gen.appPackage}.views.${gen.controllerSlug}

import kara.views.*

class ${gen.viewName}() : HtmlView() {
    override fun render(context: ActionContext) {
        ${if (isLanding) landingViewBody(filePath) else generalViewBody(filePath)}
    }
}

"""
}


fun layoutTemplate(gen : Generator) : String {
    return """
package ${gen.appPackage}.views

import kara.views.*
import ${gen.appPackage}.styles.*

class ${gen.viewName}() : HtmlLayout() {
    override fun render(context: ActionContext, mainView: HtmlView) {
        head {
            title("Kara App")
            stylesheet(${gen.stylesheetName}())
        }
        body {
            h1("Kara App")
            div(id="main") {
                renderView(context, mainView)
            }
        }
    }
}

"""
}


fun stylesheetTemplate(gen : Generator) : String {
    return """
package ${gen.appPackage}.styles

import kara.styles.*

class ${gen.stylesheetName}() : Stylesheet() {
    override fun render() {
        s("body") {
            backgroundColor = c("#f0f0f0")
            fontFamily = "\"Lucida Sans Unicode\", \"Lucida Grande\", sans-serif"
        }
        s("h1") {
            marginLeft = 1.em
        }
        s("#main") {
            width = 85.percent
            backgroundColor = c("#fff")
            margin = box(0.px, auto)
            padding = box(1.em)
            border = "1px solid #ccc"
            borderRadius = 5.px
        }
    }
}

"""
}

