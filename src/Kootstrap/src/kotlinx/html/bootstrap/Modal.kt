package kotlinx.html.bootstrap

import kara.ActionResult
import kara.BaseActionResult
import kara.link
import kotlinx.html.*
import javax.servlet.http.HttpServletResponse.SC_OK

class ModalBuilder {
    var button: (A.() -> Unit)? = null
    var h: highlight = highlight.default
    var c: caliber = caliber.default

    var formContent: (FORM.() -> Unit)? = null
    fun formContent(content: FORM.() -> Unit) {
        formContent = content
    }

    fun button(h: highlight = highlight.default, size: caliber = caliber.default, b: A.() -> Unit) {
        button = b
        this.h = h
        this.c = size
    }

    var header: (HtmlBodyTag.() -> Unit)? = null
    fun header(content: HtmlBodyTag.() -> Unit) {
        header = content
    }

    var body: (HtmlBodyTag.() -> Unit)? = null
    fun body(c: HtmlBodyTag.() -> Unit) {
        body = c
    }

    var save: (BUTTON.() -> Unit)? = null
    fun save(c: BUTTON.() -> Unit) {
        save = c
    }

    var footer: (HtmlBodyTag.()->Unit)? = null
    fun footer(c: HtmlBodyTag.()->Unit) {
        footer = c
    }

    var dialogStyle: (StyledElement.() -> Unit)? = null
    fun dialogStyle(s: StyledElement.() -> Unit) {
        dialogStyle = s
    }
}

private var unique: Int = 0
private val uniqueId: String get() = "__mdl${unique++}"

fun HtmlBodyTag.modalShow(id: String, h: highlight = highlight.default, c: caliber = caliber.default, button: A.() -> Unit) {
    action("#$id".link(), h, c) {
        this["role"] = "button"
        this["data-toggle"] = "modal"
        button()
    }
}

fun HtmlBodyTag.modalDialog(id: String, content: ModalBuilder.() -> Unit, body: (DIV.(ModalBuilder) -> Unit)? = null) {
    val builder = ModalBuilder()
    builder.content()
    div {
        this.id = id
        addClass("modal fade")
        this["tabindex"] = "-1"
        this["role"] = "dialog"
        this["aria-hidden"] = "true"
        div {
            addClass("modal-dialog")
            builder.dialogStyle ?. let {
                style {
                    it()
                }
            }

            div{
                addClass("modal-content")
                if (body == null) {
                    modalBody(builder)
                } else {
                    body(builder)
                }
            }
        }
    }
}

fun HtmlBodyTag.modalDialogForm(id: String, action: Link, formMethod: FormMethod = FormMethod.post, content: ModalBuilder.() -> Unit) {
    modalDialog(id, content) {
        form{
            addClass(form_horizontal)
            it.formContent?.let {it()}
            this.action = action
            this.method = formMethod
            modalBody(it)
        }
    }
}

fun HtmlBodyTag.modalFrame(content: ModalBuilder.() -> Unit, body: DIV.(ModalBuilder) -> Unit) {
    val builder = ModalBuilder()
    builder.content()
    val id = uniqueId
    modalShow(id, builder.h, builder.c, builder.button!!)
    div {
        addClass("modal fade")
        this.id = id
        this["tabindex"] = "-1"
        this["role"] = "dialog"
        this["aria-hidden"] = "true"
        div {
            addClass("modal-dialog")
            div {
                addClass("modal-content")
                body(builder)
            }
        }
    }
}

fun HtmlBodyTag.modal(content: ModalBuilder.() -> Unit) {
    modalFrame(content) {
        modalBody(it)
    }
}

fun HtmlBodyTag.modalForm(action: Link, formMethod: FormMethod = FormMethod.post, content: ModalBuilder.() -> Unit) {
    modalFrame(content) {
        form {
            addClass(form_horizontal)
            it.formContent?. let {it()}
            this.action = action
            this.method = formMethod
            modalBody(it)
        }
    }
}

fun HtmlBodyTag.modalBody(builder: ModalBuilder) {
    val head = builder.header ?: throw RuntimeException("No header provided")
    val body = builder.body ?: throw RuntimeException("No content provided")
    val foot = builder.footer // Optional
    val submitButton = builder.save // Optional

    div {
        addClass("modal-header")
        a {
            addClass(close)
            this["type"] = "button"
            this["aria-hidden"] = "true"
            this["data-dismiss"] = "modal"
            !"&times;"
        }

        h4 {
            addClass("modal-title")
            head()
        }
    }

    div {
        addClass("modal-body")
        body()
    }

    div {
        addClass("modal-footer")
        if (foot != null) {
            if (submitButton != null) error("'save {}' won't work if 'footer {}' is defined")
            foot()
        }
        else {
            bt_button(highlight.default) {
                buttonType = ButtonType.button
                this["aria-hidden"] = "true"
                this["data-dismiss"] = "modal"
                +"Close"
            }

            if (submitButton != null) {
                bt_button(highlight.primary) {
                    buttonType = ButtonType.submit
                    submitButton()
                }
            }
        }
    }
}

class MODAL() : HtmlBodyTag(null, "div") {
}

fun <T : HtmlBodyTag> T.modal(dataUrl: Link, effect: String = "fade", content: T.() -> Unit) {
    withAttributes(content) {
        attribute("data-url", dataUrl.href())
        attribute("data-use", "modal")
    }
    div {
        addClass("modal $effect")
    }
}

fun dialog(content: ModalBuilder.() -> Unit): ActionResult {
    val builder = ModalBuilder()
    builder.content()
    return ModalResult {
        div {
            addClass("modal-dialog")
            div {
                addClass("modal-content")
                modalBody(builder)
            }
        }
    }
}

fun dialogForm(action: Link, formMethod: FormMethod = FormMethod.post, enctype: EncodingType = EncodingType.urlencoded, content: ModalBuilder.() -> Unit): ActionResult {
    val builder = ModalBuilder()
    builder.content()
    return ModalResult {
        div {
            addClass("modal-dialog")
            div {
                addClass("modal-content")
                form {
                    addClass(form_horizontal)
                    builder.formContent?. let {it()}

                    this.action = action
                    this.method = formMethod
                    this.enctype = enctype

                    modalBody(builder)
                }
            }
        }
    }
}

class ModalResult(val modalContent: HtmlBodyTag.() -> Unit) : BaseActionResult("text/html", SC_OK, {
    MODAL().apply {
        addClass("modal-dialog")
        attribute("tabindex", "-1")
        attribute("role", "dialog")
        attribute("aria-hidden", "true")
        modalContent()
    }.toString()
})

fun HtmlBodyTag.sampleModalDialog() {
    modal {
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
