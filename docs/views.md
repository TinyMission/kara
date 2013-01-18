---
layout: default
title: Kara Views
isDoc: true
docPage: views
displayName: Views
icon: file-alt
---

## Kara Views

HTML views in Kara are created using a custom Kotlin DSL. Each view inherits from kara.view.HtmlView and looks like this:

    class Index() : HtmlView() {
        override fun render(context: ActionContext) {
            h2("Welcome to Kara")
            p("Your app is up and running, now it's time to make something!")
            p("Start by editing this file here: src/com/karaexample/views/home/Index.kt")
        }
    }

As you can see, the actual view markup is placed in the overriden render() method.
The render() method accepts one argument, the action context.
This context hold references to the action's request, response, session, and parameters.

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

Each function represents a single HTML tag, and accepts arguments for the tag's attributes.
The last argument is an optional function literal that can be used to populate the tag's children (as with the ol and fieldset tags above).
Text content can either be passed directly to the tag function, or added inside its body with the + operator.

This flexible markup mechanism highlights the power of the Kotlin syntax, and can't be acheived in languages like Java or even Scala.

### Layouts

Applications generally contain HTML markup that's shared between views.
In Kara (as well as many other web frameworks) these are called layouts.
By convention, layout files are placed in the root of the app's views directory.
Each layout inherits from the *HtmlLayout* class. Consider the following example:

    class DefaultLayout() : HtmlLayout() {
        override fun render(context: ActionContext, mainView: HtmlView) {
            head {
                title("Kara Demo Title")
                stylesheet(DefaultStyles())
            }
            body {
                h1("Kara Demo Site")
                div(id="main") {
                    renderView(context, mainView)
                }
                a(text="Kara is developed by Tiny Mission", href="http://tinymission.com")
            }
        }
    }

As with views, a layout's markup is placed in the render() method, which accepts the current action context, as well as the main view to render.
The mainView is rendered with the renderView() function.

At this point, the layout used to render each view is specified at the controller level (either passed to the constructor, or specified later inside an action).


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
