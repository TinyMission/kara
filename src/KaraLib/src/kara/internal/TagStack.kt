package kara.internal

import kara.Tag
import java.util.Stack

/**
 * Keeps track of the stack of tags that are currently being rendered.
 */
class TagStack(val initial : Tag) {

    val stack = Stack<Tag>();

    {
        stack.push(initial)
    }

    public val top : Tag
        get() = stack.last()

    public fun push(tag : Tag) {
        stack.push(tag)
    }

    public fun pop(tag : Tag) {
        if (tag == top)
            stack.pop()
    }
}
