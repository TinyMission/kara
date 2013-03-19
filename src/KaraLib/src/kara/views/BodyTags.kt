package kara.views

import java.util.*
import kara.views.Attributes
import kara.styles.StyleClass
import kara.controllers.Request
import kara.views.EncodingType
import kara.views.FormMethod
import kara.views.InputType
import kara.views.Wrap
import kara.controllers.Link
import kara.styles.StyledElement

val <T> empty_init : T.() -> Unit = {}

abstract class BodyTag(name : String, isEmpty : Boolean) : TagWithText(name, isEmpty) {
    // attributes
    public var id : String
        get() = this[Attributes.id]
        set(value) {
            this[Attributes.id] = value
        }
    public var c : StyleClass
        get() = this[Attributes.c]
        set(value) {
            this[Attributes.c] = value
        }
    public var style : String
        get() = this["style"]
        set(value) {
            this["style"] = value
        }
    public var title : String
        get() = this[Attributes.title]
        set(value) {
            this[Attributes.title] = value
        }

    fun style(init : StyledElement.()->Unit) {
        val element = StyledElement("inline")
        element.init()
        val builder = StringBuilder()
        for ((k, v) in element.attributes) {
            builder.append("$k:$v;")
        }

        this["style"] = builder.toString()
    }

    // tags
	fun a(c : StyleClass? = null, id : String? = null, text : String = "", href : Link? = null, target : String? = null, init : A.() -> Unit = empty_init) {
		val tag = A()
		if (id != null) tag.id = id
		if (c != null) tag.c = c
		if (href != null) tag.href = href
		if (target != null) tag.target = target
        initTag(tag, init)
        if (tag.children.size == 0) {
            tag.text = text
        }
	}

	fun b(text : String = "", c : StyleClass? = null, id : String = "" , init : B.() -> Unit = empty_init) {
		val tag = B()
		tag.id = id
		if (c != null) tag.c = c

        initTag(tag, init)
		if (tag.children.size == 0) {
			tag.text = text
		}
	}

	fun button(text : String = "", c : StyleClass? = null, id : String = "" , init : BUTTON.() -> Unit = empty_init) {
		val tag = BUTTON()
		tag.id = id
        if (c != null) tag.c = c
        initTag(tag, init)
		if (tag.children.size == 0) {
			tag.text = text
		}
	}

	fun canvas(c : StyleClass? = null, id : String = "" , width : Int, height : Int, init : CANVAS.() -> Unit = empty_init) {
		val tag = CANVAS()
		tag.id = id
        if (c != null) tag.c = c

		tag.width = width
		tag.height = height

        initTag(tag, init)
	}

	fun div(c : StyleClass? = null, id : String = "" , init : DIV.() -> Unit = empty_init) {
		val tag = DIV()
		tag.id = id
        if (c != null) tag.c = c
        initTag(tag, init)
	}

	fun em(text : String = "", c : StyleClass? = null, id : String = "" , init : EM.() -> Unit = empty_init) {
		val tag = EM()
		tag.id = id
        if (c != null) tag.c = c
        initTag(tag, init)
		if (tag.children.size == 0) {
			tag.text = text
		}
	}

	fun fieldset(c : StyleClass? = null, id : String = "" , init : FIELDSET.() -> Unit = empty_init) {
		val tag = FIELDSET()
		tag.id = id
        if (c != null) tag.c = c
        initTag(tag, init)
	}

	fun form(c : StyleClass? = null, id : String = "" , action : Link, method : FormMethod = FormMethod.put, init : FORM.() -> Unit = empty_init) {
		val tag = FORM()
		tag.id = id
		if (c != null) tag.c = c
		tag.action = action
		tag.method = method
        initTag(tag, init)
	}

	fun h1(text : String = "", c : StyleClass? = null, id : String = "" , init : H1.() -> Unit = empty_init) {
		val tag = H1()
		tag.id = id
        if (c != null) tag.c = c

        initTag(tag, init)

		if (tag.children.size == 0) {
			tag.text = text
		}
	}
	fun h2(text : String = "", c : StyleClass? = null, id : String = "" , init : H2.() -> Unit = empty_init) {
		val tag = H2()
		tag.id = id
        if (c != null) tag.c = c

        initTag(tag, init)

		if (tag.children.size == 0) {
			tag.text = text
		}
	}
	fun h3(text : String = "", c : StyleClass? = null, id : String = "" , init : H3.() -> Unit = empty_init) {
		val tag = H3()
		tag.id = id
        if (c != null) tag.c = c

        initTag(tag, init)

		if (tag.children.size == 0) {
			tag.text = text
		}
	}
	fun h4(text : String = "", c : StyleClass? = null, id : String = "" , init : H4.() -> Unit = empty_init) {
		val tag = H4()
		tag.id = id
        if (c != null) tag.c = c

        initTag(tag, init)

		if (tag.children.size == 0) {
			tag.text = text
		}
	}

