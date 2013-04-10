package karademo.views.home

import kara.*
import karademo.models.Book
import karademo.styles.StyleClasses.*
import karademo.routes.Home
import karademo.views.DefaultLayout

class Forms(val book : Book) : HtmlView(DefaultLayout()) {
    override fun render(context: ActionContext) {
        h2("Forms")
        formForBean(book, Home.Update(), FormMethod.post) {

            table(fields) {
                tr {
                    td(cLabel) {
                        labelFor("title")
                    }
                    td {
                        textFieldFor("title")
                    }
                }
                tr {
                    td(cLabel) {
                        labelFor("author")
                    }
                    td {
                        textFieldFor("author")
                    }
                }
                tr {
                    td(cLabel) {
                        labelFor("isPublished", "Is Published?")
                    }
                    td {
                        checkBoxFor("isPublished")
                    }
                }
                tr {
                    td(cLabel + top) {
                        labelFor("description")
                    }
                    td {
                        textAreaFor("description")
                    }
                }
                tr {
                    td(cLabel) {
                        labelFor("category")
                    }
                    td {
                        radioFor("category", "fiction")
                        radioFor("category", "nonfiction")
                    }
                }
                tr {
                    td(cLabel) {
                    }
                    td {
                        submitButton("Submit")
                    }
                }
            }
        }
    }
}
