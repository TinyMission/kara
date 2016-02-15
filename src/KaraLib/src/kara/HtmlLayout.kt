package kara

import kotlinx.html.*

/** Base class for html layouts.
 * A layout is an html template that renders the shared part of the page.
 * Subclasses must implement the render function to provide the layout html.
 */
abstract class HtmlLayout() {
    /** Subclasses must implement this to render the main view inside the html document.
    */
    abstract fun HTML.render(mainView : HtmlView)
}
