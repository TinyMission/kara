package kara.views

import java.util.*


abstract class BodyTag(name : String, isEmpty : Boolean) : TagWithText(name, isEmpty) {
    // attributes
    public var id : String
        get() = attributes["id"]!!
        set(value) {
            attributes["id"] = value
        }
    public var c : String
        get() = attributes["class"]!!
        set(value) {
            attributes["class"] = value
        }
    public var style : String
        get() = attributes["style"]!!
        set(value) {
            attributes["style"] = value
        }
    public var title : String
        get() = attributes["title"]!!
        set(value) {
            attributes["title"] = value
        }

    // tags
	fun a(text : String = "", id : String = "", c : String = "", style : String = "", title : String = "", href : String = "", rel : String = "", target : String = "", init : A.() -> Unit = {}) {
		val tag = initTag(A(), init)
		tag.id = id
		tag.c = c
		tag.style = style
		tag.title = title
		tag.href = href
		tag.rel = rel
		tag.target = target
		if (tag.children.size == 0) {
			tag.text = text
		}
	}

	fun b(text : String = "", id : String = "", c : String = "", style : String = "", title : String = "", init : B.() -> Unit = {}) {
		val tag = initTag(B(), init)
		tag.id = id
		tag.c = c
		tag.style = style
		tag.title = title
		if (tag.children.size == 0) {
			tag.text = text
		}
	}

	fun button(text : String = "", id : String = "", c : String = "", style : String = "", title : String = "", init : BUTTON.() -> Unit = {}) {
		val tag = initTag(BUTTON(), init)
		tag.id = id
		tag.c = c
		tag.style = style
		tag.title = title
		if (tag.children.size == 0) {
			tag.text = text
		}
	}

	fun canvas(text : String = "", id : String = "", c : String = "", style : String = "", title : String = "", width : String = "", height : String = "", init : CANVAS.() -> Unit = {}) {
		val tag = initTag(CANVAS(), init)
		tag.id = id
		tag.c = c
		tag.style = style
		tag.title = title
		tag.width = width
		tag.height = height
		if (tag.children.size == 0) {
			tag.text = text
		}
	}

	fun div(text : String = "", id : String = "", c : String = "", style : String = "", title : String = "", init : DIV.() -> Unit = {}) {
		val tag = initTag(DIV(), init)
		tag.id = id
		tag.c = c
		tag.style = style
		tag.title = title
		if (tag.children.size == 0) {
			tag.text = text
		}
	}

	fun em(text : String = "", id : String = "", c : String = "", style : String = "", title : String = "", init : EM.() -> Unit = {}) {
		val tag = initTag(EM(), init)
		tag.id = id
		tag.c = c
		tag.style = style
		tag.title = title
		if (tag.children.size == 0) {
			tag.text = text
		}
	}

	fun fieldset(text : String = "", id : String = "", c : String = "", style : String = "", title : String = "", init : FIELDSET.() -> Unit = {}) {
		val tag = initTag(FIELDSET(), init)
		tag.id = id
		tag.c = c
		tag.style = style
		tag.title = title
		if (tag.children.size == 0) {
			tag.text = text
		}
	}

	fun form(text : String = "", id : String = "", c : String = "", style : String = "", title : String = "", action : String = "", enctype : String = "", method : String = "", init : FORM.() -> Unit = {}) {
		val tag = initTag(FORM(), init)
		tag.id = id
		tag.c = c
		tag.style = style
		tag.title = title
		tag.action = action
		tag.enctype = enctype
		tag.method = method
		if (tag.children.size == 0) {
			tag.text = text
		}
	}

	fun h1(text : String = "", id : String = "", c : String = "", style : String = "", title : String = "", init : H1.() -> Unit = {}) {
		val tag = initTag(H1(), init)
		tag.id = id
		tag.c = c
		tag.style = style
		tag.title = title
		if (tag.children.size == 0) {
			tag.text = text
		}
	}

	fun h2(text : String = "", id : String = "", c : String = "", style : String = "", title : String = "", init : H2.() -> Unit = {}) {
		val tag = initTag(H2(), init)
		tag.id = id
		tag.c = c
		tag.style = style
		tag.title = title
		if (tag.children.size == 0) {
			tag.text = text
		}
	}

