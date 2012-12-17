package karademo.views.home

import kara.views.*
import karademo.models.Book

class Forms(val book : Book) : HtmlView() {
    override fun render(context: ActionContext) {
        h2("Forms")
        formFor(book, "/updatebook", FormMethod.Post) {
            table(c="fields") {
                tr {
                    td(c="label") {
                        labelFor("title")
                    }
                    td {
                        textFieldFor("title")
                    }
                }
                tr {
                    td(c="label") {
                        labelFor("author")
                    }
                    td {
                        textFieldFor("author")
                    }
                }
                tr {
                    td(c="label") {
                        labelFor("isPublished", "Is Published?")
                    }
                    td {
                        checkBoxFor("isPublished")
                    }
                }
                tr {
                    td(c="label top") {
                        labelFor("description")
                    }
                    td {
                        textAreaFor("description")
                    }
                }
                tr {
                    td(c="label") {
                        labelFor("category")
                    }
                    td {
                        radioFor("category", "fiction")
                        radioFor("category", "nonfiction")
                    }
                }
                tr {
                    td(c="label") {
                    }
                    td {
                        submitButton("Submit")
                    }
                }
            }
        }
    }
}
