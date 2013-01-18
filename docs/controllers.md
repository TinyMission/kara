---
layout: default
title: Kara Controllers
isDoc: true
docPage: controllers
displayName: Controllers
icon: cogs
---

## Kara Controllers

The business logic for a Kara app is contained inside the controllers.
Each controller is a class that inherits from kara.controllers.BaseController, and defines one or more action methods.
An action method handles a specific request to the app, defined by the routing parameters associated with it, and returns an ActionResult.

    class HomeController() : BaseController(DefaultLayout()) {
        Get("/") fun index() : ActionResult {
            return Index()
        }

        Get("/test") fun test() : ActionResult {
            return TextResult("This is a simple text action")
        }

        Post("/updatebook") fun update() : ActionResult {
            return RedirectResult("/forms")
        }
    }

The controller above has three actions (index, test, and update). The first two respond to GET requests at / and /test, respectively.
The update action responds to POST requests at /updatebook.

If the route doesn't start with /, it's relative to the current controller.
The lowercase name of the controller, minus *controller*, is used as the first component of the route.
Any pound signs are replaced with the name of the action method.

    class FooController() : BaseController(DefaultLayout()) {
        Get("") fun index() : ActionResult {
            // maps to /foo
        }

        Get("bar") fun bar() : ActionResult {
            // maps to /foo/bar
        }

        Get("#") fun blank() : ActionResult {
            // maps to /foo/blank
        }

        Get(":id/#") fun edit(id : Int) : ActionResult {
            // maps to /foo/3/edit
        }
    }

The routing mechanism allows for more complex routes, like:

    Get("complex/*/list/:id") fun complex() : ActionResult {
        return TextResult("complex: ${params[0]} id = ${params["id"]}")
    }

In this case, the asterisk acts as a wildcard (matching any value at that location), and :id acts as a named route parameter.
The parameter values are available inside the request through the controllers RouteParams object:

    this.params[0] // wildcard param
    this.params["id"] // named :id param

When compound objects are passed as form parameters, they can be retrieved as a hash:

    // if form contains: book[title]=Foundation&book[author]=Isaac%20Asimov
    this.params.getHash("book") 
    // will return a has with {title="Foundation", author="Isaac Asimov"}

The most common ActionResult is an HtmlView, but you can also return raw text with TextResult(), JSON objects with JsonResult(), and redirects with RedirectRestul().
