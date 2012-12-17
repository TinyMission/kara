package karademo.controllers

import java.util.Date
import kara.controllers.*
import kara.views.Json
import karademo.models.Post
import karademo.views.*

/**
 */
class PostsController() : BaseController(layout = DefaultLayout()) {

    Get("get/:id") fun get(id : Int) : ActionResult {
        val post = Post(Date(), "Post ${id}")
        post.body = "This is the <em>post</em> body"

        //return Json(post)
        return posts.Show(post)
    }

}
