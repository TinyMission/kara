package kotlin.html

val <T> empty_contents: T.() -> Unit = { }

fun HTML.body(init: BODY.() -> Unit) = build(BODY(this), init)
class BODY(containingTag: HTML) : HtmlBodyTagWithText(containingTag, "body")

public abstract class HtmlBodyTag(containingTag: HtmlTag?, name: String, renderStyle: RenderStyle = RenderStyle.expanded, contentStyle: ContentStyle = ContentStyle.block) : HtmlTag(containingTag, name, renderStyle, contentStyle) {
    public var id: String by Attributes.id
    public var c: StyleClass by Attributes.c
    public var style: String by Attributes.style

    fun style(init: StyledElement.()->Unit) {
        val element = StyledElement("inline")
        element.init()
        val builder = StringBuilder()
        for ((k, v) in element.attributes) {
            builder.append("$k:$v;")
        }

        this["style"] = builder.toString()
    }
}


fun <T : HtmlBodyTag> HtmlBodyTag.contentTag(tag: T, c: StyleClass? = null, id: String? = null, contents: T.() -> Unit = empty_contents) {
    if (id != null) tag.id = id
    if (c != null) tag.c = c
    build(tag, contents)
}

fun HtmlBodyTag.button(c: StyleClass? = null, id: String? = null, contents: BUTTON.() -> Unit = empty_contents) = contentTag(BUTTON(this), c, id, contents)

fun HtmlBodyTag.dl(c: StyleClass? = null, id: String? = null, contents: DL.() -> Unit = empty_contents) = contentTag(DL(this), c, id, contents)
fun DL.dt(c: StyleClass? = null, id: String? = null, contents: DT.() -> Unit = empty_contents) = contentTag(DT(this), c, id, contents)
fun DL.dd(c: StyleClass? = null, id: String? = null, contents: DD.() -> Unit = empty_contents) = contentTag(DD(this), c, id, contents)

fun HtmlBodyTag.h1(c: StyleClass? = null, id: String? = null, contents: H1.() -> Unit = empty_contents) = contentTag(H1(this), c, id, contents)
fun HtmlBodyTag.h2(c: StyleClass? = null, id: String? = null, contents: H2.() -> Unit = empty_contents) = contentTag(H2(this), c, id, contents)
fun HtmlBodyTag.h3(c: StyleClass? = null, id: String? = null, contents: H3.() -> Unit = empty_contents) = contentTag(H3(this), c, id, contents)
fun HtmlBodyTag.h4(c: StyleClass? = null, id: String? = null, contents: H4.() -> Unit = empty_contents) = contentTag(H4(this), c, id, contents)
fun HtmlBodyTag.h5(c: StyleClass? = null, id: String? = null, contents: H5.() -> Unit = empty_contents) = contentTag(H5(this), c, id, contents)
fun HtmlBodyTag.img(c: StyleClass? = null, id: String? = null, contents: IMG.() -> Unit = empty_contents) = contentTag(IMG(this), c, id, contents)
fun HtmlBodyTag.input(c: StyleClass? = null, id: String? = null, contents: INPUT.() -> Unit = empty_contents) = contentTag(INPUT(this), c, id, contents)
fun HtmlBodyTag.label(c: StyleClass? = null, id: String? = null, contents: LABEL.() -> Unit = empty_contents) = contentTag(LABEL(this), c, id, contents)
fun HtmlBodyTag.select(c: StyleClass? = null, id: String? = null, contents: SELECT.() -> Unit = empty_contents) = contentTag(SELECT(this), c, id, contents)
fun HtmlBodyTag.textarea(c: StyleClass? = null, id: String? = null, contents: TEXTAREA.() -> Unit = empty_contents) = contentTag(TEXTAREA(this), c, id, contents)

fun HtmlBodyTag.a(c: StyleClass? = null, id: String? = null, contents: A.() -> Unit = empty_contents) = contentTag(A(this), c, id, contents)
open class A(containingTag: HtmlBodyTag) : HtmlBodyTagWithText(containingTag, "a", contentStyle = ContentStyle.propagate) {
    public var href: Link by Attributes.href
    public var rel: String by Attributes.rel
    public var target: String by Attributes.target
}

open class BUTTON(containingTag: HtmlBodyTag) : HtmlBodyTagWithText(containingTag, "button") {
}

