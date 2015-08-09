package kara.tests.mock

import javax.servlet.http.HttpSession
import javax.servlet.ServletContext
import javax.servlet.http.HttpSessionContext
import java.util.Enumeration
import java.sql.Date
import java.util.Hashtable

/**
 * Stub out the HttpSession interface for testing.
 */
class MockHttpSession : HttpSession {

    val attributes = Hashtable<String,Any>()

    public override fun getCreationTime(): Long {
        return java.util.Date().time
    }
    public override fun getId(): String? {
        return "mock"
    }
    public override fun getLastAccessedTime(): Long {
        return java.util.Date().time
    }
    public override fun getServletContext(): ServletContext? {
        throw UnsupportedOperationException()
    }
    public override fun setMaxInactiveInterval(p0: Int) {
        throw UnsupportedOperationException()
    }
    public override fun getMaxInactiveInterval(): Int {
        throw UnsupportedOperationException()
    }
    public override fun getSessionContext(): HttpSessionContext? {
        throw UnsupportedOperationException()
    }
    public override fun getAttribute(p0: String?): Any? {
        return attributes[p0]
    }
    public override fun getValue(p0: String?): Any? {
        throw UnsupportedOperationException()
    }
    public override fun getAttributeNames(): Enumeration<String>? {
        return attributes.keys()
    }
    public override fun getValueNames(): Array<String>? {
        val set = attributes.keySet()
        val iter = set.iterator()
        return Array<String>(set.size()) {
            iter.next()
        }
    }
    public override fun setAttribute(p0: String?, p1: Any?) {
        attributes[p0!!] = p1!!
    }
    public override fun putValue(p0: String?, p1: Any?) {
    }
    public override fun removeAttribute(p0: String?) {
    }
    public override fun removeValue(p0: String?) {
    }
    public override fun invalidate() {
    }
    public override fun isNew(): Boolean {
        return true
    }

}
