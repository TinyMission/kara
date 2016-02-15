package kotlinx.html

enum class BackgroundAttachment(val value: String) {
    scroll("scroll"),
    fixed("fixed"),
    inherit("inherit");
    override fun toString(): String {
        return value
    }
}
fun isBackgroundAttachment(s: String): Boolean {
    return s == "scroll" || s == "fixed"
}
fun makeBackgroundAttachment(s: String): BackgroundAttachment {
    return when (s) {
        "scroll" -> BackgroundAttachment.scroll
        "fixed" -> BackgroundAttachment.fixed
        else -> throw RuntimeException("Unknown BackgroundAttachment value $s")
    }
}

enum class BackgroundRepeat(val value: String) {
    repeat("repeat"),
    repeatX("repeat-x"),
    repeatY("repeat-y"),
    noRepeat("no-repeat"),
    inherit("inherit");
    override fun toString(): String {
        return value
    }
}
fun isBackgroundRepeat(s: String): Boolean {
    return s == "repeat" || s == "repeat-x" || s == "repeat-y" || s == "no-repeat"
}
fun makeBackgroundRepeat(s: String): BackgroundRepeat {
    return when (s) {
        "repeat" -> BackgroundRepeat.repeat
        "repeat-x" -> BackgroundRepeat.repeatX
        "repeat-y" -> BackgroundRepeat.repeatY
        "no-repeat" -> BackgroundRepeat.noRepeat
        else -> throw RuntimeException("Unknown BackgroundRepeat value $s")
    }
}

enum class BorderStyle(val value: String) {
    none("none"),
    hidden("hidden"),
    dotted("dotted"),
    dashed("dashed"),
    solid("solid"),
    double("double"),
    groove("groove"),
    ridge("ridge"),
    inset("inset"),
    outset("outset"),
    inherit("inherit");
    override fun toString(): String {
        return value
    }
}
fun isBorderStyle(s: String): Boolean {
    return s == "none" || s == "hidden" || s == "dotted" || s == "dashed" || s == "solid" || s == "double" || s == "groove" || s == "ridge" || s == "inset" || s == "outset"
}
fun makeBorderStyle(s: String): BorderStyle {
    return when (s) {
        "none" -> BorderStyle.none
        "hidden" -> BorderStyle.hidden
        "dotted" -> BorderStyle.dotted
        "dashed" -> BorderStyle.dashed
        "solid" -> BorderStyle.solid
        "double" -> BorderStyle.double
        "groove" -> BorderStyle.groove
        "ridge" -> BorderStyle.ridge
        "inset" -> BorderStyle.inset
        "outset" -> BorderStyle.outset
        else -> throw RuntimeException("Unknown BorderStyle value $s")
    }
}

enum class BoxDirection(val value: String) {
    normal("normal"),
    reverse("reverse"),
    inherit("inherit");
    override fun toString(): String {
        return value
    }
}
fun isBoxDirection(s: String): Boolean {
    return s == "normal" || s == "reverse"
}
fun makeBoxDirection(s: String): BoxDirection {
    return when (s) {
        "normal" -> BoxDirection.normal
        "reverse" -> BoxDirection.reverse
        else -> throw RuntimeException("Unknown BoxDirection value $s")
    }
}

enum class BoxAlign(val value: String) {
    start("start"),
    end("end"),
    center("center"),
    baseline("baseline"),
    inherit("inherit");
    override fun toString(): String {
        return value
    }
}
fun isBoxAlign(s: String): Boolean {
    return s == "start" || s == "end" || s == "center" || s == "baseline"
}
fun makeBoxAlign(s: String): BoxAlign {
    return when (s) {
        "start" -> BoxAlign.start
        "end" -> BoxAlign.end
        "center" -> BoxAlign.center
        "baseline" -> BoxAlign.baseline
        else -> throw RuntimeException("Unknown BoxAlign value $s")
    }
}

