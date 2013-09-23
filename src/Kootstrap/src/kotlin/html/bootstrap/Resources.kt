package kotlin.html.bootstrap

import kara.*

Get("/static/scripts/sausa.js")
class SausaJS() : EmbeddedResource("text/javascript", "content/sausa.js") {}

Get("/static/scripts/sausa.min.js")
class SausaMinJS() : EmbeddedResource("text/javascript", "content/sausa.min.js") {}

Get("/static/css/bootstrap.css")
class Bootstrap() : EmbeddedResource("text/css", "content/bootstrap.css") {}

Get("/static/css/bootstrap.min.css")
class BootstrapMin() : EmbeddedResource("text/css", "content/bootstrap.min.css") {}
