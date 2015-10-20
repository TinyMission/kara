package kara;

import org.lesscss.LessCompiler
import java.io.InputStream
import org.lesscss.*
import java.net.URI
import java.net.URL

private val compiler = LessCompiler()

public open class EmbeddedLessResource(val name: String) : CachedResource() {
    override fun content(context: ActionContext): ResourceContent {
        synchronized(this) {
            val (lessSource, combinedModification) = resourceAndModification(context)
            val css = compiler.compile(lessSource) ?: error("$name can't be compiled")

            val bytes = css.toByteArray("UTF-8")
            return ResourceContent("text/css", combinedModification, bytes.size, { bytes.inputStream() })
        }
    }

    private fun resourceAndModification(context: ActionContext): Pair<LessSource, Long?> {
        val (modification, root) = context.loadResource(name)

        val lessSource = LessSource(URLResource(root.toURI()))
        val combinedModification = if (modification == null) null else lessSource.lastModifiedIncludingImports
        return Pair(lessSource, combinedModification)
    }


    override fun validateCache(context: ActionContext, cache: ResourceCache): Boolean {
        val (lessSource, combinedModification) = resourceAndModification(context)
        return combinedModification == cache.lastModified
    }
}

public class URLResource(val uri: URI): org.lesscss.HttpResource(uri) {
    override fun createRelative(name: String?): org.lesscss.Resource? {
        val components = uri.toString().split('/')
        val newPath = "${components.take(components.size() - 1).joinToString("/")}/$name"
        return URLResource(URI(newPath))
    }
}
