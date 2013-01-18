package karatests.controllers

import kara.controllers.*
import karatests.views.HomeView
import karatests.views.DefaultLayout
import kara.views.ActionContext

object HomeController {
    fun index() : ActionResult {
        return HomeView()
    }

    fun test() : ActionResult {
        return TextResult("This is a test action")
    }

    fun update() : ActionResult {
        return TextResult("Something's been updated!")
    }
}
