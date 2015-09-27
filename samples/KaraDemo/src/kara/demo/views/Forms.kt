package kara.demo.views

import kara.HtmlTemplateView
import kara.demo.models.Book
import kara.demo.routes.Home
import kara.demo.styles.StyleClasses.cLabel
import kara.demo.styles.StyleClasses.fields
import kara.demo.styles.StyleClasses.top
import kara.formForBean
import kotlin.html.*

fun Forms(book: Book) = HtmlTemplateView(DefaultTemplate()) {
    content {
        h2 { +"Forms" }
        formForBean(book, Home.Update(), FormMethod.post) {

            table {
                setClass(fields)
                tr {
                    td {
                        setClass(cLabel)
                        labelFor("title")
                    }
                    td {
                        textFieldFor("title")
                    }
                }
                tr {
                    td {
                        setClass(cLabel)
                        labelFor("author")
                    }
                    td {
                        textFieldFor("author")
                    }
                }
                tr {
                    td {
                        setClass(cLabel)
                        labelFor("isPublished", "Is Published?")
                    }
                    td {
                        checkBoxFor("isPublished")
                    }
                }
                tr {
                    td {
                        setClass(cLabel + top)
                        labelFor("description")
                    }
                    td {
                        textAreaFor("description")
                    }
                }
                tr {
                    td {
                        setClass(cLabel)
                        labelFor("category")
                    }
                    td {
                        radioFor("category", "fiction")
                        radioFor("category", "nonfiction")
                    }
                }
                tr {
                    td {
                        setClass(cLabel)
                    }
                    td {
                        submitButton("Submit")
                    }
                }
            }
        }
    }
}

