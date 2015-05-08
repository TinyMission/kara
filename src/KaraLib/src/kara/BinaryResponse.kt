package kara

import java.io.InputStream

public class BinaryResponse(val mime: String, val length: Int, val modified : Long, val etag: String?, val streamData: ActionContext.() -> InputStream) : ActionResult {

    override fun writeResponse(context: ActionContext) {
        val r = context.response

        val ifModifiedSince = context.request.getDateHeader("If-Modified-Since")
        val ifNoneMatch = context.request.getHeader("If-None-Match")

        r.addHeader("Content-Type", mime)
        r.addIntHeader("Content-Length", length)

        if (modified > 0) {
            r.setDateHeader("Last-Modified", modified)
        }

        if (etag != null) {
            r.addHeader("ETag", etag)
            r.addHeader("Cache-Control", "max-age=7776000")
        }

        when {
            ifNoneMatch == etag || ifModifiedSince >= 0 && modified / 1000 <= ifModifiedSince / 1000 -> {
                r.setStatus(304)
            }

            else -> {
                val stream = context.streamData()
                try {
                    stream.copyTo(r.getOutputStream()!!)
                } finally {
                    stream.close()
                }
            }
        }
    }
}