enum class BoxLines(val value: String) {
    single("single"),
    multiple("multiple"),
    inherit("inherit");
    override fun toString(): String {
        return value
    }
}
fun isBoxLines(s: String): Boolean {
    return s == "single" || s == "multiple"
}
fun makeBoxLines(s: String): BoxLines {
    return when (s) {
        "single" -> BoxLines.single
        "multiple" -> BoxLines.multiple
        else -> throw RuntimeException("Unknown BoxLines value $s")
    }
}

enum class BoxOrient(val value: String) {
    horizontal("horizontal"),
    vertical("vertical"),
    inlineAxis("inline-axis"),
    blockAxis("block-axis"),
    inherit("inherit");
    override fun toString(): String {
        return value
    }
}
fun isBoxOrient(s: String): Boolean {
    return s == "horizontal" || s == "vertical" || s == "inline-axis" || s == "block-axis"
}
fun makeBoxOrient(s: String): BoxOrient {
    return when (s) {
        "horizontal" -> BoxOrient.horizontal
        "vertical" -> BoxOrient.vertical
        "inline-axis" -> BoxOrient.inlineAxis
        "block-axis" -> BoxOrient.blockAxis
        else -> throw RuntimeException("Unknown BoxOrient value $s")
    }
}

enum class BoxPack(val value: String) {
    start("start"),
    end("end"),
    center("center"),
    inherit("inherit");
    override fun toString(): String {
        return value
    }
}
fun isBoxPack(s: String): Boolean {
    return s == "start" || s == "end" || s == "center"
}
fun makeBoxPack(s: String): BoxPack {
    return when (s) {
        "start" -> BoxPack.start
        "end" -> BoxPack.end
        "center" -> BoxPack.center
        else -> throw RuntimeException("Unknown BoxPack value $s")
    }
}

enum class BoxSizing(val value: String) {
    contentBox("content-box"),
    borderBox("border-box"),
    inherit("inherit");
    override fun toString(): String {
        return value
    }
}
fun isBoxSizing(s: String): Boolean {
    return s == "content-box" || s == "border-box"
}
fun makeBoxSizing(s: String): BoxSizing {
    return when (s) {
        "content-box" -> BoxSizing.contentBox
        "border-box" -> BoxSizing.borderBox
        else -> throw RuntimeException("Unknown BoxSizing value $s")
    }
}

enum class CaptionSide(val value: String) {
    top("top"),
    bottom("bottom"),
    inherit("inherit");
    override fun toString(): String {
        return value
    }
}
fun isCaptionSide(s: String): Boolean {
    return s == "top" || s == "bottom"
}
fun makeCaptionSide(s: String): CaptionSide {
    return when (s) {
        "top" -> CaptionSide.top
        "bottom" -> CaptionSide.bottom
        else -> throw RuntimeException("Unknown CaptionSide value $s")
    }
}

enum class Clear(val value: String) {
    left("left"),
    right("right"),
    both("both"),
    none("none"),
    inherit("inherit");
    override fun toString(): String {
        return value
    }
}
fun isClear(s: String): Boolean {
    return s == "left" || s == "right" || s == "both" || s == "none"
}
fun makeClear(s: String): Clear {
    return when (s) {
        "left" -> Clear.left
        "right" -> Clear.right
        "both" -> Clear.both
        "none" -> Clear.none
        else -> throw RuntimeException("Unknown Clear value $s")
    }
}

enum class ColumnFill(val value: String) {
    balance("balance"),
    auto("auto"),
    inherit("inherit");
    override fun toString(): String {
        return value
    }
}
fun isColumnFill(s: String): Boolean {
    return s == "balance" || s == "auto"
}
fun makeColumnFill(s: String): ColumnFill {
    return when (s) {
        "balance" -> ColumnFill.balance
        "auto" -> ColumnFill.auto
        else -> throw RuntimeException("Unknown ColumnFill value $s")
    }
}

