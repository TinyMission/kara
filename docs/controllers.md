---
layout: default
title: Kara Controllers
isDoc: true
docPage: controllers
displayName: Controllers
icon: cogs
---

## Kara Controllers

The business logic for a Kara app is contained inside the controller classes mapped to HTTP requests with annotations (routes).
Each controller class must inherit kara.controllers.Request and defines an action method.
An action method handles a specific request to the app, defined by the routing annotation associated with it, and returns an kara.controllers.ActionResult.

    object Home {
        val layout = DefaultLayout()

        Get("/")
        class Index() : Request({
            karademo.views.home.Index()
        })

        Get("/test")
        class Test() : Request({
            TextResult("This is a test action, yo")
        })

        Post("/updatebook")
        class Update() : Request({
            redirect("/forms")
        })
    }

On the sample above we have defined three controllers (Index, Test, and Update). The first two respond to GET requests at / and /test, respectively.
The update action responds to POST requests at /updatebook.

If the route doesn't start with /, it's relative to the current controller.
The lowercase name of the controller, minus *controller*, is used as the first component of the route.
Any pound signs are replaced with the name of the action method.

    object FooRoutes {
        val layout = DefaultLayout()

        Get("")
        class Index() : Request({
            // maps to /foo
        })

        Get("bar")
        class Bar() : Request({
            // maps to /foo/bar
        })

        Get("#")
        class Blank() : Request({
            // maps to /foo/blank
        })

        Get(":id/#")
        class Edit(id : Int) : Request({
            // maps to /foo/3/edit
        })
    }

The routing mechanism allows for more complex routes, like:

    Get("complex/*/list/:id")
    Complex(id : Int) : Request({
        TextResult("complex: ${params[0]} id = ${params["id"]}")
    })

In this case, the asterisk acts as a wildcard (matching any value at that location), and :id acts as a named route parameter.
The parameter values are available inside the request through the controllers RouteParams object:

    this.params[0] // wildcard param
    this.params["id"] // named :id param

When compound objects are passed as form parameters, they can be retrieved as a hash:

    // if form contains: book[title]=Foundation&book[author]=Isaac%20Asimov
    this.params.getHash("book") 
    // will return a has with {title="Foundation", author="Isaac Asimov"}

The most common ActionResult is an HtmlView, but you can also return raw text with TextResult(), JSON objects with JsonResult(), and redirects with RedirectRestful().
