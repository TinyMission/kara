package kara.tests.mock

import java.util.*
import javax.servlet.ServletContext
import javax.servlet.http.HttpSession
import javax.servlet.http.HttpSessionContext

/**
 * Stub out the HttpSession interface for testing.
 */
class MockHttpSession : HttpSession {

    val attributes = Hashtable<String,Any>()

    override fun getCreationTime(): Long {
        return java.util.Date().time
    }
    override fun getId(): String? {
        return "mock"
    }
    override fun getLastAccessedTime(): Long {
        return java.util.Date().time
    }
    override fun getServletContext(): ServletContext? {
        throw UnsupportedOperationException()
    }
    override fun setMaxInactiveInterval(p0: Int) {
        throw UnsupportedOperationException()
    }
    override fun getMaxInactiveInterval(): Int {
        throw UnsupportedOperationException()
    }
    override fun getSessionContext(): HttpSessionContext? {
        throw UnsupportedOperationException()
    }
    override fun getAttribute(p0: String?): Any? {
        return attributes[p0]
    }
    override fun getValue(p0: String?): Any? {
        throw UnsupportedOperationException()
    }
    override fun getAttributeNames(): Enumeration<String>? {
        return attributes.keys()
    }
    override fun getValueNames(): Array<String>? {
        val set = attributes.keys
        val iter = set.iterator()
        return Array<String>(set.size) {
            iter.next()
        }
    }
    override fun setAttribute(p0: String?, p1: Any?) {
        attributes[p0!!] = p1!!
    }
    override fun putValue(p0: String?, p1: Any?) {
    }
    override fun removeAttribute(p0: String?) {
    }
    override fun removeValue(p0: String?) {
    }
    override fun invalidate() {
    }
    override fun isNew(): Boolean {
        return true
    }

}
