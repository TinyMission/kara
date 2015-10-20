package kotlin.html

val empty_contents: Any.() -> Unit = { }

fun HTML.body(init: BODY.() -> Unit) = build(BODY(this), init)
class BODY(containingTag: HTML) : HtmlBodyTag(containingTag, "body")

public abstract class HtmlBodyTag(containingTag: HtmlTag?, name: String, renderStyle: RenderStyle = RenderStyle.expanded, contentStyle: ContentStyle = ContentStyle.block) : HtmlTag(containingTag, name, renderStyle, contentStyle) {
    public var id: String by Attributes.id
    public var style: String by Attributes.style

    fun style(init: StyledElement.() -> Unit) {
        val element = StyledElement("inline")
        element.init()
        val builder = StringBuilder()
        for ((k, v) in element.attributes) {
            builder.append("$k:$v;")
        }

        this["style"] = builder.toString()
    }

    fun addClass(c: StyleClass?) {
        if (c != null) {
            addClass(c.name)
        }
    }

    fun addClass(c: String?) {
        if (c != null) {
            val old = get("class")
            setClass(if (!old.isNullOrEmpty()) "$old $c" else c)
        }
    }

    fun setClass(c: StyleClass?) {
        setClass(c?.name ?: "")
    }

    fun setClass(c: String) {
        attribute("class", c)
    }
}

inline fun <T : HtmlBodyTag> contentTag(tag: T, c: String, contents: T.() -> Unit) {
    tag.addClass(c)
    tag.contents()
}

inline fun <T : HtmlBodyTag> contentTag(tag: T, contents: T.() -> Unit) {
    tag.contents()
}

inline fun HtmlBodyTag.button(c: String, contents: BUTTON.() -> Unit) = contentTag(BUTTON(this), c, contents)
inline fun HtmlBodyTag.button(contents: BUTTON.() -> Unit) = contentTag(BUTTON(this), contents)

inline fun HtmlBodyTag.dl(c: String, contents: DL.() -> Unit) = contentTag(DL(this), c, contents)
inline fun HtmlBodyTag.dl(contents: DL.() -> Unit) = contentTag(DL(this), contents)

inline fun DL.dt(c: String, contents: DT.() -> Unit) = contentTag(DT(this), c, contents)
inline fun DL.dt(contents: DT.() -> Unit) = contentTag(DT(this), contents)

inline fun DL.dd(c: String, contents: DD.() -> Unit) = contentTag(DD(this), c, contents)
inline fun DL.dd(contents: DD.() -> Unit) = contentTag(DD(this), contents)

inline fun HtmlBodyTag.h1(c: String, contents: H1.() -> Unit) = contentTag(H1(this), c, contents)
inline fun HtmlBodyTag.h1(contents: H1.() -> Unit) = contentTag(H1(this), contents)

inline fun HtmlBodyTag.h2(c: String, contents: H2.() -> Unit) = contentTag(H2(this), c, contents)
inline fun HtmlBodyTag.h2(contents: H2.() -> Unit) = contentTag(H2(this), contents)

inline fun HtmlBodyTag.h3(c: String, contents: H3.() -> Unit) = contentTag(H3(this), c, contents)
inline fun HtmlBodyTag.h3(contents: H3.() -> Unit) = contentTag(H3(this), contents)

inline fun HtmlBodyTag.h4(c: String, contents: H4.() -> Unit) = contentTag(H4(this), c, contents)
inline fun HtmlBodyTag.h4(contents: H4.() -> Unit) = contentTag(H4(this), contents)

inline fun HtmlBodyTag.h5(c: String, contents: H5.() -> Unit) = contentTag(H5(this), c, contents)
inline fun HtmlBodyTag.h5(contents: H5.() -> Unit) = contentTag(H5(this), contents)

inline fun HtmlBodyTag.img(c: String, contents: IMG.() -> Unit) = contentTag(IMG(this), c, contents)
inline fun HtmlBodyTag.img(contents: IMG.() -> Unit) = contentTag(IMG(this), contents)

inline fun HtmlBodyTag.input(c: String, contents: INPUT.() -> Unit) = contentTag(INPUT(this), c, contents)
inline fun HtmlBodyTag.input(contents: INPUT.() -> Unit) = contentTag(INPUT(this), contents)