	fun h5(text : String = "", c : StyleClass? = null, id : String = "" , init : H5.() -> Unit = empty_init) {
		val tag = H5()
		tag.id = id
        if (c != null) tag.c = c

        initTag(tag, init)

		if (tag.children.size == 0) {
			tag.text = text
		}
	}

	fun img(c : StyleClass? = null, id : String = "" , width : Int? = null, height : Int? = null, src : Link? = null, alt : String = "") {
		val tag = IMG()
		tag.id = id
        if (c != null) tag.c = c
		if (width != null) tag.width = width
		if (height != null) tag.height = height
		if (src != null) tag.src = src
		tag.alt = alt

        initTag(tag, empty_init)
	}

	fun input(c : StyleClass? = null, id : String = "" , inputType : InputType, name : String = "", value : String = "", init : INPUT.() -> Unit = empty_init) {
		val tag = INPUT()
		tag.id = id
        if (c != null) tag.c = c
		tag.inputType = inputType
		tag.name = name
		tag.value = value
        initTag(tag, init)
	}

	fun label(text : String = "", c : StyleClass? = null, id : String = "" , forId : String = "", init : LABEL.() -> Unit = empty_init) {
		val tag = LABEL()
		tag.id = id
        if (c != null) tag.c = c
		tag.forId = forId

        initTag(LABEL(), init)

		if (tag.children.size == 0) {
			tag.text = text
		}
	}

	fun ol(c : StyleClass? = null, id : String = "" , init : OL.() -> Unit = empty_init) {
		val tag = OL()
		tag.id = id
        if (c != null) tag.c = c

        initTag(OL(), init)
    }

	fun p(text : String = "", c : StyleClass? = null, id : String = "" , init : P.() -> Unit = empty_init) {
		val tag = P()
		tag.id = id
        if (c != null) tag.c = c
        initTag(tag, init)
		if (tag.children.size == 0) {
			tag.text = text
		}
	}

	fun select(c : StyleClass? = null, id : String = "" , name : String = "", init : SELECT.() -> Unit = empty_init) {
		val tag = SELECT()
		tag.id = id
        if (c != null) tag.c = c
		tag.name = name
        initTag(tag, init)
    }

	fun span(c : StyleClass? = null, id : String = "" , init : SPAN.() -> Unit = empty_init) {
		val tag = SPAN()
		tag.id = id
        if (c != null) tag.c = c
        initTag(tag, init)
	}

	fun strong(text : String = "", c : StyleClass? = null, id : String = "" , init : STRONG.() -> Unit = empty_init) {
		val tag = STRONG()
		tag.id = id
        if (c != null) tag.c = c
        initTag(tag, init)
		if (tag.children.size == 0) {
			tag.text = text
		}
	}

	fun table(c : StyleClass? = null, id : String = "" , init : TABLE.() -> Unit = empty_init) {
		val tag = TABLE()
		tag.id = id
        if (c != null) tag.c = c
        initTag(tag, init)
	}

	fun textarea(text : String = "", c : StyleClass? = null, id : String = "" , rows : Int? = null, cols : Int? = null, name : String = "", init : TEXTAREA.() -> Unit = empty_init) {
		val tag = TEXTAREA()
		tag.id = id
        if (c != null) tag.c = c
        if (rows != null) tag.rows = rows
        if (cols != null) tag.cols = cols
        tag.name = name
        initTag(tag, init)
		if (tag.children.size == 0) {
			tag.text = text
		}
	}

