package karademo.controllers

import kara.controllers.*
import karademo.views.*
import karademo.views.home.*
import karademo.models.Book

class HomeController() : BaseController(DefaultLayout()) {
    Get("/") fun index() : ActionResult {
        return Index()
    }

    Get("/test") fun test() : ActionResult {
        return TextResult("This is a test action, yo")
    }

    Post("/updatebook") fun update() : ActionResult {
        return RedirectResult("/forms")
    }

    Get("/forms") fun forms() : ActionResult {
        val book = Book("Ender's Game") {
            description = "This is an excellent book about a boy who gets drawn into an interstellar war."
            isPublished = true
        }
        //(this.session).setAttribute("hello", "world")
        if (book.isPublished)
            throw RuntimeException("This is an exception!")
        return Forms(book)
    }

}