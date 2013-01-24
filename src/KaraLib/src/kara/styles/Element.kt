package kara.styles

import java.util.ArrayList
import java.util.HashMap
import kara.styles.*

/**
 * Represents a single stylesheet element.
 */
open class Element(val selector : String) {

    val children : MutableList<Element> = ArrayList<Element>()

    val attributes = HashMap<String, Any>()

    /**
     * Creates a new child element with the given selector and block.
     */
    fun s(selector : String, init : Element.() -> Unit) {
        val element = Element(selector)
        element.init()
        children.add(element)
    }

    /**
     * Writes the element to the builder with the given indenation.
     */
    open fun build(builder : StringBuilder, baseSelector : String) {
        if (baseSelector.length() > 0)
            builder.append("$baseSelector $selector {\n")
        else
            builder.append("$selector {\n")
        for (a in attributes.keySet()) {
            val attr = attributes[a]!!
            builder.append("    $a: ${attr.toString()};\n")
        }
        builder.append("}\n")
        val childBaseSelector = if (baseSelector.length() > 0) "$baseSelector $selector" else selector
        for (val child in children) {
            child.build(builder, childBaseSelector)
        }
    }

    /** Strongly-typed method for pulling attributes out of the hash. */
    fun getAttribute<T>(name : String) : T {
        if (attributes.containsKey(name))
            return attributes[name] as T
        else
            throw Exception("Element has no attribute ${name}")
    }

    /** Strongly-typed method for pulling attributes out of the hash, with a default return value. */
    fun getAttribute<T>(name : String, default : T) : T {
        if (attributes.containsKey(name))
            return attributes[name] as T
        else
            return default
    }

    /** Shorthand for making a color inside a stylesheet. */
    fun c(colorString : String) : Color {
        return color(colorString)
    }

    var backgroundAttachment : BackgroundAttachment?
        get() = getAttribute<BackgroundAttachment>("background-attachment")
        set(value) {
            attributes["background-attachment"] = value.toString()
        }

    var backgroundColor : Color?
        get() = getAttribute<Color>("background-color")
        set(value) {
            attributes["background-color"] = value.toString()
        }

    var backgroundImage : String?
        get() = getAttribute<String>("background-image")
        set(value) {
            attributes["background-image"] = value.toString()
        }

    var backgroundPosition : String?
        get() = getAttribute<String>("background-position")
        set(value) {
            attributes["background-position"] = value.toString()
        }

    var backgroundRepeat : BackgroundRepeat?
        get() = getAttribute<BackgroundRepeat>("background-repeat")
        set(value) {
            attributes["background-repeat"] = value.toString()
        }

    var border : String = ""
        set(value) {
            val tokens = value.split(' ')
            for (val token in tokens) {
                if (isLinearDimension(token))
                    borderWidth = LinearDimension.fromString(token)
                else if (isColor(token))
                    borderColor = color(token)
                else if (isBorderStyle(token))
                    borderStyle = makeBorderStyle(token)
                else
                    throw Exception("Invalid border property: ${token}")
            }
        }

    var borderColor : Color?
        get() = getAttribute<Color>("border-color")
        set(value) {
            attributes["border-color"] = value.toString()
        }

    var borderRadius : LinearDimension
        get() = getAttribute<LinearDimension>("border-radius", 0.px)
        set(value) {
            attributes["border-radius"] = value.toString()
        }

    var borderBottomLeftRadius : LinearDimension
        get() = getAttribute<LinearDimension>("border-bottom-left-radius", 0.px)
        set(value) {
            attributes["border-bottom-left-radius"] = value.toString()
        }

    var borderBottomRightRadius : LinearDimension
        get() = getAttribute<LinearDimension>("border-bottom-right-radius", 0.px)
        set(value) {
            attributes["border-bottom-right-radius"] = value.toString()
        }

    var borderTopLeftRadius : LinearDimension
        get() = getAttribute<LinearDimension>("border-top-left-radius", 0.px)
        set(value) {
            attributes["border-top-left-radius"] = value.toString()
        }

    var borderTopRightRadius : LinearDimension
        get() = getAttribute<LinearDimension>("border-top-right-radius", 0.px)
        set(value) {
            attributes["border-top-right-radius"] = value.toString()
        }

    var borderStyle : BorderStyle?
        get() = getAttribute<BorderStyle>("border-style")
        set(value) {
            attributes["border-style"] = value.toString()
        }

