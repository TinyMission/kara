package karatests.controllers

import kara.controllers.*


class FooController() : BaseController() {

    Get("bar") fun bar() : ActionResult {
        return TextResult("bar")
    }

    Get("bar/baz") fun barbaz() : ActionResult {
        return TextResult("bar/baz")
    }

    Get("[default]") fun foobar() : ActionResult {
        return TextResult("foobar")
    }

    Get("*/list") fun list() : ActionResult {
        return TextResult("list: ${params[0]}")
    }

    Get("complex/*/list/:id") fun complex() : ActionResult {
        return TextResult("complex: ${params[0]} id = ${params["id"]}")
    }

    Get("redirect") fun redirect_action() : ActionResult {
        return redirect("/foo/bar")
    }

    Get("compute/:anInt/:aFloat") fun compute(anInt : Int, aFloat : Float) : ActionResult {
        return TextResult("compute: ${anInt}, ${aFloat}")
    }
}