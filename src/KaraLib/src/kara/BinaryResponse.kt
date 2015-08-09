package kara

import java.io.InputStream

public class BinaryResponse(val mime: String, val length: Int?, val modified : Long?, val etag: String?, val streamData: ActionContext.() -> InputStream) : ActionResult {

    override fun writeResponse(context: ActionContext) {
        val r = context.response

        val ifModifiedSince = context.request.getDateHeader("If-Modified-Since")
        val ifNoneMatch = context.request.getHeader("If-None-Match")

        r.addHeader("Content-Type", mime)

        if (length != null) {
            r.addIntHeader("Content-Length", length)
        }

        if (modified != null) {
            r.setDateHeader("Last-Modified", modified)
        }

        if (etag != null) {
            r.addHeader("ETag", etag)
            r.addHeader("Cache-Control", "max-age=7776000")
        }

        when {
            etag != null && ifNoneMatch == etag -> {
                r.status = 304
            }

            ifModifiedSince >= 0 && (modified ?: 0) / 1000 <= ifModifiedSince / 1000 -> {
                r.status = 304
            }

            else -> {
                val stream = context.streamData()
                try {
                    stream.copyTo(r.outputStream!!)
                } finally {
                    stream.close()
                }
            }
        }
    }
}
