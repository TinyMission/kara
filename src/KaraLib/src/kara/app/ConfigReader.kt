package kara.internal

import java.io.*
import java.util.*
import org.codehaus.jackson.node.*
import org.codehaus.jackson.map.ObjectMapper
import org.codehaus.jackson.*
import kara.*
import java.net.URL

/**
 * Parses a json file containing a config hash.
 */
class ConfigReader(val config : Config) {

    /** Parses a json config file and uses it to populate the given reader's config. */
    fun read(file : URL) {
        try {
            val tree = ObjectMapper().readTree(file)!!

            var keyStack = Stack<String>()
            parseNode(tree, keyStack)
        }
        catch(e: IOException) {
            // Do nothing. Can't read
        }
    }

    /** Recursively parses the node and adds all value nodes to the config with flattened keys. */
    fun parseNode(node : JsonNode, keyStack : Stack<String>) {
        for (name in node.getFieldNames()) {
            val child = node.get(name)!!
            if (child is ObjectNode) {
                keyStack.push(name)
                parseNode(child, keyStack)
                keyStack.pop()
            }
            else if (child.isValueNode()) {
                var path = name
                for (key in keyStack) {
                    path = "${key}." + path
                }
                if (child.isInt())
                    config[path] = child.getIntValue().toString()
                else
                    config[path] = child.getTextValue()!!
            }
        }
    }
}
