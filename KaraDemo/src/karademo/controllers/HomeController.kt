package karademo.controllers

import kara.controllers.*
import karademo.views.*
import karademo.views.home.*
import karademo.models.Book


object HomeController {
    fun index() : ActionResult {
        return Index()
    }

    fun forms() : ActionResult {
        val book = Book("Ender's Game") {
            description = "This is an excellent book about a boy who gets drawn into an interstellar war."
            isPublished = true
        }
        //(this.session).setAttribute("hello", "world")
        return Forms(book)
    }
}
