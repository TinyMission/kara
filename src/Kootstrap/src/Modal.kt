package kotlin.html.bootstrap

import kotlin.html.*

class ModalBuilder() {
    var button: (A.()->Unit)? = null
    var h: highlight = highlight.default
    var c: caliber = caliber.default
    fun button(h: highlight = highlight.default, size: caliber = caliber.default, b: A.()->Unit) {
        button = b
        this.h = h
        this.c = size
    }

    var header: (HtmlBodyTag.()->Unit)? = null
    fun header(content: HtmlBodyTag.()->Unit) = header = content

    var body: (HtmlBodyTag.()->Unit)? = null
    fun body(c: HtmlBodyTag.()->Unit) = body = c

    var save: (BUTTON.()->Unit)? = null
    fun save(c: BUTTON.()->Unit) = save = c
}

private var unique: Int = 0
private val uniqueId: String get() = "__mdl${unique++}"

fun HtmlBodyTag.modalShow(id : String, h: highlight = highlight.default, c: caliber = caliber.default, button : A.()->Unit) {
    action("#$id".link(), h, c) {
        this["role"] = "button"
        this["data-toggle"] = "modal"
        button()
    }
}

fun HtmlBodyTag.modalDialog(id : String, content: ModalBuilder.()->Unit) {
    val builder = ModalBuilder()
    builder.content()
    div(s("modal fade"), id) {
        this["tabindex"] = "-1"
        this["role"] = "dialog"
        this["aria-hidden"] = "true"
        div(s("modal-dialog")) {
            div(s("modal-content")) {
                modalBody(builder)
            }
        }
    }
}

fun HtmlBodyTag.modalFrame(content: ModalBuilder.()->Unit, body: DIV.(ModalBuilder)->Unit) {
    val builder = ModalBuilder()
    builder.content()
    val id = uniqueId
    modalShow(id, builder.h, builder.c, builder.button!!)
    div(s("modal fade"), id) {
        this["tabindex"] = "-1"
        this["role"] = "dialog"
        this["aria-hidden"] = "true"
        div(s("modal-dialog")) {
            div(s("modal-content")) {
                body(builder)
            }
        }
    }
}

fun HtmlBodyTag.modal(content: ModalBuilder.()->Unit) {
    modalFrame(content) {
        modalBody(it)
    }
}

fun HtmlBodyTag.modalForm(action: Link, formMethod: FormMethod = FormMethod.post, content: ModalBuilder.()->Unit) {
    modalFrame(content) {
        form(form_horizontal) {
            this.action = action
            this.method = formMethod
            modalBody(it)
        }
    }
}

fun HtmlBodyTag.modalBody(builder: ModalBuilder) {
    val head = builder.header ?: throw RuntimeException("No header provided")
    val body = builder.body ?: throw RuntimeException("No content provided")

    div(s("modal-header")) {
        a(close) {
            this["type"] = "button"
            this["aria-hidden"] = "true"
            this["data-dismiss"] = "modal"
            +"&times;"
        }

        head()
    }

    div(s("modal-body")) {
        body()
    }

    div(s("modal-footer")) {
        button(highlight.default) {
            buttonType = ButtonType.button
            this["aria-hidden"] = "true"
            this["data-dismiss"] = "modal"
            +"Close"
        }

        val submitButton = builder.save
        if (submitButton != null) {
            button(highlight.primary) {
                buttonType = ButtonType.submit
                submitButton()
            }
        }
    }
}

fun HtmlBodyTag.sampleModalDialog() {
    modal() {
        button {
            +"Show Modal"
        }
        header {
            h4 {
                +"The Modal Dialog"
            }
        }
        body {
            p {
                +"Text goes here"
            }
        }

        save {
            +"Save"
        }
    }
}
