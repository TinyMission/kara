package kara;

import org.lesscss.LessCompiler
import java.io.InputStream
import org.lesscss.*

private val compiler = LessCompiler()

public open class EmbeddedLessResource(val name: String) : CachedResource() {
    override fun content(context: ActionContext): ResourceContent {
        synchronized(this) {
            val (lessSource, combinedModification) = resourceAndModification(context)
            val css = compiler.compile(lessSource) ?: error("$name can't be compiled")

            val bytes = css.toByteArray("UTF-8")
            return ResourceContent("text/css", combinedModification, bytes.size(), {bytes.inputStream})
        }
    }

    private fun resourceAndModification(context: ActionContext): Pair<LessSource, Long?> {
        val (modification, root) = context.loadResource(name)

        val lessSource = LessSource(HttpResource(root.toURI()))
        val combinedModification = if (modification == null) null else lessSource.getLastModifiedIncludingImports()
        return Pair(lessSource, combinedModification)
    }

    override fun validateCache(context: ActionContext, cache: ResourceCache): Boolean {
        val (lessSource, combinedModification) = resourceAndModification(context)
        return combinedModification == cache.lastModified
    }
}
