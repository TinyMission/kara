package kotlin.html.bootstrap

import kotlin.html.*

public fun s(name : String) : StyleClass = SimpleClassStyle(name)

val muted = s("text-muted")
val img_thumbnail = s("img-thumbnail")

val media = s("media")
val media_body = s("media-body")
val media_object = s("media-object")
val media_heading = s("media-heading")

val collapse = s("collapse")
val close = s("close")
val active = s("active")
val list_unstyled = s("list-unstyled")

val pull_left = s("pull-left")
val pull_right = s("pull-right")

val hidden_tiny = s("hidden-xs")
val hidden_small = s("hidden-sm")
val hidden_medium = s("hidden-md")
val hidden_large = s("hidden-lg")
val visible_tiny = s("visible-xs")
val visible_small = s("visible-sm")
val visible_medium = s("visible-md")
val visible_large = s("visible-lg")


enum class caliber(val value: String) {
    tiny : caliber("xs")
    small : caliber("sm")
    default : caliber("")
    large : caliber("lg")
}

enum class highlight {
    `default`
    muted
    primary
    success
    info
    warning
    danger
}