	fun ul(c : StyleClass? = null, id : String = "" , init : UL.() -> Unit = empty_init) {
		val tag = initTag(UL(), init)
		tag.id = id
        if (c != null) tag.c = c
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
	public var href : Link
		get() = this[Attributes.href]
		set(value) {
			this[Attributes.href] = value
		}
	public var rel : String
		get() = this[Attributes.rel]
		set(value) {
			this[Attributes.rel] = value
		}
	public var target : String
		get() = this[Attributes.target]
		set(value) {
			this[Attributes.target] = value
		}
}
open class B() : BodyTag("b", false) {
}
open class BUTTON() : BodyTag("button", false) {
}
open class CANVAS() : BodyTag("canvas", false) {
	public var width : Int
		get() = this[Attributes.width]
		set(value) {
			this[Attributes.width] = value
		}
	public var height : Int
		get() = this[Attributes.height]
		set(value) {
			this[Attributes.height] = value
		}
}
open class DIV() : BodyTag("div", false) {
}
open class EM() : BodyTag("em", false) {
}
open class FIELDSET() : BodyTag("fieldset", false) {
}
open class FORM() : BodyTag("form", false) {
	public var action : Link
		get() = this[Attributes.action]
		set(value) {
			this[Attributes.action] = value
		}
	public var enctype : EncodingType
		get() = this[Attributes.enctype]
		set(value) {
			this[Attributes.enctype] = value
		}
	public var method : FormMethod
		get() = this[Attributes.method]
		set(value) {
			this[Attributes.method] = value
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
	public var width : Int
		get() = this[Attributes.width]
		set(value) {
			this[Attributes.width] = value
		}
	public var height : Int
		get() = this[Attributes.height]
		set(value) {
			this[Attributes.height] = value
		}
	public var src : Link
		get() = this[Attributes.src]
		set(value) {
			this[Attributes.src] = value
		}
	public var alt : String
		get() = this[Attributes.alt]
		set(value) {
			this[Attributes.alt] = value
		}
}
open class INPUT() : BodyTag("input", true) {
/*
	public var accept : String
		get() = this[Attributes.accept]
		set(value) {
			this[Attributes.accept] = value
		}
*/
	public var alt : String
		get() = this[Attributes.alt]
		set(value) {
			this[Attributes.alt] = value
		}
	public var autocomplete : Boolean
		get() = this[Attributes.autocomplete]
		set(value) {
			this[Attributes.autocomplete] = value
		}
	public var autofocus : Boolean
		get() = this[Attributes.autofocus]
		set(value) {
			this[Attributes.autofocus] = value
		}
	public var checked : Boolean
		get() = this[Attributes.checked]
		set(value) {
			this[Attributes.checked] = value
		}
	public var disabled : Boolean
		get() = this[Attributes.disabled]
		set(value) {
			this[Attributes.disabled] = value
		}
	public var height : Int
		get() = this[Attributes.height]
		set(value) {
			this[Attributes.height] = value
		}
/*
	public var list : String
		get() = this[Attributes.list]
		set(value) {
			this[Attributes.list] = value
		}
*/
/*
	public var max : String
		get() = this[Attributes.max]
		set(value) {
			this[Attributes.max] = value
		}
*/
	public var maxlength : Int
		get() = this[Attributes.maxlength]
		set(value) {
			this[Attributes.maxlength] = value
		}
/*
	public var min : String
		get() = this[Attributes.min]
		set(value) {
			this[Attributes.min] = value
		}
*/
	public var multiple : Boolean
		get() = this[Attributes.multiple]
		set(value) {
			this[Attributes.multiple] = value
		}
	public var inputType : InputType
		get() = this[Attributes.inputType]
		set(value) {
			this[Attributes.inputType] = value
		}
	public var name : String
		get() = this[Attributes.name]
		set(value) {
			this[Attributes.name] = value
		}
	public var pattern : String
		get() = this[Attributes.pattern]
		set(value) {
			this[Attributes.pattern] = value
		}
	public var placeholder : String
		get() = this[Attributes.placeholder]
		set(value) {
			this[Attributes.placeholder] = value
		}
	public var readonly : Boolean
		get() = this[Attributes.readonly]
		set(value) {
			this[Attributes.readonly] = value
		}
	public var required : Boolean
		get() = this[Attributes.required]
		set(value) {
			this[Attributes.required] = value
		}
	public var size : Int
		get() = this[Attributes.size]
		set(value) {
			this[Attributes.size] = value
		}
	public var src : Link
		get() = this[Attributes.src]
		set(value) {
			this[Attributes.src] = value
		}
	public var step : Int
		get() = this[Attributes.step]
		set(value) {
			this[Attributes.step] = value
		}
	public var value : String
		get() = this[Attributes.value]
		set(value) {
			this[Attributes.value] = value
		}
	public var width : Int
		get() = this[Attributes.width]
		set(value) {
			this[Attributes.width] = value
		}
}
open class LABEL() : BodyTag("label", false) {
	public var forId : String
		get() = this[Attributes.forId]
		set(value) {
			this[Attributes.forId] = value
		}
}
open class OL() : BodyTag("ol", false) {
	fun li(text : String = "", c : StyleClass? = null, id : String = "" , init : LI.() -> Unit = empty_init) {
		val tag = LI()
		tag.id = id
        if (c != null) tag.c = c
        initTag(tag, init)
		if (tag.children.size == 0) {
			tag.text = text
		}
	}

}
open class P() : BodyTag("p", false) {
}
open class SELECT() : BodyTag("select", false) {
	public var name : String
		get() = this[Attributes.name]
		set(value) {
			this[Attributes.name] = value
		}
	public var size : Int
		get() = this[Attributes.size]
		set(value) {
			this[Attributes.size] = value
		}
	public var multiple : Boolean
		get() = this[Attributes.multiple]
		set(value) {
			this[Attributes.multiple] = value
		}
	public var disabled : Boolean
		get() = this[Attributes.disabled]
		set(value) {
			this[Attributes.disabled] = value
		}
	fun option(c : StyleClass? = null, value : String = "", id : String = "" , init : OPTION.() -> Unit = empty_init) {
		val tag = OPTION()
		tag.id = id
        tag.value = value
        if (c != null) tag.c = c
        initTag(tag, init)
	}

