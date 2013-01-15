package karademo

import kara.controllers.Get
import karademo.controllers.HomeController
import kara.controllers.*
import karademo.views.DefaultLayout
import java.util.Date
import karademo.views.posts.Show

object Routes {
    val layout = DefaultLayout()

    Get("/")
    class Index(): Request({ HomeController.index() })

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
        HomeController.forms()
    })

    object Posts {
        Get("get/:id") class GetPost(id: Int) : Request({
            val post = models.Post(Date(), "Post ${id}")
            post.body = "This is the <em>post</em> body"

            //return Json(post)
            Show(post)
        })
    }
}