	fun h3(text : String = "", id : String = "", c : String = "", style : String = "", title : String = "", init : H3.() -> Unit = {}) {
		val tag = initTag(H3(), init)
		tag.id = id
		tag.c = c
		tag.style = style
		tag.title = title
		if (tag.children.size == 0) {
			tag.text = text
		}
	}

	fun h4(text : String = "", id : String = "", c : String = "", style : String = "", title : String = "", init : H4.() -> Unit = {}) {
		val tag = initTag(H4(), init)
		tag.id = id
		tag.c = c
		tag.style = style
		tag.title = title
		if (tag.children.size == 0) {
			tag.text = text
		}
	}

	fun h5(text : String = "", id : String = "", c : String = "", style : String = "", title : String = "", init : H5.() -> Unit = {}) {
		val tag = initTag(H5(), init)
		tag.id = id
		tag.c = c
		tag.style = style
		tag.title = title
		if (tag.children.size == 0) {
			tag.text = text
		}
	}

	fun img(text : String = "", id : String = "", c : String = "", style : String = "", title : String = "", width : String = "", height : String = "", src : String = "", alt : String = "", init : IMG.() -> Unit = {}) {
		val tag = initTag(IMG(), {})
		tag.id = id
		tag.c = c
		tag.style = style
		tag.title = title
		tag.width = width
		tag.height = height
		tag.src = src
		tag.alt = alt
		if (tag.children.size == 0) {
			tag.text = text
		}
	}

	fun input(text : String = "", id : String = "", c : String = "", style : String = "", title : String = "", accept : String = "", alt : String = "", autocomplete : String = "", autofocus : String = "", checked : String = "", disabled : String = "", height : String = "", list : String = "", max : String = "", maxlength : String = "", min : String = "", multiple : String = "", inputType : String = "", name : String = "", pattern : String = "", placeholder : String = "", readonly : String = "", required : String = "", size : String = "", src : String = "", step : String = "", value : String = "", width : String = "", init : INPUT.() -> Unit = {}) {
		val tag = initTag(INPUT(), {})
		tag.id = id
		tag.c = c
		tag.style = style
		tag.title = title
		tag.accept = accept
		tag.alt = alt
		tag.autocomplete = autocomplete
		tag.autofocus = autofocus
		tag.checked = checked
		tag.disabled = disabled
		tag.height = height
		tag.list = list
		tag.max = max
		tag.maxlength = maxlength
		tag.min = min
		tag.multiple = multiple
		tag.inputType = inputType
		tag.name = name
		tag.pattern = pattern
		tag.placeholder = placeholder
		tag.readonly = readonly
		tag.required = required
		tag.size = size
		tag.src = src
		tag.step = step
		tag.value = value
		tag.width = width
		if (tag.children.size == 0) {
			tag.text = text
		}
	}

	fun label(text : String = "", id : String = "", c : String = "", style : String = "", title : String = "", forId : String = "", init : LABEL.() -> Unit = {}) {
		val tag = initTag(LABEL(), init)
		tag.id = id
		tag.c = c
		tag.style = style
		tag.title = title
		tag.forId = forId
		if (tag.children.size == 0) {
			tag.text = text
		}
	}

	fun ol(text : String = "", id : String = "", c : String = "", style : String = "", title : String = "", init : OL.() -> Unit = {}) {
		val tag = initTag(OL(), init)
		tag.id = id
		tag.c = c
		tag.style = style
		tag.title = title
		if (tag.children.size == 0) {
			tag.text = text
		}
	}

	fun p(text : String = "", id : String = "", c : String = "", style : String = "", title : String = "", init : P.() -> Unit = {}) {
		val tag = initTag(P(), init)
		tag.id = id
		tag.c = c
		tag.style = style
		tag.title = title
		if (tag.children.size == 0) {
			tag.text = text
		}
	}

	fun select(text : String = "", id : String = "", c : String = "", style : String = "", title : String = "", name : String = "", size : String = "", multiple : String = "", disabled : String = "", init : SELECT.() -> Unit = {}) {
		val tag = initTag(SELECT(), init)
		tag.id = id
		tag.c = c
		tag.style = style
		tag.title = title
		tag.name = name
		tag.size = size
		tag.multiple = multiple
		tag.disabled = disabled
		if (tag.children.size == 0) {
			tag.text = text
		}
	}

