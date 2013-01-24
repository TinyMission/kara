package kara.generators

import java.util.HashMap
import org.apache.log4j.Logger


/** Gets thrown by Permissions when the user denies an action from being performed. */
class PermissionDeniedException(action : String) : RuntimeException("Permission denied for: $action") {

}


/**
 * Asks the user for permission to perform generator activities and keeps track of their responses.
 */
class Permissions() {
    val logger = Logger.getLogger("Permissions")!!

    val always = HashMap<String,Boolean>()

    fun ask(category : String, action : String) : Boolean {
        if (always.getOrElse(category, { false})) { // the user previously answered 'always'
            return true
        }
        logger.warn("$action Would you like to allow this operation?")
        var isPositive : Boolean? = null
        while(isPositive == null) {
            println("(A)lways, (y)es, (n)o: ")
            val answer = readLine()
            when (answer) {
                "A", "a", "Always", "always", "", null -> {
                    always[category] = true
                    isPositive = true
                }
                "Y", "y", "Yes", "yes" -> isPositive = true
                "N", "n", "No", "no" -> throw PermissionDeniedException(action)
                else -> println("Sorry, couldn't understand your response...")
            }
        }
        return isPositive!!
    }

}
