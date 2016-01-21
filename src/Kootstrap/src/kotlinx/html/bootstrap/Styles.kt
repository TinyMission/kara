package kotlinx.html.bootstrap

import kotlinx.html.*

public fun s(name : String) : StyleClass = SimpleClassStyle(name)

val muted = "text-muted"
val img_thumbnail = "img-thumbnail"

val media = "media"
val media_body = "media-body"
val media_object = "media-object"
val media_heading = "media-heading"

val collapse = "collapse"
val close = "close"
val active = "active"
val list_unstyled = "list-unstyled"

val pull_left = "pull-left"
val pull_right = "pull-right"

val hidden_tiny = "hidden-xs"
val hidden_small = "hidden-sm"
val hidden_medium = "hidden-md"
val hidden_large = "hidden-lg"
val visible_tiny = "visible-xs"
val visible_small = "visible-sm"
val visible_medium = "visible-md"
val visible_large = "visible-lg"


enum class caliber(val value: String) {
    tiny("xs"),
    small("sm"),
    default(""),
    large("lg")
}

enum class highlight {
    default,
    muted,
    primary,
    success,
    info,
    warning,
    danger
}