	fun span(text : String = "", id : String = "", c : String = "", style : String = "", title : String = "", init : SPAN.() -> Unit = {}) {
		val tag = initTag(SPAN(), init)
		tag.id = id
		tag.c = c
		tag.style = style
		tag.title = title
		if (tag.children.size == 0) {
			tag.text = text
		}
	}

	fun strong(text : String = "", id : String = "", c : String = "", style : String = "", title : String = "", init : STRONG.() -> Unit = {}) {
		val tag = initTag(STRONG(), init)
		tag.id = id
		tag.c = c
		tag.style = style
		tag.title = title
		if (tag.children.size == 0) {
			tag.text = text
		}
	}

	fun table(text : String = "", id : String = "", c : String = "", style : String = "", title : String = "", init : TABLE.() -> Unit = {}) {
		val tag = initTag(TABLE(), init)
		tag.id = id
		tag.c = c
		tag.style = style
		tag.title = title
		if (tag.children.size == 0) {
			tag.text = text
		}
	}

	fun textarea(text : String = "", id : String = "", c : String = "", style : String = "", title : String = "", autofocus : String = "", cols : String = "", disabled : String = "", maxlength : String = "", name : String = "", placeholder : String = "", readonly : String = "", required : String = "", rows : String = "", wrap : String = "", init : TEXTAREA.() -> Unit = {}) {
		val tag = initTag(TEXTAREA(), init)
		tag.id = id
		tag.c = c
		tag.style = style
		tag.title = title
		tag.autofocus = autofocus
		tag.cols = cols
		tag.disabled = disabled
		tag.maxlength = maxlength
		tag.name = name
		tag.placeholder = placeholder
		tag.readonly = readonly
		tag.required = required
		tag.rows = rows
		tag.wrap = wrap
		if (tag.children.size == 0) {
			tag.text = text
		}
	}

	fun ul(text : String = "", id : String = "", c : String = "", style : String = "", title : String = "", init : UL.() -> Unit = {}) {
		val tag = initTag(UL(), init)
		tag.id = id
		tag.c = c
		tag.style = style
		tag.title = title
		if (tag.children.size == 0) {
			tag.text = text
		}
	}


    // view rendering
    fun renderView(context : ActionContext, view : HtmlView) {
        view.render(context)
		for (tag in view.children) {
			children.add(tag)
		}
    }
}

class Body() : BodyTag("body", false)

