package kotlin.html.bootstrap

import kara.EmbeddedLessResource
import kara.EmbeddedResource
import kara.Get

@Get("/resources/scripts/sausa.js")
object SausaJS : EmbeddedResource("text/javascript", "content/sausa.js")

@Get("/resources/scripts/bootstrap.js")
object BootstrapJS : EmbeddedResource("text/javascript", "content/bootstrap.js")

@Get("/resources/css/bootstrap.css")
object Bootstrap : EmbeddedLessResource("content/less/bootstrap.less")