enum class Direction(val value: String) {
    ltr("ltr"),
    rtl("rtl"),
    inherit("inherit");
    override fun toString(): String {
        return value
    }
}
fun isDirection(s: String): Boolean {
    return s == "ltr" || s == "rtl"
}
fun makeDirection(s: String): Direction {
    return when (s) {
        "ltr" -> Direction.ltr
        "rtl" -> Direction.rtl
        else -> throw RuntimeException("Unknown Direction value $s")
    }
}

enum class Display(val value: String) {
    none("none"),
    block("block"),
    `inline`("inline"),
    inlineBlock("inline-block"),
    inlineTable("inline-table"),
    listItem("list-item"),
    table("table"),
    tableCaption("table-caption"),
    tableCell("table-cell"),
    tableColumn("table-column"),
    tableColumnGroup("table-column-group"),
    tableFooterGroup("table-footer-group"),
    tableHeaderGroup("table-header-group"),
    tableRow("table-row"),
    tableRowGroup("table-row-group"),
    inherit("inherit");
    override fun toString(): String {
        return value
    }
}
fun isDisplay(s: String): Boolean {
    return s == "none" || s == "block" || s == "inline" || s == "inline-block" || s == "inline-table" || s == "list-item" || s == "table" || s == "table-caption" || s == "table-cell" || s == "table-column" || s == "table-column-group" || s == "table-footer-group" || s == "table-header-group" || s == "table-row" || s == "table-row-group"
}
fun makeDisplay(s: String): Display {
    return when (s) {
        "none" -> Display.none
        "block" -> Display.block
        "inline" -> Display.inline
        "inline-block" -> Display.inlineBlock
        "inline-table" -> Display.inlineTable
        "list-item" -> Display.listItem
        "table" -> Display.table
        "table-caption" -> Display.tableCaption
        "table-cell" -> Display.tableCell
        "table-column" -> Display.tableColumn
        "table-column-group" -> Display.tableColumnGroup
        "table-footer-group" -> Display.tableFooterGroup
        "table-header-group" -> Display.tableHeaderGroup
        "table-row" -> Display.tableRow
        "table-row-group" -> Display.tableRowGroup
        else -> throw RuntimeException("Unknown Display value $s")
    }
}

enum class EmptyCells(val value: String) {
    hide("hide"),
    show("show"),
    inherit("inherit");
    override fun toString(): String {
        return value
    }
}
fun isEmptyCells(s: String): Boolean {
    return s == "hide" || s == "show"
}
fun makeEmptyCells(s: String): EmptyCells {
    return when (s) {
        "hide" -> EmptyCells.hide
        "show" -> EmptyCells.show
        else -> throw RuntimeException("Unknown EmptyCells value $s")
    }
}

enum class FloatType(val value: String) {
    left("left"),
    right("right"),
    none("none"),
    inherit("inherit");
    override fun toString(): String {
        return value
    }
}
fun isFloat(s: String): Boolean {
    return s == "left" || s == "right" || s == "none"
}
fun makeFloat(s: String): FloatType {
    return when (s) {
        "left" -> FloatType.left
        "right" -> FloatType.right
        "none" -> FloatType.none
        else -> throw RuntimeException("Unknown Float value $s")
    }
}

enum class FontStyle(val value: String) {
    normal("normal"),
    italic("italic"),
    oblique("oblique"),
    inherit("inherit");
    override fun toString(): String {
        return value
    }
}
fun isFontStyle(s: String): Boolean {
    return s == "normal" || s == "italic" || s == "oblique"
}
fun makeFontStyle(s: String): FontStyle {
    return when (s) {
        "normal" -> FontStyle.normal
        "italic" -> FontStyle.italic
        "oblique" -> FontStyle.oblique
        else -> throw RuntimeException("Unknown FontStyle value $s")
    }
}

