# Kara Web Framework

Kara is a web framework for the JVM using the [Kotlin programming language](http://confluence.jetbrains.net/display/Kotlin/Welcome).  It uses Kotlin's unique syntax to allow developers to write succinct, statically-typed HTML and CSS all in one language.

## Overview

Kara is still atan alpha stage, but it contains the following feature set:

* A Rails-like MVC architecture, where controllers contain business logic and views render the output.
* A statically-typed HTML view engine written in Kotlin
* Support for generating JSON responses directly from objects
* A statically-typed CSS rendering engine to compose stylesheets
* A Jetty-based embedded web server that automatically reloads the application when it's rebuilt
* A flexible JSON-based configuration system
* A project and file generator system to aid in setting up your code
* An environment system for different runtime environments

As a project in heavy development, there are still a number of **planned** features that **have yet to be implemented**:

* Much more documentation, clearly
* Integration with Kotlin's Javascript compiler to generate Javascript from Kotlin code (regular Javascript is currently supported)
* A deployment generator that creates servlet-ready WAR files
* Asset compilation and minification
* A plugin architecture that allows plugins for integrating third-party libraries

There are several features that are **not planned** for Kara, since we feel they fall outside of the scope of the framework:

* Support for different HTML template engines (JSP, FreeMaker, Velocity, Jade, etc.)
* Support for different Javascript wrappers (Coffeescript, TypeScript, Dart, etc.)
* A database integration library. There are plenty of good ORM's for the JVM, and while there's a chance that Kotlin could be used to do some interesting things with mapping databases, this can be developed independently of Kara

### Philosophy

Kara is not intended to be everything to ever one. There are slews of excellent web frameworks out there that meet a wide range of needs for developers.
If you're looking for something that lets you use a dynamic language with really simple syntax, or use proper HTML markup that a designer can edit, you'd be better served by something like Rails or Django.

Kara's specific goal is to make a web framework that focuses on developer productivity while maintaining a safe yet powerful syntax for defining markup.
The HTML view DSL is more concise than most markup languages used today, yet maintains strong type safety to help avoid common pitfalls that you'd experince in dynamic languages.
Plus, Kotlin is an excellent language to develop with - letting you easily express complex ideas through simple code that leverages the power of the JVM.


## Installation

To install Kara, simply checkout the repository and add the location to your system path.

### Platforms and Prerequisites

To run Kara, the only concrete prerequisite is a working JDK. Optionally, [Ant](http://ant.apache.org/) can be used to enable automatic server restart during development.

To develop a Kara app, it is highly recommended to use [IntelliJ IDEA](http://www.jetbrains.com/idea/) with the [Kotlin Plugin](http://confluence.jetbrains.net/display/Kotlin/Getting+Started).

So far, Kara has only been tested on Mac OS X. However, the runtime is obviously platform independent and the bash script should work fine on any other *nix. In order to get it to work on Windows, we'll need to develop a batch file to run it.


## Command Line Usage

Here's an overview of the Kara command line usage:

    Usage:
        kara [-options] command args

    Commands:
        c, config    Show the application's configuration for the current environment
        g, generate  Generates a new project or file (see below)
        h, help      Show this help message
        s, server    Run the Kara server on the current directory

    Options:
        -d, --debug  Show debug log messages
        -e, --env    Specify the environment (default is --env=development)
        -i, --info   Show info log messages (default)
        -w, --warn   Shwo only warning log messages

    Generators:
        project <name>      Generates a new Kara project with the given name.
                            Use the --package=<package> option to specify a package
                            that's different than the project name.
        controller <name>   Generates a new controller with the given name.
                            "Controller" will be automatically appended to the name)
        view <controller> <view>  Generate a new view for the given controller.

### Creating a Project

To create a Kara project, navigate to the directory you'd like the new project in and run something like:

    kara generate project MyKaraApp --package=com.example

This will create a new project in MyKaraApp with the package com.example. At this point, the project is just a set of directories and some boilerplate code.

To import the project into IDEA, follow the steps before. **NOTE: this is quite crude and we would benefit from some guidance from JetBrains as to how to smooth this out.**

* Open IDEA and select *File -> Import Project* and select your project's directory
* Select *File -> Project Structure* and choose a JDK from the dropdown. Either 6 or 7 should work
* After the project has been imported, select *File -> Import Module* and select the <package>.iml file that Kara created
* Open a .kt file in the project (like src/<package>/Application.kt) and IDEA will prompt you to set up the module with Kotlin, select "Set up module as JVM Kotlin module"
* Right click on lib/KaraLib.jar in the project navigator and select *Add as Library*

That should be it. You should now be able to build the project.

Once the project is built, it can be run in the development server by simply running:

    kara server

### Other Generators

To create a new controller in your existing project, simply run something like:

    kara generate controller Blog

which will create a controller called BlogController.

To create a new view in your existing project, run something like:

    kara generate view Blog List

which will generate a view called List that belongs to the BlogController.


## Project Structure

Here's the general structure of a Kara project:

    /bin                            Binary output (.class files)
    /config                         Application config files
    /lib                            Third party libraries
    /public/images                  Image resources
    /public/javascripts             Javascript source files
    /public/stylesheets             CSS stylesheets (third party or generated)
    /public/system                  App-generated files that need to persist between deployments
    /src/<package>/controllers      Application controllers
    /src/<package>/models           Database integration
    /src/<package>/styles           Stylesheet sources
    /src/<package>/views            HTML view sources
    /tmp                            Temporary files, like sessions


## Views

HTML views in Kara are created using a custom Kotlin DSL. Each view inherits from kara.view.HtmlView and looks like this:

    class Index() : HtmlView() {
        override fun render(context: ActionContext) {
            h2("Welcome to Kara")
            p("Your app is up and running, now it's time to make something!")
            p("Start by editing this file here: src/com/karaexample/views/home/Index.kt")
        }
    }

As you can see, the actual view markup is placed in the overriden render() method. The render() method accepts one argument, the action context. This context hold references to the action's request, response, session, and parameters.

Some more complex view markup might look like this:

    ol {
        li("List Item 1")
        li("List Item 2")
    }
    fieldset() {
        label("Text Input")
        input(inputType="text", value="Text")
    }
    p {
        + "Some text"
        + "Some more text"
    }

Each function represents a single HTML tag, and accepts arguments for the tag's attributes. The last argument is an optional function literal that can be used to populate the tag's children (as with the ol and fieldset tags above). Text content can either be passed directly to the tag function, or added inside its body with the + operator.

This flexible markup mechanism highlights the power of the Kotlin syntax, and can't be acheived in languages like Java or even Scala.

### Forms

Kara has a special form builder tag that allows you to generate form markup directly from a model object using reflection. For example:

    class BookForm(val book : Book) : HtmlView() {
        override fun render(context: ActionContext) {
            h2("Book Form")
            formFor(book, "/updatebook", FormMethod.Post) {
                p {
                    labelFor("title")
                    textFieldFor("title")
                }
                p {
                    labelFor("isPublished", "Is Published?")
                    checkBoxFor("isPublished")
                }
            }
        }
    }

In this case, the view itself accepts an argument for a model object - book - which is used during the rendering process.
The formFor method generates the form tag and binds all contained form methods to that form.
In this case, we generate labels, a text field, and a check box.
The values for those fields will be automatically populated with the corresponding properties from the model object.


## Stylesheets

Like HTML views, CSS stylesheets are also defined with a Kotlin DSL. A stylesheet inherits from kara.styles.Stylesheet, and looks like this:

    class DefaultStyles() : Stylesheet() {
        override fun render() {
            s("body") {
                backgroundColor = c("#f0f0f0")
            }
            s("#main") {
                width = 85.percent
                backgroundColor = c("#fff")
                margin = box(0.px, auto)
                padding = box(1.em)
                border = "1px solid #ccc"
                borderRadius = 5.px
            }

            s("input[type=text], textarea") {
                padding = box(4.px)
                width = 300.px
            }
            s("textarea") {
                height = 80.px
            }

            s("table.fields") {
                s("td") {
                    padding = box(6.px, 3.px)
                }
                s("td.label") {
                    textAlign = TextAlign.right
                }
                s("td.label.top") {
                    verticalAlign = VerticalAlign.top
                }
            }
        }
    }

Each selector is declared with the s() function, and it's attributes are set inside a function literal.
All possible CSS attributes are mapped to properties on the selector, and strongly-typed enums are used for attributes with a predefined set of possible values.
Selector nesting is automatically handled by nesting s() calls and generates the appropriate compound selectors.

Note that special value types exist for dimensional and color attributes. These include:

* Linear dimensions (like width, height, borderRadius, etc.) - created using extension functions on numeric values (i.e. 1.5.em, 3.px, 25.percent)
* Box dimensions (like padding and margin) - created using the box() function, which accepts 1-4 linear dimenions using the same logic as their CSS counterparts
* Colors - created using the c() function and passing a string defining the color

Because the CSS markup is defined using Kotlin itself, the DSL renders CSS preprocessing libraries like SASS and LESS obselete.
Variable and macros can be defined right in code, either inside the style class or as separate functions to share amongst several styles.


## Controllers

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

The routing mechanism allows for more complex routes, like:

    Get("complex/*/list/:id") fun complex() : ActionResult {
        return TextResult("complex: ${params[0]} id = ${params["id"]}")
    }

In this case, the asterisk acts as a wildcard (matching any value at that location), and :id acts as a named route parameter.
The parameter values are available inside the request through the controllers RouteParams object:

    this.params[0] // wildcard param
    this.params["id"] // named :id param

The most common ActionResult is an HtmlView, but you can also return raw text with TextResult(), JSON objects with JsonResult(), and redirects with RedirectRestul().


## Authors

Kara is developed by [Tiny Mission](http://tinymission.com). We're a small web and mobile development company and hope to use Kara to help us work faster and write better code.


## Contributing

There's plenty of work left to do to make Kara a first class framework, and we'd welcome contributions. Contact andy at tinymission.com to get involved.


## License

Kara is Open Source and licensed under the Apache Licenses, version 2.0. It can be freely used in commercial projects.