fun HtmlBodyTag.br(c: StyleClass? = null, id: String? = null) = contentTag(BR(this), c, id)
fun HtmlBodyTag.div(c: StyleClass? = null, id: String? = null, contents: DIV.() -> Unit = empty_contents) = contentTag(DIV(this), c, id, contents)
fun HtmlBodyTag.b(c: StyleClass? = null, id: String? = null, contents: B.() -> Unit = empty_contents) = contentTag(B(this), c, id, contents)
fun HtmlBodyTag.i(c: StyleClass? = null, id: String? = null, contents: I.() -> Unit = empty_contents) = contentTag(I(this), c, id, contents)
fun HtmlBodyTag.p(c: StyleClass? = null, id: String? = null, contents: P.() -> Unit = empty_contents) = contentTag(P(this), c, id, contents)
fun HtmlBodyTag.span(c: StyleClass? = null, id: String? = null, contents: SPAN.() -> Unit = empty_contents) = contentTag(SPAN(this), c, id, contents)
fun HtmlBodyTag.strong(c: StyleClass? = null, id: String? = null, contents: STRONG.() -> Unit = empty_contents) = contentTag(STRONG(this), c, id, contents)
fun HtmlBodyTag.small(c: StyleClass? = null, id: String? = null, contents: SMALL.() -> Unit = empty_contents) = contentTag(SMALL(this), c, id, contents)
fun HtmlBodyTag.blockquote(c: StyleClass? = null, id: String? = null, contents: BLOCKQUOTE.() -> Unit = empty_contents) = contentTag(BLOCKQUOTE(this), c, id, contents)
fun HtmlBodyTag.address(c: StyleClass? = null, id: String? = null, contents: ADDRESS.() -> Unit = empty_contents) = contentTag(ADDRESS(this), c, id, contents)
fun HtmlBodyTag.em(c: StyleClass? = null, id: String? = null, contents: EM.() -> Unit = empty_contents) = contentTag(EM(this), c, id, contents)

open class BR(containingTag: HtmlBodyTag) : HtmlBodyTag(containingTag, "br", RenderStyle.empty) {
}
open class DIV(containingTag: HtmlBodyTag) : HtmlBodyTagWithText(containingTag, "div") {
}
open class I(containingTag: HtmlBodyTag) : HtmlBodyTagWithText(containingTag, "i", contentStyle = ContentStyle.propagate) {
}
open class B(containingTag: HtmlBodyTag) : HtmlBodyTagWithText(containingTag, "b", contentStyle = ContentStyle.propagate) {
}
open class P(containingTag: HtmlBodyTag) : HtmlBodyTagWithText(containingTag, "p") {
}
open class SPAN(containingTag: HtmlBodyTag) : HtmlBodyTagWithText(containingTag, "span", contentStyle = ContentStyle.propagate) {
}
open class STRONG(containingTag: HtmlBodyTag) : HtmlBodyTagWithText(containingTag, "strong", contentStyle = ContentStyle.propagate) {
}
open class SMALL(containingTag: HtmlBodyTag) : HtmlBodyTagWithText(containingTag, "small", contentStyle = ContentStyle.propagate) {
}
open class EM(containingTag: HtmlBodyTag) : HtmlBodyTagWithText(containingTag, "em", contentStyle = ContentStyle.propagate) {
}
open class ADDRESS(containingTag: HtmlBodyTag) : HtmlBodyTagWithText(containingTag, "address") {
}
open class BLOCKQUOTE(containingTag: HtmlBodyTag) : HtmlBodyTagWithText(containingTag, "blockquote") {
    public var cite: Link by Attributes.cite
}


open class DL(containingTag: HtmlBodyTag) : HtmlBodyTag(containingTag, "dl") {
}
open class DD(containingTag: DL) : HtmlBodyTagWithText(containingTag, "dd", contentStyle = ContentStyle.propagate) {
}
open class DT(containingTag: DL) : HtmlBodyTagWithText(containingTag, "dt", contentStyle = ContentStyle.propagate) {
}

abstract class ListTag(containingTag: HtmlBodyTag, name: String) : HtmlBodyTag(containingTag, name) {
}
open class OL(containingTag: HtmlBodyTag) : ListTag(containingTag, "ol") {
}
open class UL(containingTag: HtmlBodyTag) : ListTag(containingTag, "ul") {
}
open class LI(containingTag: ListTag) : HtmlBodyTagWithText(containingTag, "li") {
}
fun HtmlBodyTag.ul(c: StyleClass? = null, id: String? = null, contents: UL.() -> Unit = empty_contents) = contentTag(UL(this), c, id, contents)
fun HtmlBodyTag.ol(c: StyleClass? = null, id: String? = null, contents: OL.() -> Unit = empty_contents) = contentTag(OL(this), c, id, contents)
fun ListTag.li(c: StyleClass? = null, id: String? = null, contents: LI.() -> Unit = empty_contents) = contentTag(LI(this), c, id, contents)

