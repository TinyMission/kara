package kara

import java.io.InputStream

public class BinaryResponse(val mime: String, val length: Int, val modified : Long, val streamData: () -> InputStream) : ActionResult {

    override fun writeResponse(context: ActionContext) {
        val r = context.response

        val ifModifiedSince = context.request.getDateHeader("If-Modified-Since")

        if (ifModifiedSince >= 0 && modified/1000 <= ifModifiedSince/1000) {
            r.setStatus(304)
        }
        else {
            r.addHeader("Content-Type", mime)
            r.addIntHeader("Content-Length", length)

            if (modified > 0) {
                r.setDateHeader("Last-Modified", modified)
            }

            val stream = streamData()
            try {
                stream.copyTo(r.getOutputStream()!!)
            }
            finally {
                stream.close()
            }
        }
    }
}
