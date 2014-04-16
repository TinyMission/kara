package kara.internal

import kara.Config
import java.io.File
import java.net.URL
import java.util.regex.Matcher
import java.util.regex.Pattern

public fun Config.readConfig(path: String, baseFile: File? = null) {
    val resolvedPath = evalVars(path)
    val file = if (resolvedPath.startsWith('/') || baseFile == null) File(resolvedPath) else File(baseFile, resolvedPath)

    logger.info("Reading ${file.getAbsolutePath()}")

    file.forEachLine {
        val line = it.trim()

        when {
            line.startsWith("include ") -> {
                readConfig(line.trimLeading("include "), file.getParentFile())
            }

            line.startsWith("log ") -> {
                logger.info(evalVars(line.trimLeading("log ")))
            }

            line.startsWith("#") || line.isEmpty() -> {
               // Skip comments and empty lines
            }

            else -> {
                val eq = line.indexOf('=')
                if (eq <= 0) error("Cannot parse line '$line' in file '${file.getAbsolutePath()}'")
                set(line.substring(0, eq).trim(), evalVars(line.substring(eq + 1).trim()))
            }
        }
    }
}

val varPattern =  Pattern.compile("\\$\\{([^\\}]*)\\}")
public fun Config.evalVars(line: String): String {
    val matcher = varPattern.matcher(line)
    val answer = StringBuilder()

    var lastAppend = 0

    while (matcher.find()) {
        val varName = matcher.group(1)!!
        answer.append(line, lastAppend, matcher.start())
        answer.append(evalVar(varName))
        lastAppend = matcher.end()
    }

    answer.append(line, lastAppend, line.length())

    return answer.toString()
}

public fun Config.evalVar(name: String): String {
    return tryGet(name) ?: System.getProperty(name) ?: System.getenv(name) ?: name
}