open class A() : BodyTag("a", false) {
	public var href : String
		get() = attributes["href"]!!
		set(value) {
			attributes["href"] = value
		}
	public var rel : String
		get() = attributes["rel"]!!
		set(value) {
			attributes["rel"] = value
		}
	public var target : String
		get() = attributes["target"]!!
		set(value) {
			attributes["target"] = value
		}
}
open class B() : BodyTag("b", false) {
}
open class BUTTON() : BodyTag("button", false) {
}
open class CANVAS() : BodyTag("canvas", false) {
	public var width : String
		get() = attributes["width"]!!
		set(value) {
			attributes["width"] = value
		}
	public var height : String
		get() = attributes["height"]!!
		set(value) {
			attributes["height"] = value
		}
}
open class DIV() : BodyTag("div", false) {
}
open class EM() : BodyTag("em", false) {
}
open class FIELDSET() : BodyTag("fieldset", false) {
}
open class FORM() : BodyTag("form", false) {
	public var action : String
		get() = attributes["action"]!!
		set(value) {
			attributes["action"] = value
		}
	public var enctype : String
		get() = attributes["enctype"]!!
		set(value) {
			attributes["enctype"] = value
		}
	public var method : String
		get() = attributes["method"]!!
		set(value) {
			attributes["method"] = value
		}
}
open class H1() : BodyTag("h1", false) {
}
open class H2() : BodyTag("h2", false) {
}
open class H3() : BodyTag("h3", false) {
}
open class H4() : BodyTag("h4", false) {
}
open class H5() : BodyTag("h5", false) {
}
open class IMG() : BodyTag("img", true) {
	public var width : String
		get() = attributes["width"]!!
		set(value) {
			attributes["width"] = value
		}
	public var height : String
		get() = attributes["height"]!!
		set(value) {
			attributes["height"] = value
		}
	public var src : String
		get() = attributes["src"]!!
		set(value) {
			attributes["src"] = value
		}
	public var alt : String
		get() = attributes["alt"]!!
		set(value) {
			attributes["alt"] = value
		}
}
open class INPUT() : BodyTag("input", true) {
	public var accept : String
		get() = attributes["accept"]!!
		set(value) {
			attributes["accept"] = value
		}
	public var alt : String
		get() = attributes["alt"]!!
		set(value) {
			attributes["alt"] = value
		}
	public var autocomplete : String
		get() = attributes["autocomplete"]!!
		set(value) {
			attributes["autocomplete"] = value
		}
	public var autofocus : String
		get() = attributes["autofocus"]!!
		set(value) {
			attributes["autofocus"] = value
		}
	public var checked : String
		get() = attributes["checked"]!!
		set(value) {
			attributes["checked"] = value
		}
	public var disabled : String
		get() = attributes["disabled"]!!
		set(value) {
			attributes["disabled"] = value
		}
	public var height : String
		get() = attributes["height"]!!
		set(value) {
			attributes["height"] = value
		}
	public var list : String
		get() = attributes["list"]!!
		set(value) {
			attributes["list"] = value
		}
	public var max : String
		get() = attributes["max"]!!
		set(value) {
			attributes["max"] = value
		}
	public var maxlength : String
		get() = attributes["maxlength"]!!
		set(value) {
			attributes["maxlength"] = value
		}
	public var min : String
		get() = attributes["min"]!!
		set(value) {
			attributes["min"] = value
		}
	public var multiple : String
		get() = attributes["multiple"]!!
		set(value) {
			attributes["multiple"] = value
		}
	public var inputType : String
		get() = attributes["type"]!!
		set(value) {
			attributes["type"] = value
		}
	public var name : String
		get() = attributes["name"]!!
		set(value) {
			attributes["name"] = value
		}
	public var pattern : String
		get() = attributes["pattern"]!!
		set(value) {
			attributes["pattern"] = value
		}
	public var placeholder : String
		get() = attributes["placeholder"]!!
		set(value) {
			attributes["placeholder"] = value
		}
	public var readonly : String
		get() = attributes["readonly"]!!
		set(value) {
			attributes["readonly"] = value
		}
	public var required : String
		get() = attributes["required"]!!
		set(value) {
			attributes["required"] = value
		}
	public var size : String
		get() = attributes["size"]!!
		set(value) {
			attributes["size"] = value
		}
	public var src : String
		get() = attributes["src"]!!
		set(value) {
			attributes["src"] = value
		}
	public var step : String
		get() = attributes["step"]!!
		set(value) {
			attributes["step"] = value
		}
	public var value : String
		get() = attributes["value"]!!
		set(value) {
			attributes["value"] = value
		}
	public var width : String
		get() = attributes["width"]!!
		set(value) {
			attributes["width"] = value
		}
}
open class LABEL() : BodyTag("label", false) {
	public var forId : String
		get() = attributes["for"]!!
		set(value) {
			attributes["for"] = value
		}
}
open class OL() : BodyTag("ol", false) {
	fun li(text : String = "", id : String = "", c : String = "", style : String = "", title : String = "", init : LI.() -> Unit = {}) {
		val tag = initTag(LI(), init)
		tag.id = id
		tag.c = c
		tag.style = style
		tag.title = title
		if (tag.children.size == 0) {
			tag.text = text
		}
	}

}
open class P() : BodyTag("p", false) {
}
open class SELECT() : BodyTag("select", false) {
	public var name : String
		get() = attributes["name"]!!
		set(value) {
			attributes["name"] = value
		}
	public var size : String
		get() = attributes["size"]!!
		set(value) {
			attributes["size"] = value
		}
	public var multiple : String
		get() = attributes["multiple"]!!
		set(value) {
			attributes["multiple"] = value
		}
	public var disabled : String
		get() = attributes["disabled"]!!
		set(value) {
			attributes["disabled"] = value
		}
	fun option(text : String = "", id : String = "", c : String = "", style : String = "", title : String = "", label : String = "", disabled : String = "", init : OPTION.() -> Unit = {}) {
		val tag = initTag(OPTION(), init)
		tag.id = id
		tag.c = c
		tag.style = style
		tag.title = title
		tag.label = label
		tag.disabled = disabled
		if (tag.children.size == 0) {
			tag.text = text
		}
	}

