package kotlin.html.bootstrap

import kara.*

Get("/static/scripts/sausa.js")
class SausaJS() : EmbeddedResource("text/javascript", "content/sausa.js") {}

Get("/static/scripts/sausa.min.js")
class SausaMinJS() : EmbeddedResource("text/javascript", "content/sausa.min.js") {}

Get("/static/scripts/bootstrap.js")
class BootstrapJS() : EmbeddedResource("text/javascript", "content/bootstrap.js") {}

Get("/static/scripts/bootstrap.min.js")
class BootstrapMinJS() : EmbeddedResource("text/javascript", "content/bootstrap.min.js") {}

Get("/static/css/bootstrap.css")
class Bootstrap() : EmbeddedResource("text/css", "content/bootstrap.css") {}

Get("/static/css/bootstrap.min.css")
class BootstrapMin() : EmbeddedResource("text/css", "content/bootstrap.min.css") {}