enum class FontVariant(val value: String) {
    normal("normal"),
    smallCaps("small-caps"),
    inherit("inherit");
    override fun toString(): String {
        return value
    }
}
fun isFontVariant(s: String): Boolean {
    return s == "normal" || s == "small-caps"
}
fun makeFontVariant(s: String): FontVariant {
    return when (s) {
        "normal" -> FontVariant.normal
        "small-caps" -> FontVariant.smallCaps
        else -> throw RuntimeException("Unknown FontVariant value $s")
    }
}

enum class FontWeight(val value: String) {
    normal("normal"),
    bold("bold"),
    bolder("bolder"),
    lighter("lighter"),
    inherit("inherit");
    override fun toString(): String {
        return value
    }
}
fun isFontWeight(s: String): Boolean {
    return s == "normal" || s == "bold" || s == "bolder" || s == "lighter"
}
fun makeFontWeight(s: String): FontWeight {
    return when (s) {
        "normal" -> FontWeight.normal
        "bold" -> FontWeight.bold
        "bolder" -> FontWeight.bolder
        "lighter" -> FontWeight.lighter
        else -> throw RuntimeException("Unknown FontWeight value $s")
    }
}

enum class FontStretch(val value: String) {
    wider("wider"),
    narrower("narrower"),
    ultraCondensed("ultra-condensed"),
    extraCondensed("extra-condensed"),
    condensed("condensed"),
    semiCondensed("semi-condensed"),
    normal("normal"),
    semiExpanded("semi-expanded"),
    expanded("expanded"),
    extraExpanded("extra-expanded"),
    ultraExpanded("ultra-expanded"),
    inherit("inherit");
    override fun toString(): String {
        return value
    }
}
fun isFontStretch(s: String): Boolean {
    return s == "wider" || s == "narrower" || s == "ultra-condensed" || s == "extra-condensed" || s == "condensed" || s == "semi-condensed" || s == "normal" || s == "semi-expanded" || s == "expanded" || s == "extra-expanded" || s == "ultra-expanded"
}
fun makeFontStretch(s: String): FontStretch {
    return when (s) {
        "wider" -> FontStretch.wider
        "narrower" -> FontStretch.narrower
        "ultra-condensed" -> FontStretch.ultraCondensed
        "extra-condensed" -> FontStretch.extraCondensed
        "condensed" -> FontStretch.condensed
        "semi-condensed" -> FontStretch.semiCondensed
        "normal" -> FontStretch.normal
        "semi-expanded" -> FontStretch.semiExpanded
        "expanded" -> FontStretch.expanded
        "extra-expanded" -> FontStretch.extraExpanded
        "ultra-expanded" -> FontStretch.ultraExpanded
        else -> throw RuntimeException("Unknown FontStretch value $s")
    }
}

enum class ListStyleType(val value: String) {
    circle("circle"),
    disc("disc"),
    decimal("decimal"),
    lowerAlpha("lower-alpha"),
    lowerGreek("lower-greek"),
    lowerLatin("lower-latin"),
    lowerRoman("lower-roman"),
    none("none"),
    square("square"),
    upperAlpha("upper-alpha"),
    upper("upper"),
    latin("latin"),
    upperRoman("upper-roman"),
    inherit("inherit");
    override fun toString(): String {
        return value
    }
}
fun isListStyleType(s: String): Boolean {
    return s == "circle" || s == "disc" || s == "decimal" || s == "lower-alpha" || s == "lower-greek" || s == "lower-latin" || s == "lower-roman" || s == "none" || s == "square" || s == "upper-alpha" || s == "upper" || s == "latin" || s == "upper-roman"
}
fun makeListStyleType(s: String): ListStyleType {
    return when (s) {
        "circle" -> ListStyleType.circle
        "disc" -> ListStyleType.disc
        "decimal" -> ListStyleType.decimal
        "lower-alpha" -> ListStyleType.lowerAlpha
        "lower-greek" -> ListStyleType.lowerGreek
        "lower-latin" -> ListStyleType.lowerLatin
        "lower-roman" -> ListStyleType.lowerRoman
        "none" -> ListStyleType.none
        "square" -> ListStyleType.square
        "upper-alpha" -> ListStyleType.upperAlpha
        "upper" -> ListStyleType.upper
        "latin" -> ListStyleType.latin
        "upper-roman" -> ListStyleType.upperRoman
        else -> throw RuntimeException("Unknown ListStyleType value $s")
    }
}