inline fun HtmlBodyTag.label(c: String, contents: LABEL.() -> Unit) = contentTag(LABEL(this), c, contents)
inline fun HtmlBodyTag.label(contents: LABEL.() -> Unit) = contentTag(LABEL(this), contents)

inline fun HtmlBodyTag.select(c: String, contents: SELECT.() -> Unit) = contentTag(SELECT(this), c, contents)
inline fun HtmlBodyTag.select(contents: SELECT.() -> Unit) = contentTag(SELECT(this), contents)

inline fun HtmlBodyTag.textarea(c: String, contents: TEXTAREA.() -> Unit) = contentTag(TEXTAREA(this), c, contents)
inline fun HtmlBodyTag.textarea(contents: TEXTAREA.() -> Unit) = contentTag(TEXTAREA(this), contents)

inline fun HtmlBodyTag.a(c: String, contents: A.() -> Unit) = contentTag(A(this), c, contents)
inline fun HtmlBodyTag.a(contents: A.() -> Unit) = contentTag(A(this), contents)

open class A(containingTag: HtmlBodyTag) : HtmlBodyTag(containingTag, "a", contentStyle = ContentStyle.propagate) {
    public var href: Link by Attributes.href
    public var rel: String by Attributes.rel
    public var target: String by Attributes.target
}

open class BUTTON(containingTag: HtmlBodyTag) : HtmlBodyTag(containingTag, "button", RenderStyle.adaptive, ContentStyle.propagate) {
    public var name: String by Attributes.name
    public var value: String by Attributes.value
    public var buttonType: ButtonType by Attributes.buttonType
    public var href: Link by Attributes.href
}

fun HtmlBodyTag.hr() = contentTag(HR(this), empty_contents)
fun HtmlBodyTag.hr(c: String) = contentTag(HR(this), c, empty_contents)

fun HtmlBodyTag.br() = contentTag(BR(this), empty_contents)
fun HtmlBodyTag.br(c: String) = contentTag(BR(this), c, empty_contents)

inline fun HtmlBodyTag.div(c: String, contents: DIV.() -> Unit) = contentTag(DIV(this), c, contents)
inline fun HtmlBodyTag.div(contents: DIV.() -> Unit) = contentTag(DIV(this), contents)

inline fun HtmlBodyTag.b(c: String, contents: B.() -> Unit) = contentTag(B(this), c, contents)
inline fun HtmlBodyTag.b(contents: B.() -> Unit) = contentTag(B(this), contents)

inline fun HtmlBodyTag.i(c: String, contents: I.() -> Unit) = contentTag(I(this), c, contents)
inline fun HtmlBodyTag.i(contents: I.() -> Unit) = contentTag(I(this), contents)

inline fun HtmlBodyTag.p(c: String, contents: P.() -> Unit) = contentTag(P(this), c, contents)
inline fun HtmlBodyTag.p(contents: P.() -> Unit) = contentTag(P(this), contents)

inline fun HtmlBodyTag.pre(c: String, contents: PRE.() -> Unit) = contentTag(PRE(this), c, contents)
inline fun HtmlBodyTag.pre(contents: PRE.() -> Unit) = contentTag(PRE(this), contents)

inline fun HtmlBodyTag.span(c: String, contents: SPAN.() -> Unit) = contentTag(SPAN(this), c, contents)
inline fun HtmlBodyTag.span(contents: SPAN.() -> Unit) = contentTag(SPAN(this), contents)

inline fun HtmlBodyTag.strong(c: String, contents: STRONG.() -> Unit) = contentTag(STRONG(this), c, contents)
inline fun HtmlBodyTag.strong(contents: STRONG.() -> Unit) = contentTag(STRONG(this), contents)

inline fun HtmlBodyTag.small(c: String, contents: SMALL.() -> Unit) = contentTag(SMALL(this), c, contents)
inline fun HtmlBodyTag.small(contents: SMALL.() -> Unit) = contentTag(SMALL(this), contents)

inline fun HtmlBodyTag.blockquote(c: String, contents: BLOCKQUOTE.() -> Unit) = contentTag(BLOCKQUOTE(this), c, contents)
inline fun HtmlBodyTag.blockquote(contents: BLOCKQUOTE.() -> Unit) = contentTag(BLOCKQUOTE(this), contents)

