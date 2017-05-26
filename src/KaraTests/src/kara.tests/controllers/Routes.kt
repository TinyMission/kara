package kara.tests.controllers

import kara.*
import kara.tests.views.HomeView
import kara.tests.views.SomeFunView
import kara.tests.views.view
import kotlinx.html.CssElement
import kotlinx.html.div
import kotlinx.html.span
import java.math.BigDecimal
import java.net.SocketException

@Get("/test/test.css")
object TestStyles : Stylesheet() {
    override fun CssElement.render() {
        body {}
    }
}

object Routes {
    @Get("/")
    class Index : Request({ HomeView() })

    @Get("/test")
    class Test : Request({ TextResult("This is a test action") })

    @Post("/update")
    class Update : Request({ TextResult("Something's been updated!") })

    @Get("/optional/?p")
    class Optional(val p: String?) : Request({
        TextResult("optional/$p")
    })

    @Get("/default/?p")
    class Default(val p: String? = "test") : Request({
        TextResult("default/$p")
    })

    @Get("/ndefault/?p")
    class NDefault(val p: Int? = 42) : Request({
        TextResult("ndefault/$p")
    })

    @Get("/emptyStrings")
    class EmptyStrings(val es: String, val esn: String?, val int: Int?) : Request({
        TextResult("es:$es,esn:$esn,int:$int")
    })

    @Get("/arrayParam")
    class ArrayParam(val arr: Array<String?>) : Request({
        TextResult(arr.joinToString())
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

    @Get("/route-params/:param") class RouteParams(val param: String) : Request({
        TextResult(param)
    })

    object Foo {
        @Get("#")
        class Blank : Request({
            TextResult("blank")
        })

        @Get("bar")
        class Bar : Request({
            TextResult("bar")
        })

        @Get("bar/baz")
        class Barbaz : Request({
            TextResult("bar/baz")
        })

        @Get("#")
        class Foobar : Request({
            TextResult("foobar")
        })

        @Get("*/list")
        class List : Request({
            TextResult("list: ${params[0]}")
        })

        @Get("complex/*/list/:id")
        class Complex(val id: String) : Request({
            TextResult("complex: ${params[0]} id = $id")
        })

        @Get("redirect")
        class Redirect : Request({
            redirect("/foo/bar")
        })

        @Get("compute/:anInt/:aFloat")
        class Compute(val anInt: Int, val aFloat: Float) : Request({
            TextResult("compute: $anInt, $aFloat")
        })

        @Get("compute")
        class ComputeQuery(val anInt: Int, val aFloat: Float) : Request({
            TextResult("compute: $anInt, $aFloat")
        })
    }

    object Crud {
        @Get("")
        class Index : Request({
            TextResult("index")
        })

        @Get(":id")
        class Show(val id: Int) : Request({
            TextResult("show $id")
        })

        @Post("")
        class Create : Request({
            TextResult("create")
        })

        @Put(":id")
        class Update(val id: Int) : Request({
            TextResult("update $id")
        })

        @Delete(":id")
        class _Delete(val id: String) : Request({
            TextResult("delete $id")
        })
    }

    @Controller("text/plain")
    @Location("/fun")
    object Function {
        @Get("")
        fun index() = "index"

        @Get("bar")
        fun bar() = "bar"

        @Get("compute/:anInt/:aDecimal")
        fun compute(anInt: Int, aDecimal: BigDecimal) = aDecimal * BigDecimal(anInt)

        @Get("custom-result/:code")
        fun customResultCode(code: Int) {
            code.resultWithStatusCode(code)
        }

        @Get("empty")
        fun nothing() {}
    }

    object InterfaceControllerTest {
        @InterfaceController
        @Get("/Action")
        open class SomeInterfaceController( handler: ActionContext.() -> ActionResult = { TextResult("It's interface") })
            : Request(handler)

        class ImplInterfaceController() : SomeInterfaceController({ TextResult("It's implementation") })
    }

    object InterfaceNotFinalControllerTest {
        @InterfaceController
        @Get("/Action")
        open class SomeInterfaceController( handler: ActionContext.() -> ActionResult = { TextResult("It's interface") })
            : Request(handler)

        open class ImplInterfaceController() : SomeInterfaceController({ TextResult("It's implementation") })
    }

    object InterfaceParamControllerTest {
        @InterfaceController
        @Get("/Action")
        open class SomeInterfaceController(val code: String, handler: ActionContext.() -> ActionResult = { TextResult("It's interface") })
            : Request(handler)

        class ImplInterfaceController(code: String) : SomeInterfaceController(code, { TextResult("It's implementation with param $code") })
    }
}
