package karademo.routes

import kara.*
import java.util.Date
import karademo.models.Book

object Home {
    Get("/")
    class Index() : Request({ karademo.views.Index() })

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
        karademo.views.Forms(book)
    })

    Get("/json")
    class JsonPage : Request({
        json {
            jsonObject {
                jsonValue("version", 5)
                jsonObject("people")
                {
                    jsonValue("id", 1)
                    jsonValue("name", "Ilya")
                }
                jsonArray("cities")
                {
                    jsonValue("a")
                    jsonValue("b")
                    jsonValue("c")
                    jsonObject {
                        jsonValue("x", "y")
                    }
                }
            }
        }
    })

    object Posts {
        Get("get/:id") class GetPost(id: Int) : Request({
            val post = karademo.models.Post(Date(), "Post ${id}")
            post.body = "This is the <em>post</em> body"
            karademo.views.Show(post)
        })
    }
}