inline fun HtmlBodyTag.address(c: String, contents: ADDRESS.() -> Unit) = contentTag(ADDRESS(this), c, contents)
inline fun HtmlBodyTag.address(contents: ADDRESS.() -> Unit) = contentTag(ADDRESS(this), contents)

inline fun HtmlBodyTag.em(c: String, contents: EM.() -> Unit) = contentTag(EM(this), c, contents)
inline fun HtmlBodyTag.em(contents: EM.() -> Unit) = contentTag(EM(this), contents)

open class BR(containingTag: HtmlBodyTag) : HtmlBodyTag(containingTag, "br", RenderStyle._empty)
open class HR(containingTag: HtmlBodyTag) : HtmlBodyTag(containingTag, "hr", RenderStyle._empty)
open class DIV(containingTag: HtmlBodyTag) : HtmlBodyTag(containingTag, "div")
open class I(containingTag: HtmlBodyTag) : HtmlBodyTag(containingTag, "i", contentStyle = ContentStyle.propagate)
open class B(containingTag: HtmlBodyTag) : HtmlBodyTag(containingTag, "b", contentStyle = ContentStyle.propagate)
open class P(containingTag: HtmlBodyTag) : HtmlBodyTag(containingTag, "p")
open class PRE(containingTag: HtmlBodyTag) : HtmlBodyTag(containingTag, "pre")
open class SPAN(containingTag: HtmlBodyTag) : HtmlBodyTag(containingTag, "span", contentStyle = ContentStyle.propagate)
open class STRONG(containingTag: HtmlBodyTag) : HtmlBodyTag(containingTag, "strong", contentStyle = ContentStyle.propagate)
open class SMALL(containingTag: HtmlBodyTag) : HtmlBodyTag(containingTag, "small", contentStyle = ContentStyle.propagate)
open class EM(containingTag: HtmlBodyTag) : HtmlBodyTag(containingTag, "em", contentStyle = ContentStyle.propagate)
open class ADDRESS(containingTag: HtmlBodyTag) : HtmlBodyTag(containingTag, "address")
open class BLOCKQUOTE(containingTag: HtmlBodyTag) : HtmlBodyTag(containingTag, "blockquote") {
    public var cite: Link by Attributes.cite
}


open class DL(containingTag: HtmlBodyTag) : HtmlBodyTag(containingTag, "dl")
open class DD(containingTag: DL) : HtmlBodyTag(containingTag, "dd", contentStyle = ContentStyle.propagate)
open class DT(containingTag: DL) : HtmlBodyTag(containingTag, "dt", contentStyle = ContentStyle.propagate)

abstract class ListTag(containingTag: HtmlBodyTag, name: String) : HtmlBodyTag(containingTag, name)
open class OL(containingTag: HtmlBodyTag) : ListTag(containingTag, "ol")
open class UL(containingTag: HtmlBodyTag) : ListTag(containingTag, "ul")
open class LI(containingTag: ListTag) : HtmlBodyTag(containingTag, "li")

inline fun HtmlBodyTag.ul(c: String, contents: UL.() -> Unit) = contentTag(UL(this), c, contents)
inline fun HtmlBodyTag.ul(contents: UL.() -> Unit) = contentTag(UL(this), contents)

inline fun HtmlBodyTag.ol(c: String, contents: OL.() -> Unit) = contentTag(OL(this), c, contents)
inline fun HtmlBodyTag.ol(contents: OL.() -> Unit) = contentTag(OL(this), contents)

inline fun ListTag.li(c: String, contents: LI.() -> Unit) = contentTag(LI(this), c, contents)
inline fun ListTag.li(contents: LI.() -> Unit) = contentTag(LI(this), contents)

open class H1(containingTag: HtmlBodyTag) : HtmlBodyTag(containingTag, "h1")
open class H2(containingTag: HtmlBodyTag) : HtmlBodyTag(containingTag, "h2")
open class H3(containingTag: HtmlBodyTag) : HtmlBodyTag(containingTag, "h3")
open class H4(containingTag: HtmlBodyTag) : HtmlBodyTag(containingTag, "h4")
open class H5(containingTag: HtmlBodyTag) : HtmlBodyTag(containingTag, "h5")