	fun optgroup(text : String = "", id : String = "", c : String = "", style : String = "", title : String = "", init : OPTGROUP.() -> Unit = {}) {
		val tag = initTag(OPTGROUP(), init)
		tag.id = id
		tag.c = c
		tag.style = style
		tag.title = title
		if (tag.children.size == 0) {
			tag.text = text
		}
	}

}
open class SPAN() : BodyTag("span", false) {
}
open class STRONG() : BodyTag("strong", false) {
}
open class TABLE() : BodyTag("table", false) {
	fun tr(text : String = "", id : String = "", c : String = "", style : String = "", title : String = "", init : TR.() -> Unit = {}) {
		val tag = initTag(TR(), init)
		tag.id = id
		tag.c = c
		tag.style = style
		tag.title = title
		if (tag.children.size == 0) {
			tag.text = text
		}
	}

	fun tbody(text : String = "", id : String = "", c : String = "", style : String = "", title : String = "", init : TBODY.() -> Unit = {}) {
		val tag = initTag(TBODY(), init)
		tag.id = id
		tag.c = c
		tag.style = style
		tag.title = title
		if (tag.children.size == 0) {
			tag.text = text
		}
	}

}
open class TEXTAREA() : BodyTag("textarea", false) {
	public var autofocus : String
		get() = attributes["autofocus"]!!
		set(value) {
			attributes["autofocus"] = value
		}
	public var cols : String
		get() = attributes["cols"]!!
		set(value) {
			attributes["cols"] = value
		}
	public var disabled : String
		get() = attributes["disabled"]!!
		set(value) {
			attributes["disabled"] = value
		}
	public var maxlength : String
		get() = attributes["maxlength"]!!
		set(value) {
			attributes["maxlength"] = value
		}
	public var name : String
		get() = attributes["name"]!!
		set(value) {
			attributes["name"] = value
		}
	public var placeholder : String
		get() = attributes["placeholder"]!!
		set(value) {
			attributes["placeholder"] = value
		}
	public var readonly : String
		get() = attributes["readonly"]!!
		set(value) {
			attributes["readonly"] = value
		}
	public var required : String
		get() = attributes["required"]!!
		set(value) {
			attributes["required"] = value
		}
	public var rows : String
		get() = attributes["rows"]!!
		set(value) {
			attributes["rows"] = value
		}
	public var wrap : String
		get() = attributes["wrap"]!!
		set(value) {
			attributes["wrap"] = value
		}
}
open class UL() : BodyTag("ul", false) {
	fun li(text : String = "", id : String = "", c : String = "", style : String = "", title : String = "", init : LI.() -> Unit = {}) {
		val tag = initTag(LI(), init)
		tag.id = id
		tag.c = c
		tag.style = style
		tag.title = title
		if (tag.children.size == 0) {
			tag.text = text
		}
	}

}
open class LI() : BodyTag("li", false) {
}
open class OPTION() : BodyTag("option", false) {
	public var label : String
		get() = attributes["label"]!!
		set(value) {
			attributes["label"] = value
		}
	public var disabled : String
		get() = attributes["disabled"]!!
		set(value) {
			attributes["disabled"] = value
		}
}
open class OPTGROUP() : BodyTag("optgroup", false) {
}
open class TR() : BodyTag("tr", false) {
	fun td(text : String = "", id : String = "", c : String = "", style : String = "", title : String = "", init : TD.() -> Unit = {}) {
		val tag = initTag(TD(), init)
		tag.id = id
		tag.c = c
		tag.style = style
		tag.title = title
		if (tag.children.size == 0) {
			tag.text = text
		}
	}

	fun th(text : String = "", id : String = "", c : String = "", style : String = "", title : String = "", init : TH.() -> Unit = {}) {
		val tag = initTag(TH(), init)
		tag.id = id
		tag.c = c
		tag.style = style
		tag.title = title
		if (tag.children.size == 0) {
			tag.text = text
		}
	}

}
open class TBODY() : BodyTag("tbody", false) {
}
open class TD() : BodyTag("td", false) {
}
open class TH() : BodyTag("th", false) {
}


// standalone tags
//fun html(init : HTML.() -> Unit) : HTML {
//	val tag = HTML()
//	tag.init()
//	return tag
//}
//fun div(init : DIV.() -> Unit) : DIV {
//	val tag = DIV()
//	tag.init()
//	return tag
//}
//fun span(init : SPAN.() -> Unit) : SPAN {
//	val tag = SPAN()
//	tag.init()
//	return tag
//}