    var borderWidth : LinearDimension
        get() = getAttribute<LinearDimension>("border-width", 0.px)
        set(value) {
            attributes["border-width"] = value.toString()
        }

    var clear : Clear?
        get() = getAttribute<Clear>("clear")
        set(value) {
            attributes["clear"] = value.toString()
        }

    var color : Color?
        get() = getAttribute<Color>("color")
        set(value) {
            attributes["color"] = value.toString()
        }

    var float : Float?
        get() = getAttribute<Float>("float")
        set(value) {
            attributes["float"] = value.toString()
        }

    var fontFamily : String?
        get() = getAttribute<String>("font-family")
        set(value) {
            attributes["font-family"] = value.toString()
        }

    var fontSize : LinearDimension?
        get() = getAttribute<LinearDimension>("font-size")
        set(value) {
            attributes["font-size"] = value.toString()
        }

    var fontWeight : FontWeight
        get() = getAttribute<FontWeight>("font-weight")
        set(value) {
            attributes["font-weight"] = value.toString()
        }

    var height : LinearDimension?
        get() = getAttribute<LinearDimension>("height")
        set(value) {
            attributes["height"] = value.toString()
        }

    var lineHeight : LinearDimension?
        get() = getAttribute<LinearDimension>("line-height")
        set(value) {
            attributes["line-height"] = value.toString()
        }

    var margin : BoxDimensions?
        get() = getAttribute<BoxDimensions>("margin")
        set(value) {
            attributes["margin"] = value.toString()
        }

    var marginTop : LinearDimension?
        get() = getAttribute<LinearDimension>("margin-top")
        set(value) {
            attributes["margin-top"] = value.toString()
        }

    var marginBottom : LinearDimension?
        get() = getAttribute<LinearDimension>("margin-bottom")
        set(value) {
            attributes["margin-bottom"] = value.toString()
        }

    var marginLeft : LinearDimension?
        get() = getAttribute<LinearDimension>("margin-left")
        set(value) {
            attributes["margin-left"] = value.toString()
        }

    var marginRight : LinearDimension?
        get() = getAttribute<LinearDimension>("margin-right")
        set(value) {
            attributes["margin-right"] = value.toString()
        }

    var maxHeight : LinearDimension?
        get() = getAttribute<LinearDimension>("max-height")
        set(value) {
            attributes["max-height"] = value.toString()
        }

    var maxWidth : LinearDimension?
        get() = getAttribute<LinearDimension>("max-width")
        set(value) {
            attributes["max-width"] = value.toString()
        }

    var minHeight : LinearDimension?
        get() = getAttribute<LinearDimension>("min-height")
        set(value) {
            attributes["min-height"] = value.toString()
        }

    var minWidth : LinearDimension?
        get() = getAttribute<LinearDimension>("min-width")
        set(value) {
            attributes["min-width"] = value.toString()
        }

    var overflow : Overflow
        get() = getAttribute<Overflow>("overflow", Overflow.inherit)
        set(value) {
            attributes["overflow"] = value.toString()
        }

    var padding : BoxDimensions?
        get() = getAttribute<BoxDimensions>("padding")
        set(value) {
            attributes["padding"] = value.toString()
        }

    var paddingTop : LinearDimension?
        get() = getAttribute<LinearDimension>("padding-top")
        set(value) {
            attributes["padding-top"] = value.toString()
        }

    var paddingBottom : LinearDimension?
        get() = getAttribute<LinearDimension>("padding-bottom")
        set(value) {
            attributes["padding-bottom"] = value.toString()
        }

    var paddingLeft : LinearDimension?
        get() = getAttribute<LinearDimension>("padding-left")
        set(value) {
            attributes["padding-left"] = value.toString()
        }

    var paddingRight : LinearDimension?
        get() = getAttribute<LinearDimension>("padding-right")
        set(value) {
            attributes["padding-right"] = value.toString()
        }

    var textAlign : TextAlign
        get() = getAttribute<TextAlign>("text-align", TextAlign.inherit)
        set(value) {
            attributes["text-align"] = value.toString()
        }

    var verticalAlign : VerticalAlign
        get() = getAttribute<VerticalAlign>("vertical-align", VerticalAlign.inherit)
        set(value) {
            attributes["vertical-align"] = value.toString()
        }

    var width : LinearDimension?
        get() = getAttribute<LinearDimension>("width")
        set(value) {
            attributes["width"] = value.toString()
        }
}
