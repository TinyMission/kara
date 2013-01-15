package karatests.controllers

import kara.controllers.Get
import kara.controllers.Post
import kara.controllers.*
import karatests.views.DefaultLayout

object Routes {
    val layout = DefaultLayout()

    Get("/")
    class Index(): Request({ HomeController.index() })

    Get("/test")
    class Test(): Request({ HomeController.test() })

    Post("/update")
    class Update(): Request({ HomeController.update() })

    object Foo {
        Get("#")
        class Blank() : Request({
            TextResult("blank")
        })

        Get("bar")
        class Bar() : Request({
            TextResult("bar")
        })

        Get("bar/baz")
        class Barbaz() : Request({
            TextResult("bar/baz")
        })

        Get("#")
        class Foobar() : Request({
            TextResult("foobar")
        })

        Get("*/list")
        class List() : Request({
            TextResult("list: ${params[0]}")
        })

        Get("complex/*/list/:id")
        class Complex(id : String) : Request({
            TextResult("complex: ${params[0]} id = $id")
        })

        Get("redirect")
        class Redirect() : Request({
            redirect("/foo/bar")
        })

        Get("compute/:anInt/:aFloat")
        class Compute(val anInt : Int, val aFloat : Float) : Request({
            TextResult("compute: ${anInt}, ${aFloat}")
        })

        Get("compute")
        class ComputeQuery(val anInt : Int, val aFloat : Float) : Request({
            TextResult("compute: ${anInt}, ${aFloat}")
        })
    }

    object Crud {
        Get("")
        class Index() : Request({
            TextResult("index")
        })

        Get(":id")
        class Show(id : Int) : Request({
            TextResult("show $id")
        })

        Post("")
        class Create() : Request({
            TextResult("create")
        })

        Put(":id")
        class Update(id : Int) : Request({
            TextResult("update ${id}")
        })

        Delete(":id")
        class _Delete(id : String) : Request({
            TextResult("delete $id")
        })
    }
}