open class IMG(containingTag: HtmlBodyTag) : HtmlBodyTag(containingTag, "img", RenderStyle._empty, ContentStyle.text) {
    public var width: Int by Attributes.width
    public var height: Int by Attributes.height
    public var src: Link by Attributes.src
    public var alt: String by Attributes.alt
}

open class INPUT(containingTag: HtmlBodyTag) : HtmlBodyTag(containingTag, "input", RenderStyle.adaptive, ContentStyle.propagate) {
    public var alt: String by Attributes.alt
    public var autocomplete: Boolean by Attributes.autocomplete
    public var autofocus: Boolean by Attributes.autofocus
    public var checked: Boolean by Attributes.checked
    public var disabled: Boolean by Attributes.disabled
    public var height: Int by Attributes.height
    public var maxlength: Int by Attributes.maxlength
    public var multiple: Boolean by Attributes.multiple
    public var inputType: InputType by Attributes.inputType
    public var name: String by Attributes.name
    public var pattern: String by Attributes.pattern
    public var placeholder: String by Attributes.placeholder
    public var readonly: Boolean by Attributes.readonly
    public var required: Boolean by Attributes.required
    public var size: Int by Attributes.size
    public var src: Link by Attributes.src
    public var step: Int by Attributes.step
    public var value: String by Attributes.value
    public var width: Int by Attributes.width
}

abstract class TableTag(containingTag: HtmlBodyTag, name: String) : HtmlBodyTag(containingTag, name)
open class TABLE(containingTag: HtmlBodyTag) : TableTag(containingTag, "table")
open class THEAD(containingTag: TABLE) : TableTag(containingTag, "thead")
open class TFOOT(containingTag: TABLE) : TableTag(containingTag, "tfoot")
open class TBODY(containingTag: TABLE) : TableTag(containingTag, "tbody")
open class TR(containingTag: TableTag) : HtmlBodyTag(containingTag, "tr")
open class TH(containingTag: TR) : HtmlBodyTag(containingTag, "th")
open class TD(containingTag: TR) : HtmlBodyTag(containingTag, "td")

inline fun HtmlBodyTag.table(c: String, contents: TABLE.() -> Unit) = contentTag(TABLE(this), c, contents)
inline fun HtmlBodyTag.table(contents: TABLE.() -> Unit) = contentTag(TABLE(this), contents)

inline fun TABLE.tbody(c: String, contents: TBODY.() -> Unit) = contentTag(TBODY(this), c, contents)
inline fun TABLE.tbody(contents: TBODY.() -> Unit) = contentTag(TBODY(this), contents)

inline fun TABLE.thead(c: String, contents: THEAD.() -> Unit) = contentTag(THEAD(this), c, contents)
inline fun TABLE.thead(contents: THEAD.() -> Unit) = contentTag(THEAD(this), contents)

inline fun TABLE.tfoot(c: String, contents: TFOOT.() -> Unit) = contentTag(TFOOT(this), c, contents)
inline fun TABLE.tfoot(contents: TFOOT.() -> Unit) = contentTag(TFOOT(this), contents)

inline fun TableTag.tr(c: String, contents: TR.() -> Unit) = contentTag(TR(this), c, contents)
inline fun TableTag.tr(contents: TR.() -> Unit) = contentTag(TR(this), contents)

inline fun TR.th(c: String, contents: TH.() -> Unit) = contentTag(TH(this), c, contents)
inline fun TR.th(contents: TH.() -> Unit) = contentTag(TH(this), contents)

inline fun TR.td(c: String, contents: TD.() -> Unit) = contentTag(TD(this), c, contents)
inline fun TR.td(contents: TD.() -> Unit) = contentTag(TD(this), contents)


inline fun HtmlBodyTag.form(c: String, contents: FORM.() -> Unit) = contentTag(FORM(this), c, contents)
inline fun HtmlBodyTag.form(contents: FORM.() -> Unit) = contentTag(FORM(this), contents)

inline fun SELECT.option(c: String, contents: OPTION.() -> Unit) = contentTag(OPTION(this), c, contents)
inline fun SELECT.option(contents: OPTION.() -> Unit) = contentTag(OPTION(this), contents)

