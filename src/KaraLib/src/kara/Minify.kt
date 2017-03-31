package kara

import com.google.javascript.jscomp.*
import com.yahoo.platform.yui.compressor.CssCompressor
import jj.org.mozilla.javascript.ErrorReporter
import jj.org.mozilla.javascript.EvaluatorException
import org.apache.log4j.Logger
import org.slf4j.LoggerFactory
import java.io.StringWriter

fun ByteArray.minifyResource(context: ActionContext, mime: String, fileName: String): ByteArray {
    try {
        return when {
            !context.config.minifyResrouces() -> return this
            (mime == "text/javascript" || mime == "application/javascript") && !fileName.endsWith(".min.js") -> ClosureCompiler.compile(this, fileName)
            mime == "text/css" -> compressCss()
            else -> this
        }
    }
    catch(e: Throwable) {
        MinifierReporter.log.warn("Minification failed at $fileName will be used unminified version.", e)
        return this
    }
}

object ClosureCompiler {
    private val externs = CommandLineRunner.getBuiltinExterns(CompilerOptions.Environment.BROWSER)
    private val options = CompilerOptions().apply {
        languageIn = CompilerOptions.LanguageMode.ECMASCRIPT5
        languageOut = CompilerOptions.LanguageMode.ECMASCRIPT5
        CompilationLevel.SIMPLE_OPTIMIZATIONS.setOptionsForCompilationLevel(this)
    }

    object LogOnlyErrorManager : ErrorManager {
        private val logger = LoggerFactory.getLogger(javaClass)

        override fun report(checkLevel: CheckLevel?, jsError: JSError?) {
            fun JSError.message () =
                    "Minification failed at $lineNumber:$charno:${sourceName?.let {" at '$it'"}.orEmpty()}: $description"
            jsError?.let {
                when (checkLevel) {
                    CheckLevel.ERROR -> logger.error(it.message())
                    CheckLevel.WARNING -> logger.warn(it.message())
                    else -> logger.warn("Unknown check level $checkLevel with error: ${it.message()}")
                }
            }
        }

        override fun generateReport() { // nothing
        }

        override fun getWarnings(): Array<JSError> = emptyArray()

        override fun getTypedPercent(): Double = 0.0

        override fun getErrors(): Array<JSError> = emptyArray()

        override fun getErrorCount(): Int = 0

        override fun getWarningCount(): Int = 0

        override fun setTypedPercent(p0: Double) { // nothing
        }
    }

    fun compile(content: ByteArray, fileName: String?) : ByteArray {
        return Compiler(LogOnlyErrorManager).run {
            compile(externs, listOf(SourceFile.fromCode(fileName ?: "plain javascript", content.toString(Charsets.UTF_8))), options)
            toSource().toByteArray()
        }
    }
}

private fun ByteArray.compressCss(): ByteArray {
    val compressor = CssCompressor(this.inputStream().reader())
    val answer = StringWriter()
    compressor.compress(answer, 160)
    return answer.toString().toByteArray()
}

object MinifierReporter : ErrorReporter {
    val log = Logger.getLogger(this.javaClass)!!

    override fun warning(message: String?, sourceName: String?, line: Int, lineSource: String?, lineOffset: Int) {
        log.warn("Minification failed at $line:$lineOffset${lineSource?.let {" at '$it'"}.orEmpty()}: $message")
    }

    override fun error(message: String?, sourceName: String?, line: Int, lineSource: String?, lineOffset: Int) {
        log.error("Minification failed at $line:$lineOffset${lineSource?.let {" at '$it'"}.orEmpty()}: $message")
    }

    override fun runtimeError(message: String?, sourceName: String?, line: Int, lineSource: String?, lineOffset: Int): EvaluatorException? {
        log.error(message)
        return EvaluatorException(message)
    }
}
