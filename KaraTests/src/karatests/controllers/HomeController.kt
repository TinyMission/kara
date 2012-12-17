package karatests.controllers

import kara.controllers.*
import karatests.views.HomeView
import karatests.views.DefaultLayout

class HomeController() : BaseController(layout = DefaultLayout()) {

    Get("/") fun index() : ActionResult {
        return HomeView()
    }

    Get("/test") fun test() : ActionResult {
        return TextResult("This is a test action")
    }

    Post("/udpate") fun update() : ActionResult {
        return TextResult("Something's been updated!")
    }
}