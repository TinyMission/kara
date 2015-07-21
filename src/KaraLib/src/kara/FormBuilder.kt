package kara

import java.lang.reflect.Method
import org.apache.log4j.Logger
import kara.internal.*
import kotlin.html.*
import kotlin.html.InputType.*
import kotlinx.reflection.*


public interface FormModel<P> {
    fun modelName(): String
    fun propertyValue(property: P): String
    fun propertyName(property: P): String
}

class BeanFormModel(val model: Any) : FormModel<String> {
    val modelName = model.javaClass.simpleName.toLowerCase()

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

fun <P,M:FormModel<P>> HtmlBodyTag.formForModel(model: M, action : Link, formMethod : FormMethod = FormMethod.post, contents: FormBuilder<P,M>.() -> Unit) {
    val builder = FormBuilder(this, model)
    builder.action = action
    builder.method = formMethod
    builder.contents()

    if (builder.hasFiles) {
        builder.enctype = EncodingType.multipart
    }
}

fun HtmlBodyTag.formForBean(bean: Any, action : Link, formMethod : FormMethod = FormMethod.post, contents : FormBuilder<String, FormModel<String>>.() -> Unit) {
    formForModel(BeanFormModel(bean), action, formMethod, contents)
}

/**
 * Allows forms to be built based on a model object.
 */
class FormBuilder<P, M:FormModel<P>>(containingTag : HtmlBodyTag, val model : M) : FORM(containingTag) {
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
     * Creates a label element for the given property.
     *
     * @param text the text to use for the label (defaults to the property name)
     */
    public fun HtmlBodyTag.labelFor(property: P, text : String? = null, c : StyleClass? = null) {
        label {
            setClass(c)
            forId = propertyId(property)
            +(text ?: model.propertyName(property).decamel().capitalize())
        }
    }

    /**
     * Creates an input of the given type for the given property.
     * This method should not generally be used, as all valid input types are mapped to their own methods.
     * It may be convenient, however, if you're trying to assign the input type programmatically.
     */
    public fun HtmlBodyTag.inputFor(inputType : InputType, property: P, contents : INPUT.() -> Unit = empty_contents) {
        val value = propertyValue(property)
        input {
            this.id = propertyId(property)
            this.inputType = inputType
            this.name = propertyName(property)
            this.value = value
            this.contents()
        }
    }

    /**
     * Creates a textarea for the given property.
     */
    public fun HtmlBodyTag.textAreaFor(property: P, contents : TEXTAREA.() -> Unit = empty_contents) {
        val value = propertyValue(property)
        textarea {
            this.id=propertyId(property)
            this.name=propertyName(property)
            this.text=value
            this.contents()
        }
    }

    /**
     * Creates a submit button for the form, with an optional name.
     */
    public fun HtmlBodyTag.submitButton(value : String, name : String = "submit", contents : INPUT.() -> Unit = empty_contents) {
        input() {
            this.inputType = InputType.submit
            this.value = value
            this.name = name
            contents()
        }
    }

    /**
     * Creates an input of type text for the given property.
     */
    public fun HtmlBodyTag.textFieldFor(property: P, contents : INPUT.() -> Unit = empty_contents) {
        inputFor(InputType.text, property, contents)
    }

    /**
     * Creates an input of type password for the given property.
     */
    public fun HtmlBodyTag.passwordFieldFor(property: P, contents : INPUT.() -> Unit = empty_contents) {
        inputFor(InputType.password, property, contents)
    }

    /**
     * Creates an input of type email for the given property.
     */
    public fun HtmlBodyTag.emailFieldFor(property: P, contents : INPUT.() -> Unit = empty_contents) {
        inputFor(InputType.email, property, contents)
    }

    /**
     * Creates an input of type tel for the given property.
     */
    public fun HtmlBodyTag.telFieldFor(property: P, contents : INPUT.() -> Unit = empty_contents) {
        inputFor(InputType.tel, property, contents)
    }

    /**
     * Creates an input of type date for the given property.
     */
    public fun HtmlBodyTag.dateFieldFor(property: P, contents : INPUT.() -> Unit = empty_contents) {
        inputFor(InputType.date, property, contents)
    }

    /**
     * Creates an input of type datetime for the given property.
     */
    public fun HtmlBodyTag.dateTimeFieldFor(property: P, contents : INPUT.() -> Unit = empty_contents) {
        inputFor(InputType.datetime, property, contents)
    }

    /**
     * Creates an input of type color for the given property.
     */
    public fun HtmlBodyTag.colorFieldFor(property: P, contents : INPUT.() -> Unit = empty_contents) {
        inputFor(InputType.color, property, contents)
    }

    /**
     * Creates an input of type number for the given property.
     */
    public fun HtmlBodyTag.numberFieldFor(property: P, contents : INPUT.() -> Unit = empty_contents) {
        inputFor(InputType.number, property, contents)
    }

    /**
     * Creates an input of type month for the given property.
     */
    public fun HtmlBodyTag.monthFieldFor(property: P, contents : INPUT.() -> Unit = empty_contents) {
        inputFor(month, property, contents)
    }

    /**
     * Creates an input of type range for the given property.
     */
    public fun HtmlBodyTag.rangeFieldFor(property: P, contents : INPUT.() -> Unit = empty_contents) {
        inputFor(range, property, contents)
    }

    /**
     * Creates an input of type search for the given property.
     */
    public fun HtmlBodyTag.searchFieldFor(property: P, contents : INPUT.() -> Unit = empty_contents) {
        inputFor(search, property, contents)
    }

    /**
     * Creates an input of type time for the given property.
     */
    public fun HtmlBodyTag.timeFieldFor(property: P, contents : INPUT.() -> Unit = empty_contents) {
        inputFor(time, property, contents)
    }

    /**
     * Creates an input of type url for the given property.
     */
    public fun HtmlBodyTag.urlFieldFor(property: P, contents : INPUT.() -> Unit = empty_contents) {
        inputFor(url, property, contents)
    }

    /**
     * Creates an input of type week for the given property.
     */
    public fun HtmlBodyTag.weekFieldFor(property: P, contents : INPUT.() -> Unit = empty_contents) {
        inputFor(week, property, contents)
    }

    /**
     * Creates an input of type file for the given property.
     */
    public fun HtmlBodyTag.fileFieldFor(property: P, contents : INPUT.() -> Unit = empty_contents) {
        inputFor(file, property, contents)
        if (!hasFiles) {
            hasFiles = true
            logger.debug("Setting enctype=multipart/form-data for form due to a file field")
        }
    }

    /**
     * Creates a radio button for the given property and value.
     */
    public fun HtmlBodyTag.radioFor(property: P, value : String, contents : INPUT.() -> Unit = empty_contents) {
        val modelValue = propertyValue(property)
        input {
            this.id = propertyId(property)
            this.name = propertyName(property)
            this.inputType = radio
            this.value = value
            checked = value.equals(modelValue, ignoreCase = true)
            contents()
        }
    }

    /**
     * Creates a checkbox for the given property.
     */
    public fun HtmlBodyTag.checkBoxFor(property: P, contents : INPUT.() -> Unit = empty_contents) {
        val modelValue = propertyValue(property)
        input {
            this.id = propertyId(property)
            this.inputType = checkbox
            this.name = propertyName(property)
            checked = modelValue == "true"
            contents()
        }
    }
}