enum class ListStylePosition(val value: String) {
    inside("inside"),
    outside("outside"),
    inherit("inherit");
    override fun toString(): String {
        return value
    }
}
fun isListStylePosition(s: String): Boolean {
    return s == "inside" || s == "outside"
}
fun makeListStylePosition(s: String): ListStylePosition {
    return when (s) {
        "inside" -> ListStylePosition.inside
        "outside" -> ListStylePosition.outside
        else -> throw RuntimeException("Unknown ListStylePosition value $s")
    }
}

enum class Overflow(val value: String) {
    visible("visible"),
    hidden("hidden"),
    scroll("scroll"),
    auto("auto"),
    noDisplay("no-display"),
    noContent("no-content"),
    inherit("inherit");
    override fun toString(): String {
        return value
    }
}
fun isOverflow(s: String): Boolean {
    return s == "visible" || s == "hidden" || s == "scroll" || s == "auto" || s == "no-display" || s == "no-content"
}
fun makeOverflow(s: String): Overflow {
    return when (s) {
        "visible" -> Overflow.visible
        "hidden" -> Overflow.hidden
        "scroll" -> Overflow.scroll
        "auto" -> Overflow.auto
        "no-display" -> Overflow.noDisplay
        "no-content" -> Overflow.noContent
        else -> throw RuntimeException("Unknown Overflow value $s")
    }
}

enum class PageBreak(val value: String) {
    auto("auto"),
    always("always"),
    avoid("avoid"),
    left("left"),
    right("right"),
    inherit("inherit");
    override fun toString(): String {
        return value
    }
}
fun isPageBreak(s: String): Boolean {
    return s == "auto" || s == "always" || s == "avoid" || s == "left" || s == "right"
}
fun makePageBreak(s: String): PageBreak {
    return when (s) {
        "auto" -> PageBreak.auto
        "always" -> PageBreak.always
        "avoid" -> PageBreak.avoid
        "left" -> PageBreak.left
        "right" -> PageBreak.right
        else -> throw RuntimeException("Unknown PageBreak value $s")
    }
}

enum class Position(val value: String) {
    static("static"),
    absolute("absolute"),
    fixed("fixed"),
    relative("relative"),
    inherit("inherit");
    override fun toString(): String {
        return value
    }
}
fun isPosition(s: String): Boolean {
    return s == "static" || s == "absolute" || s == "fixed" || s == "relative"
}
fun makePosition(s: String): Position {
    return when (s) {
        "static" -> Position.static
        "absolute" -> Position.absolute
        "fixed" -> Position.fixed
        "relative" -> Position.relative
        else -> throw RuntimeException("Unknown Position value $s")
    }
}

enum class Resize(val value: String) {
    none("none"),
    both("both"),
    horizontal("horizontal"),
    vertical("vertical"),
    inherit("inherit");
    override fun toString(): String {
        return value
    }
}
fun isResize(s: String): Boolean {
    return s == "none" || s == "both" || s == "horizontal" || s == "vertical"
}
fun makeResize(s: String): Resize {
    return when (s) {
        "none" -> Resize.none
        "both" -> Resize.both
        "horizontal" -> Resize.horizontal
        "vertical" -> Resize.vertical
        else -> throw RuntimeException("Unknown Resize value $s")
    }
}

enum class TableLayout(val value: String) {
    auto("auto"),
    fixed("fixed"),
    inherit("inherit");
    override fun toString(): String {
        return value
    }
}
fun isTableLayout(s: String): Boolean {
    return s == "auto" || s == "fixed"
}
fun makeTableLayout(s: String): TableLayout {
    return when (s) {
        "auto" -> TableLayout.auto
        "fixed" -> TableLayout.fixed
        else -> throw RuntimeException("Unknown TableLayout value $s")
    }
}

