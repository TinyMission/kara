package kotlin.html.bootstrap

import kara.*

Get("/resources/sausa.js")
class SausaJS() : EmbeddedResource("text/javascript", "srcipts/sausa.js") {}

Get("/resources/sausa.min.js")
class SausaMinJS() : EmbeddedResource("text/javascript", "srcipts/sausa.min.js") {}
