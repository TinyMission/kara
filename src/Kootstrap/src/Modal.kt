package kotlin.html.bootstrap

import kotlin.html.*

class ModalBuilder() {
    var button: (A.()->Unit)? = null
    fun button(b: A.()->Unit) = button = b

    var header: (HtmlBodyTag.()->Unit)? = null
    fun header(content: HtmlBodyTag.()->Unit) = header = content

    var body: (HtmlBodyTag.()->Unit)? = null
    fun body(c: HtmlBodyTag.()->Unit) = body = c

    var save: (BUTTON.()->Unit)? = null
    fun save(c: BUTTON.()->Unit) = save = c
}

private var unique: Int = 0
private val uniqueId: String get() = "__mdl${unique++}"

fun HtmlBodyTag.modal(content: ModalBuilder.()->Unit) {
    val id = uniqueId

    val builder = ModalBuilder()
    builder.content()

    showModalButton(id, builder.button!!)
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

fun HtmlBodyTag.modalForm(action: Link, formMethod: FormMethod = FormMethod.post, content: ModalBuilder.()->Unit) {
    val id = uniqueId

    val builder = ModalBuilder()
    builder.content()

    showModalButton(id, builder.button!!)
    div(s("modal fade"), id) {
        this["tabindex"] = "-1"
        this["role"] = "dialog"
        this["aria-hidden"] = "true"

        div(s("modal-dialog")) {
            div(s("modal-content")) {
                form(form_horizontal) {
                    this.action = action
                    this.method = formMethod

                    modalBody(builder)
                }
            }
        }
    }
}

fun HtmlBodyTag.showModalButton(id: String, title: A.()->Unit) {
    a() {
        href = "#$id".link()
        this["role"] = "button"
        this["data-toggle"] = "modal"

        title()
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
        action {
            this["aria-hidden"] = "true"
            this["data-dismiss"] = "modal"
            +"Close"
        }

        if (builder.save != null) {
            button(highlight.primary, body = builder.save!!)
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