	fun optgroup(c : StyleClass? = null, id : String = "" , init : OPTGROUP.() -> Unit = empty_init) {
		val tag = OPTGROUP()
		tag.id = id
        if (c != null) tag.c = c
        initTag(tag, init)
	}

}
open class SPAN() : BodyTag("span", false) {
}
open class STRONG() : BodyTag("strong", false) {
}
open class TABLE() : BodyTag("table", false) {
	fun tr(c : StyleClass? = null, id : String = "" , init : TR.() -> Unit = empty_init) {
		val tag = TR()
		tag.id = id
        if (c != null) tag.c = c
        initTag(tag, init)
	}

	fun tbody(c : StyleClass? = null, id : String = "" , init : TBODY.() -> Unit = empty_init) {
		val tag = TBODY()
		tag.id = id
        if (c != null) tag.c = c
        initTag(tag, init)
	}

}
open class TEXTAREA() : BodyTag("textarea", false) {
	public var autofocus : Boolean
		get() = this[Attributes.autofocus]
		set(value) {
			this[Attributes.autofocus] = value
		}
	public var cols : Int
		get() = this[Attributes.cols]
		set(value) {
			this[Attributes.cols] = value
		}
	public var disabled : Boolean
		get() = this[Attributes.disabled]
		set(value) {
			this[Attributes.disabled] = value
		}
	public var maxlength : Int
		get() = this[Attributes.maxlength]
		set(value) {
			this[Attributes.maxlength] = value
		}
	public var name : String
		get() = this[Attributes.name]
		set(value) {
			this[Attributes.name] = value
		}
	public var placeholder : String
		get() = this[Attributes.placeholder]
		set(value) {
			this[Attributes.placeholder] = value
		}
	public var readonly : Boolean
		get() = this[Attributes.readonly]
		set(value) {
			this[Attributes.readonly] = value
		}
	public var required : Boolean
		get() = this[Attributes.required]
		set(value) {
			this[Attributes.required] = value
		}
	public var rows : Int
		get() = this[Attributes.rows]
		set(value) {
			this[Attributes.rows] = value
		}
	public var wrap : Wrap
		get() = this[Attributes.wrap]
		set(value) {
			this[Attributes.wrap] = value
		}
}
open class UL() : BodyTag("ul", false) {
	fun li(text : String = "", c : StyleClass? = null, id : String = "" , init : LI.() -> Unit = empty_init) {
		val tag = LI()
		tag.id = id
        if (c != null) tag.c = c
        initTag(tag, init)
		if (tag.children.size == 0) {
			tag.text = text
		}
	}

}
open class LI() : BodyTag("li", false) {
}
open class OPTION() : BodyTag("option", false) {
	public var value : String
		get() = this[Attributes.value]
		set(value) {
			this[Attributes.value] = value
		}
	public var label : String
		get() = this[Attributes.label]
		set(value) {
			this[Attributes.label] = value
		}
	public var disabled : Boolean
		get() = this[Attributes.disabled]
		set(value) {
			this[Attributes.disabled] = value
		}
}
open class OPTGROUP() : BodyTag("optgroup", false) {
}
open class TR() : BodyTag("tr", false) {
	fun td(c :  StyleClass? = null, id : String = "", init : TD.() -> Unit = empty_init) {
		val tag = TD()
		tag.id = id
        if (c != null) tag.c = c
        initTag(tag, init)
	}

	fun th(text : String = "", c : StyleClass? = null, id : String = "" , init : TH.() -> Unit = empty_init) {
		val tag = TH()
		tag.id = id
        if (c != null) tag.c = c
        initTag(tag, init)
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
