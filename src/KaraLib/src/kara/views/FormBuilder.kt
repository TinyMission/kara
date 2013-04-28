package kara

import java.lang.reflect.Method
import org.apache.log4j.Logger
import kara.internal.*
import kara.InputType.*

public trait FormModel<P> {
    fun modelName(): String
    fun propertyValue(property: P): String
    fun propertyName(property: P): String
}

class BeanFormModel(val model: Any) : FormModel<String> {
    val modelName = model.javaClass.getSimpleName().toLowerCase()

    override fun modelName(): String {
        return modelName
    }

    override fun propertyValue(property: String): String {
        return model.propertyValue(property).toString() // TODO: Use provided parameter serialization instead of toString
    }

    override fun propertyName(property: String): String {
        return property
    }
}

/**
 * Allows forms to be built based on a model object.
 */
class FormBuilder<P>(containingTag : HtmlBodyTag, val model : FormModel<P>) : FORM(containingTag) {
    val logger = Logger.getLogger(this.javaClass)!!

    /** If true, the form will have enctype="multipart/form-data" */
    var hasFiles : Boolean = false

    fun propertyValue(property: P) : String {
        return model.propertyValue(property)
    }

    fun propertyName(property: P) : String {
        return "${model.modelName()}[${model.propertyName(property)}]"
    }

    fun propertyId(property: P) : String {
        return "form-${model.modelName()}-${model.propertyName(property)}"
    }

    /**
     * Gets the current body tag on the top of the stack, or this if one doesn't exist (shouldn't happen).
     */
    val currentTag : HtmlBodyTag
        get() {
            if (tagStack == null)
                return this
            val tag = tagStack?.top!!
            if (tag is HtmlBodyTag)
                return tag as HtmlBodyTag
            return this
        }

    /**
     * Creates a label element for the given property.
     *
     * @param text the text to use for the label (defaults to the property name)
     */
    public fun labelFor(property: P, text : String? = null, c : StyleClass? = null) {
        currentTag.label(c) {
            forId = propertyId(property)
            +(text ?: model.propertyName(property).decamel().capitalize())
        }
    }

    /**
     * Creates an input of the given type for the given property.
     * This method should not generally be used, as all valid input types are mapped to their own methods.
     * It may be convenient, however, if you're trying to assign the input type programmatically.
     */
    public fun inputFor(inputType : InputType, property: P, contents : INPUT.() -> Unit = {}) {
        val value = propertyValue(property)
        currentTag.input(id = propertyId(property)) {
            this.inputType = inputType
            this.name = propertyName(property)
            this.value = value
            this.contents()
        }
    }

    /**
     * Creates a textarea for the given property.
     */
    public fun textAreaFor(property: P, contents : TEXTAREA.() -> Unit = {}) {
        val value = propertyValue(property)
        currentTag.textarea(id=propertyId(property)) {
            this.name=propertyName(property)
            this.text=value
            this.contents()
        }
    }

    /**
     * Creates a submit button for the form, with an optional name.
     */
    public fun submitButton(value : String, name : String = "submit", init : INPUT.() -> Unit = {}) {
        currentTag.input() {
            this.inputType = InputType.submit
            this.value = value
            this.name = name
            init()
        }
    }

    /**
     * Creates an input of type text for the given property.
     */
    public fun textFieldFor(property: P, init : INPUT.() -> Unit = {}) {
        inputFor(InputType.text, property, init)
    }

    /**
     * Creates an input of type password for the given property.
     */
    public fun passwordFieldFor(property: P, init : INPUT.() -> Unit = {}) {
        inputFor(InputType.password, property, init)
    }

    /**
     * Creates an input of type email for the given property.
     */
    public fun emailFieldFor(property: P, init : INPUT.() -> Unit = {}) {
        inputFor(InputType.email, property, init)
    }

    /**
     * Creates an input of type tel for the given property.
     */
    public fun telFieldFor(property: P, init : INPUT.() -> Unit = {}) {
        inputFor(InputType.tel, property, init)
    }

    /**
     * Creates an input of type date for the given property.
     */
    public fun dateFieldFor(property: P, init : INPUT.() -> Unit = {}) {
        inputFor(InputType.date, property, init)
    }

    /**
     * Creates an input of type datetime for the given property.
     */
    public fun dateTimeFieldFor(property: P, init : INPUT.() -> Unit = {}) {
        inputFor(InputType.datetime, property, init)
    }

    /**
     * Creates an input of type color for the given property.
     */
    public fun colorFieldFor(property: P, init : INPUT.() -> Unit = {}) {
        inputFor(InputType.color, property, init)
    }

    /**
     * Creates an input of type number for the given property.
     */
    public fun numberFieldFor(property: P, init : INPUT.() -> Unit = {}) {
        inputFor(InputType.number, property, init)
    }

    /**
     * Creates an input of type month for the given property.
     */
    public fun monthFieldFor(property: P, init : INPUT.() -> Unit = {}) {
        inputFor(month, property, init)
    }

    /**
     * Creates an input of type range for the given property.
     */
    public fun rangeFieldFor(property: P, init : INPUT.() -> Unit = {}) {
        inputFor(range, property, init)
    }

    /**
     * Creates an input of type search for the given property.
     */
    public fun searchFieldFor(property: P, init : INPUT.() -> Unit = {}) {
        inputFor(search, property, init)
    }

    /**
     * Creates an input of type time for the given property.
     */
    public fun timeFieldFor(property: P, init : INPUT.() -> Unit = {}) {
        inputFor(time, property, init)
    }

    /**
     * Creates an input of type url for the given property.
     */
    public fun urlFieldFor(property: P, init : INPUT.() -> Unit = {}) {
        inputFor(url, property, init)
    }

    /**
     * Creates an input of type week for the given property.
     */
    public fun weekFieldFor(property: P, init : INPUT.() -> Unit = {}) {
        inputFor(week, property, init)
    }

    /**
     * Creates an input of type file for the given property.
     */
    public fun fileFieldFor(property: P, init : INPUT.() -> Unit = {}) {
        inputFor(file, property, init)
        if (!hasFiles) {
            hasFiles = true
            logger.debug("Setting enctype=multipart/form-data for form due to a file field")
        }
    }

    /**
     * Creates a radio button for the given property and value.
     */
    public fun radioFor(property: P, value : String, contents : INPUT.() -> Unit = {}) {
        val modelValue = propertyValue(property).toString()
        currentTag.input(id = propertyId(property)) {
            this.name = propertyName(property)
            this.inputType = radio
            this.value = value
            checked = value.equalsIgnoreCase(modelValue)
            contents()
        }
    }

    /**
     * Creates a checkbox for the given property.
     */
    public fun checkBoxFor(property: P, contents : INPUT.() -> Unit = {}) {
        val modelValue = propertyValue(property)
        currentTag.input(id = propertyId(property)) {
            this.inputType = checkbox
            this.name = propertyName(property)
            checked = modelValue == "true"
            contents()
        }
    }
}

fun <P> HtmlBodyTag.formForModel(model: FormModel<P>, action : Link, formMethod : FormMethod = FormMethod.post, init : FormBuilder<P>.() -> Unit) {
    val builder = FormBuilder(this, model)
    builder.action = action
    builder.method = formMethod
    builder.tagStack = this.tagStack
    builder.init()
    children.add(builder)

    if (builder.hasFiles) {
        builder.enctype = EncodingType.multipart
    }
}

fun HtmlBodyTag.formForBean(bean: Any, action : Link, formMethod : FormMethod = FormMethod.post, init : FormBuilder<String>.() -> Unit) {
    formForModel(BeanFormModel(bean), action, formMethod, init)
}
