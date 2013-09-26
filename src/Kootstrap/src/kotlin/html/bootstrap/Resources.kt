package kotlin.html.bootstrap

import kara.*

Get("/static/scripts/sausa.js")
object SausaJS : EmbeddedResource("text/javascript", "content/sausa.js") {}

Get("/static/scripts/sausa.min.js")
object SausaMinJS : EmbeddedResource("text/javascript", "content/sausa.min.js") {}

Get("/static/scripts/bootstrap.js")
object BootstrapJS : EmbeddedResource("text/javascript", "content/bootstrap.js") {}

Get("/static/scripts/bootstrap.min.js")
object BootstrapMinJS : EmbeddedResource("text/javascript", "content/bootstrap.min.js") {}

Get("/static/css/bootstrap.css")
object Bootstrap : EmbeddedResource("text/css", "content/bootstrap.css") {}

Get("/static/css/bootstrap.min.css")
object BootstrapMin : EmbeddedResource("text/css", "content/bootstrap.min.css") {}