inline fun SELECT.optiongroup(c: String, contents: OPTGROUP.() -> Unit) = contentTag(OPTGROUP(this), c, contents)
inline fun SELECT.optiongroup(contents: OPTGROUP.() -> Unit) = contentTag(OPTGROUP(this), contents)

open class FIELDSET(containingTag: HtmlBodyTag) : HtmlBodyTag(containingTag, "fieldset")

public open class FORM(containingTag: HtmlBodyTag) : HtmlBodyTag(containingTag, "form") {
    public var action: Link by Attributes.action
    public var enctype: EncodingType by Attributes.enctype
    public var method: FormMethod by Attributes.method
    public var novalidate: Boolean by Attributes.novalidate
}

open class SELECT(containingTag: HtmlBodyTag) : HtmlBodyTag(containingTag, "select") {
    public var name: String by Attributes.name
    public var size: Int by Attributes.size
    public var multiple: Boolean by Attributes.multiple
    public var disabled: Boolean by Attributes.disabled
    public var required: Boolean by Attributes.required
}

open class OPTION(containingTag: HtmlBodyTag) : HtmlBodyTag(containingTag, "option") {
    public var value: String by Attributes.value
    public var label: String by Attributes.label
    public var disabled: Boolean by Attributes.disabled
    public var selected: Boolean by Attributes.selected
}

open class OPTGROUP(containingTag: HtmlBodyTag) : HtmlBodyTag(containingTag, "optgroup") {
}

open class TEXTAREA(containingTag: HtmlBodyTag) : HtmlBodyTag(containingTag, "textarea") {
    public var autofocus: Boolean by Attributes.autofocus
    public var cols: Int by Attributes.cols
    public var disabled: Boolean by Attributes.disabled
    public var maxlength: Int by Attributes.maxlength
    public var name: String by Attributes.name
    public var placeholder: String by Attributes.placeholder
    public var readonly: Boolean by Attributes.readonly
    public var required: Boolean by Attributes.required
    public var rows: Int by Attributes.rows
    public var wrap: Wrap by Attributes.wrap
}

inline fun HtmlBodyTag.fieldset(c: String, contents: FIELDSET.() -> Unit) = contentTag(FIELDSET(this), c, contents)
inline fun HtmlBodyTag.fieldset(contents: FIELDSET.() -> Unit) = contentTag(FIELDSET(this), contents)

inline fun FIELDSET.legend(c: String, contents: LEGEND.() -> Unit) = contentTag(LEGEND(this), c, contents)
inline fun FIELDSET.legend(contents: LEGEND.() -> Unit) = contentTag(LEGEND(this), contents)

open class LABEL(containingTag: HtmlBodyTag) : HtmlBodyTag(containingTag, "label") {
    public var forId: String by Attributes.forId
}

open class LEGEND(containingTag: FIELDSET) : HtmlBodyTag(containingTag, "legend")

inline fun HtmlBodyTag.canvas(c: String, contents: CANVAS.() -> Unit) = contentTag(CANVAS(this), c, contents)
inline fun HtmlBodyTag.canvas(contents: CANVAS.() -> Unit) = contentTag(CANVAS(this), contents)

open class CANVAS(containingTag: HtmlBodyTag) : HtmlBodyTag(containingTag, "canvas") {
    public var width: Int by Attributes.width
    public var height: Int by Attributes.height
}

open class CODE(containingTag: HtmlBodyTag) : HtmlBodyTag(containingTag, "code")
inline fun HtmlBodyTag.code(contents: CODE.() -> Unit) = contentTag(CODE(this), contents)

open class IFRAME(containingTag: HtmlBodyTag) : HtmlBodyTag(containingTag, "iframe") {
    public var src: Link by Attributes.src
}

inline fun HtmlBodyTag.iframe(contents: IFRAME.() -> Unit) = contentTag(IFRAME(this), contents)

open class GENERIC_TAG(containingTag: HtmlBodyTag, name: String) : HtmlBodyTag(containingTag, name)
inline fun HtmlBodyTag.tag(c: String, name: String, contents: HtmlBodyTag.() -> Unit) = contentTag(GENERIC_TAG(this, name), c, contents)
inline fun HtmlBodyTag.tag(name: String, contents: HtmlBodyTag.() -> Unit) = contentTag(GENERIC_TAG(this, name), contents)