enum class TextAlign(val value: String) {
    left("left"),
    right("right"),
    center("center"),
    justify("justify"),
    inherit("inherit");
    override fun toString(): String {
        return value
    }
}
fun isTextAlign(s: String): Boolean {
    return s == "left" || s == "right" || s == "center" || s == "justify"
}
fun makeTextAlign(s: String): TextAlign {
    return when (s) {
        "left" -> TextAlign.left
        "right" -> TextAlign.right
        "center" -> TextAlign.center
        "justify" -> TextAlign.justify
        else -> throw RuntimeException("Unknown TextAlign value $s")
    }
}

enum class VerticalAlign(val value: String) {
    top("top"),
    bottom("bottom"),
    middle("middle"),
    inherit("inherit");
    override fun toString(): String {
        return value
    }
}
fun isVerticalAlign(s: String): Boolean {
    return s == "top" || s == "bottom" || s == "middle"
}
fun makeVerticalAlign(s: String): VerticalAlign {
    return when (s) {
        "top" -> VerticalAlign.top
        "bottom" -> VerticalAlign.bottom
        "middle" -> VerticalAlign.middle
        else -> throw RuntimeException("Unknown VerticalAlign value $s")
    }
}

enum class Visibility(val value: String) {
    visible("visible"),
    hidden("hidden"),
    collapse("collapse"),
    inherit("inherit");
    override fun toString(): String {
        return value
    }
}
fun isVisibility(s: String): Boolean {
    return s == "visible" || s == "hidden" || s == "collapse"
}
fun makeVisibility(s: String): Visibility {
    return when (s) {
        "visible" -> Visibility.visible
        "hidden" -> Visibility.hidden
        "collapse" -> Visibility.collapse
        else -> throw RuntimeException("Unknown Visibility value $s")
    }
}

enum class WhiteSpace(val value: String) {
    normal("normal"),
    nowrap("nowrap"),
    pre("pre"),
    preLine("pre-line"),
    preWrap("pre-wrap"),
    inherit("inherit");
    override fun toString(): String {
        return value
    }
}
fun isWhiteSpace(s: String): Boolean {
    return s == "normal" || s == "nowrap" || s == "pre" || s == "pre-line" || s == "pre-wrap"
}
fun makeWhiteSpace(s: String): WhiteSpace {
    return when (s) {
        "normal" -> WhiteSpace.normal
        "nowrap" -> WhiteSpace.nowrap
        "pre" -> WhiteSpace.pre
        "pre-line" -> WhiteSpace.preLine
        "pre-wrap" -> WhiteSpace.preWrap
        else -> throw RuntimeException("Unknown WhiteSpace value $s")
    }
}

enum class WordBreak(val value: String) {
    normal("normal"),
    breakAll("break-all"),
    hyphenate("hyphenate"),
    inherit("inherit");
    override fun toString(): String {
        return value
    }
}
fun isWordBreak(s: String): Boolean {
    return s == "normal" || s == "break-all" || s == "hyphenate"
}
fun makeWordBreak(s: String): WordBreak {
    return when (s) {
        "normal" -> WordBreak.normal
        "break-all" -> WordBreak.breakAll
        "hyphenate" -> WordBreak.hyphenate
        else -> throw RuntimeException("Unknown WordBreak value $s")
    }
}

enum class WordWrap(val value: String) {
    normal("normal"),
    breakWord("break-word"),
    inherit("inherit");
    override fun toString(): String {
        return value
    }
}
fun isWordWrap(s: String): Boolean {
    return s == "normal" || s == "break-word"
}
fun makeWordWrap(s: String): WordWrap {
    return when (s) {
        "normal" -> WordWrap.normal
        "break-word" -> WordWrap.breakWord
        else -> throw RuntimeException("Unknown WordWrap value $s")
    }
}

