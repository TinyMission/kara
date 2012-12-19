package generators.templates

import kara.generators.Generator

/**
 * Templates for generating views.
 */

fun viewTemplate(gen : Generator) : String {
    return """
package ${gen.appPackage}.views.${gen.controllerSlug}

import kara.views.*

class ${gen.viewName}() : HtmlView() {
    override fun render(context: ActionContext) {
        h2("This is a Kara view")
    }
}

"""
}


fun layoutTemplate(gen : Generator) : String {
    return """
package ${gen.appPackage}.views

import kara.views.*

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

