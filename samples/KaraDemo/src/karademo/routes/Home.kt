package karademo.routes

import kara.*
import java.util.Date
import karademo.views.posts.Show
import karademo.models.Book

object Home {
    Get("/")
    class Index(): Request({ karademo.views.home.Index() })

    Get("/test")
    class Test() : Request({
        TextResult("This is a test action, yo")
    })

    Post("/updatebook")
    class Update() : Request({
        println("parameters:")
        println(params.getHash("book").toString())
        redirect("/forms")
    })

    Get("/forms")
    class Forms() : Request({
        val book = Book("Ender's Game") {
            description = "This is an excellent book about a boy who gets drawn into an interstellar war."
            isPublished = true
        }
        //(this.session).setAttribute("hello", "world")
        karademo.views.home.Forms(book)
    })

    object Posts {
        Get("get/:id") class GetPost(id: Int) : Request({
            val post = karademo.models.Post(Date(), "Post ${id}")
            post.body = "This is the <em>post</em> body"

            //return Json(post)
            Show(post)
        })
    }
}
