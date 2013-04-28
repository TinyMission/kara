package kara.internal

import kara.HtmlTag
import java.util.Stack

/**
 * Keeps track of the stack of tags that are currently being rendered.
 */
class TagStack(val initial : HtmlTag) {

    val stack = Stack<HtmlTag>();

    {
        stack.push(initial)
    }

    public val top : HtmlTag
        get() = stack.last()

    public fun push(tag : HtmlTag) {
        stack.push(tag)
    }

    public fun pop(tag : HtmlTag) {
        if (tag == top)
            stack.pop()
    }
}
