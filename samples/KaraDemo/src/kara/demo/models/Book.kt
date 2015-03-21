package kara.demo.models

enum class BookCategory(val value : String) {
    Fiction: BookCategory("fiction")
    Nonfiction: BookCategory("nonfiction")
    override fun toString() : String {
        return value
    }
}

/**
 * Example model that represents a book.
 */
class Book(var title : String = "", init : Book.() -> Unit = {}) {

    var author : String = ""

    var isPublished : Boolean = false

    var description : String = ""

    var category : BookCategory = BookCategory.Fiction

    {
        this.init()
    }
}
