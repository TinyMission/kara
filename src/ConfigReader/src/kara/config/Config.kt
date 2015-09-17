package kara.config

import java.util.*
import javax.naming.*
import java.util.concurrent.*
import org.apache.log4j.*
import java.io.*
import java.util.regex.*

public open class Config() {
    public class MissingException(desc: String) : RuntimeException(desc)

    val data = LinkedHashMap<String, String>()
    val cache = ConcurrentHashMap<String, String?>()

    /**
     * Gets the value for the given key.
     * Will raise an exception if the value isn't present. Try calling contains(key) first if you're unsure.
     */
    public fun get(name: String): String {
        return tryGet(name) ?: throw MissingException("Could not find config value for key $name")
    }

    public fun tryGet(name: String): String? {
        return lookupCache(name) {
            lookupJNDI(name) ?: data[name]
        }
    }

    private fun lookupCache(name: String, eval: (String) -> String?): String? {
        return cache[name] ?: run {
            val answer = eval(name)
            if (answer != null) {
                cache.putIfAbsent(name, answer)
            }
            answer
        }
    }

    /** Sets a value for the given key. */
    public fun set(name: String, value: String) {
        data[name] = value
    }

    /** Returns true if the config contains a value for the given key. */
    public fun contains(name: String): Boolean {
        return data.containsKey(name) || lookupJNDI(name) != null
    }

    /** Prints the entire config to a nicely formatted string. */
    public override fun toString(): String {
        val builder = StringBuilder()
        for (name in data.keySet()) {
            builder.append("$name: ${data[name]}\n")
        }
        return builder.toString()
    }

    private fun lookupJNDI(name: String): String? {
        try {
            val initCtx = InitialContext()
            val envCtx = initCtx.lookup("java:comp/env") as Context

            val folder = envCtx.lookup(name)
            return (folder as String)
        } catch(e: NamingException) {
            return null
        }
    }

    public companion object {
        val logger = Logger.getLogger(Config::class.java)!!

        public fun readConfig(config: Config, path: String, classloader: ClassLoader, baseFile: File? = null) {
            fun eval(name: String): String {
                return config.tryGet(name) ?: System.getProperty(name) ?: System.getenv(name) ?: error("$name is not defined")
            }

            val resolvedPath = evalVars(path, ::eval)

            val file = if (resolvedPath.startsWith('/') || baseFile == null) File(resolvedPath) else File(baseFile, resolvedPath)
            var text: String? = null
            var base: File? = null
            if (file.exists()) {
                base = file.parentFile
                text = file.readText("UTF-8")
                Config.logger.info("Reading ${file.absolutePath}")
            } else {
                val resource = classloader.getResourceAsStream(path)
                if (resource != null) {
                    Config.logger.info("Reading classpath resource $path")
                    base = null
                    text = resource.reader("UTF-8").readText()
                }
            }

            if (text == null) {
                error("$path cannot be found")
            }

            text.reader.forEachLine {
                val line = it.trim()

                when {
                    line.startsWith("include ") -> {
                        readConfig(config, line.removePrefix("include "), classloader, base)
                    }

                    line.startsWith("log ") -> {
                        Config.logger.info(evalVars(line.removePrefix("log "), ::eval))
                    }

                    line.startsWith("#") || line.isEmpty() -> {
                        // Skip comments and empty lines
                    }

                    else -> {
                        val eq = line.indexOf('=')
                        if (eq <= 0) error("Cannot parse line '$line' in file '${file.absolutePath}'")
                        config.set(line.substring(0, eq).trim(), evalVars(line.substring(eq + 1).trim(), ::eval))
                    }
                }
            }
        }

        val varPattern = Pattern.compile("\\$\\{([^\\}]*)\\}")
        fun evalVars(line: String, eval: (String) -> String): String {
            val matcher = varPattern.matcher(line)
            val answer = StringBuilder()

            var lastAppend = 0

            while (matcher.find()) {
                val varName = matcher.group(1)!!
                answer.append(line, lastAppend, matcher.start())
                answer.append(eval(varName))
                lastAppend = matcher.end()
            }

            answer.append(line, lastAppend, line.length())

            return answer.toString()
        }
    }
}
