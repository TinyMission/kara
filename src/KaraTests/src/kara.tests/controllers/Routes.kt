package kara.tests.controllers

import kara.*
import kara.tests.views.HomeView
import kara.tests.views.SomeFunView
import kara.tests.views.view
import java.net.SocketException
import kotlin.html.div
import kotlin.html.span

object Routes {
    @Get("/")
    class Index() : Request({ HomeView() })

    @Get("/test")
    class Test() : Request({ TextResult("This is a test action") })

    @Post("/update")
    class Update() : Request({ TextResult("Something's been updated!") })

    @Get("/optional/?p")
    class Optional(val p: String?) : Request({
        TextResult("optional/${p}")
    })

    @Get("/error/:brokenPipe")
    class Error(val brokenPipe: Boolean) : Request({
        if (brokenPipe) {
            throw SocketException("Broken Pipe")
        }
        throw RuntimeException("Something went wrong")
    })

    @Get("/template/:n")
    class SomeRoute(n: Int) : Request({
        when (n) {
            1 -> view {
                header {
                    div {

                    }
                }

                content {
                    left {
                        span {

                        }
                    }
                }
            }
            2 -> SomeFunView()
            else -> ErrorResult(404, "Not found")
        }
    })

    object Foo {
        @Get("#")
        class Blank() : Request({
            TextResult("blank")
        })

        @Get("bar")
        class Bar() : Request({
            TextResult("bar")
        })

        @Get("bar/baz")
        class Barbaz() : Request({
            TextResult("bar/baz")
        })

        @Get("#")
        class Foobar() : Request({
            TextResult("foobar")
        })

        @Get("*/list")
        class List() : Request({
            TextResult("list: ${params[0]}")
        })

        @Get("complex/*/list/:id")
        class Complex(id: String) : Request({
            TextResult("complex: ${params[0]} id = $id")
        })

        @Get("redirect")
        class Redirect() : Request({
            redirect("/foo/bar")
        })

        @Get("compute/:anInt/:aFloat")
        class Compute(val anInt: Int, val aFloat: Float) : Request({
            TextResult("compute: ${anInt}, ${aFloat}")
        })

        @Get("compute")
        class ComputeQuery(val anInt: Int, val aFloat: Float) : Request({
            TextResult("compute: ${anInt}, ${aFloat}")
        })
    }

    object Crud {
        @Get("")
        class Index() : Request({
            TextResult("index")
        })

        @Get(":id")
        class Show(id: Int) : Request({
            TextResult("show $id")
        })

        @Post("")
        class Create() : Request({
            TextResult("create")
        })

        @Put(":id")
        class Update(id: Int) : Request({
            TextResult("update ${id}")
        })

        @Delete(":id")
        class _Delete(id: String) : Request({
            TextResult("delete $id")
        })
    }
}