open class H1(containingTag: HtmlBodyTag) : HtmlBodyTagWithText(containingTag, "h1") {
}
open class H2(containingTag: HtmlBodyTag) : HtmlBodyTagWithText(containingTag, "h2") {
}
open class H3(containingTag: HtmlBodyTag) : HtmlBodyTagWithText(containingTag, "h3") {
}
open class H4(containingTag: HtmlBodyTag) : HtmlBodyTagWithText(containingTag, "h4") {
}
open class H5(containingTag: HtmlBodyTag) : HtmlBodyTagWithText(containingTag, "h5") {
}
open class IMG(containingTag: HtmlBodyTag) : HtmlBodyTag(containingTag, "img", RenderStyle.empty, ContentStyle.text) {
    public var width: Int by Attributes.width
    public var height: Int by Attributes.height
    public var src: Link by Attributes.src
    public var alt: String by Attributes.alt
}
open class INPUT(containingTag: HtmlBodyTag) : HtmlBodyTag(containingTag, "input") {
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

abstract class TableTag(containingTag: HtmlBodyTag, name: String) : HtmlBodyTag(containingTag, name) {
}
open class TABLE(containingTag: HtmlBodyTag) : TableTag(containingTag, "table") {
}
open class THEAD(containingTag: TABLE) : TableTag(containingTag, "thead") {
}
open class TFOOT(containingTag: TABLE) : TableTag(containingTag, "tfoot") {
}
open class TBODY(containingTag: TABLE) : TableTag(containingTag, "tbody") {
}
open class TR(containingTag: TableTag) : HtmlBodyTag(containingTag, "tr"){
}
open class TH(containingTag: TR) : HtmlBodyTagWithText(containingTag, "th") {
}
open class TD(containingTag: TR) : HtmlBodyTagWithText(containingTag, "td") {
}

fun HtmlBodyTag.table(c: StyleClass? = null, id: String? = null, contents: TABLE.() -> Unit = empty_contents) = contentTag(TABLE(this), c, id, contents)
fun TABLE.tbody(c: StyleClass? = null, id: String? = null, contents: TBODY.() -> Unit = empty_contents) = contentTag(TBODY(this), c, id, contents)
fun TABLE.thead(c: StyleClass? = null, id: String? = null, contents: THEAD.() -> Unit = empty_contents) = contentTag(THEAD(this), c, id, contents)
fun TABLE.tfoot(c: StyleClass? = null, id: String? = null, contents: TFOOT.() -> Unit = empty_contents) = contentTag(TFOOT(this), c, id, contents)
fun TableTag.tr(c: StyleClass? = null, id: String? = null, contents: TR.() -> Unit = empty_contents) = contentTag(TR(this), c, id, contents)
fun TR.th(c: StyleClass? = null, id: String? = null, contents: TH.() -> Unit = empty_contents) = contentTag(TH(this), c, id, contents)
fun TR.td(c: StyleClass? = null, id: String? = null, contents: TD.() -> Unit = empty_contents) = contentTag(TD(this), c, id, contents)


fun HtmlBodyTag.form(c: StyleClass? = null, id: String? = null, contents: FORM.() -> Unit = empty_contents) = contentTag(FORM(this), c, id, contents)

fun SELECT.option(c: StyleClass? = null, id: String? = null, contents: OPTION.() -> Unit = empty_contents) = contentTag(OPTION(this), c, id, contents)
fun SELECT.optiongroup(c: StyleClass? = null, id: String? = null, contents: OPTGROUP.() -> Unit = empty_contents) = contentTag(OPTGROUP(this), c, id, contents)

open class FIELDSET(containingTag: HtmlBodyTag) : HtmlBodyTag(containingTag, "fieldset") {
}
public open class FORM(containingTag: HtmlBodyTag) : HtmlBodyTag(containingTag, "form") {
    public var action: Link by Attributes.action
    public var enctype: EncodingType by Attributes.enctype
    public var method: FormMethod by Attributes.method
}

open class SELECT(containingTag: HtmlBodyTag) : HtmlBodyTag(containingTag, "select") {
    public var name: String by Attributes.name
    public var size: Int by Attributes.size
    public var multiple: Boolean by Attributes.multiple
    public var disabled: Boolean by Attributes.disabled
}

open class OPTION(containingTag: HtmlBodyTag) : HtmlBodyTagWithText(containingTag, "option") {
    public var value: String by Attributes.value
    public var label: String by Attributes.label
    public var disabled: Boolean by Attributes.disabled
    public var selected: Boolean by Attributes.selected
}
open class OPTGROUP(containingTag: HtmlBodyTag) : HtmlBodyTag(containingTag, "optgroup") {
}

open class TEXTAREA(containingTag: HtmlBodyTag) : HtmlBodyTagWithText(containingTag, "textarea") {
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

fun HtmlBodyTag.fieldset(c: StyleClass? = null, id: String? = null, contents: FIELDSET.() -> Unit = empty_contents) = contentTag(FIELDSET(this), c, id, contents)
fun FIELDSET.legend(c: StyleClass? = null, id: String? = null, contents: LEGEND.() -> Unit = empty_contents) = contentTag(LEGEND(this), c, id, contents)

open class LABEL(containingTag: HtmlBodyTag) : HtmlBodyTagWithText(containingTag, "label") {
    public var forId: String by Attributes.forId
}
open class LEGEND(containingTag: FIELDSET) : HtmlBodyTagWithText(containingTag, "legend") {
}

fun HtmlBodyTag.canvas(c: StyleClass? = null, id: String? = null, contents: CANVAS.() -> Unit = empty_contents) = contentTag(CANVAS(this), c, id, contents)
open class CANVAS(containingTag: HtmlBodyTag) : HtmlBodyTag(containingTag, "canvas") {
    public var width: Int by Attributes.width
    public var height: Int by Attributes.height
}
