package kara.tests.mock

import java.io.ByteArrayOutputStream
import java.io.PrintWriter
import java.util.*
import javax.servlet.ServletOutputStream
import javax.servlet.http.Cookie
import javax.servlet.http.HttpServletResponse

class MockHttpServletResponse() : HttpServletResponse {

    val _outSteam = ByteArrayOutputStream()
    var _status = HttpServletResponse.SC_OK
    var _contentType : String? = null

    fun stringOutput() : String? {
        _outSteam.flush()
        return String(_outSteam.toByteArray()!!, Charsets.UTF_8)
    }

    override fun sendError(p0 : Int) {
        _status = p0
        _outSteam.reset()
    }
    override fun sendError(p0 : Int, p1 : String?) {
        _status = p0
        _outSteam.reset()
        val writer = PrintWriter(_outSteam)
        p1?.let {writer.write(it)}
        writer.flush()
    }
    override fun sendRedirect(p0 : String?) {
        _status = HttpServletResponse.SC_MOVED_TEMPORARILY
        _outSteam.reset()
        val writer = PrintWriter(_outSteam)
        writer.write(p0!!)
        writer.flush()
    }
    override fun encodeURL(p0 : String?) : String? {
        throw UnsupportedOperationException()
    }
    override fun getHeaderNames() : MutableCollection<String>? {
        throw UnsupportedOperationException()
    }
    override fun encodeUrl(p0 : String?) : String? {
        throw UnsupportedOperationException()
    }
    override fun encodeRedirectUrl(p0 : String?) : String? {
        throw UnsupportedOperationException()
    }
    override fun addCookie(p0 : Cookie?) {
    }
    override fun setIntHeader(p0 : String?, p1 : Int) {
        throw UnsupportedOperationException()
    }
    override fun getHeader(p0 : String?) : String? {
        throw UnsupportedOperationException()
    }
    override fun addDateHeader(p0 : String?, p1 : Long) {
        throw UnsupportedOperationException()
    }
    override fun getStatus() : Int {
        return _status
    }
    override fun setStatus(p0 : Int, p1 : String?) {
        throw UnsupportedOperationException()
    }
    override fun setStatus(p0 : Int) {
        throw UnsupportedOperationException()
    }
    override fun containsHeader(p0 : String?) : Boolean {
        throw UnsupportedOperationException()
    }
    override fun setHeader(p0 : String?, p1 : String?) {
        throw UnsupportedOperationException()
    }
    override fun encodeRedirectURL(p0 : String?) : String? {
        throw UnsupportedOperationException()
    }
    override fun addHeader(p0 : String?, p1 : String?) {
        throw UnsupportedOperationException()
    }
    override fun addIntHeader(p0 : String?, p1 : Int) {
        throw UnsupportedOperationException()
    }
    override fun getHeaders(p0 : String?) : MutableCollection<String>? {
        throw UnsupportedOperationException()
    }
    override fun setDateHeader(p0 : String?, p1 : Long) {
        throw UnsupportedOperationException()
    }
    override fun setContentType(p0 : String?) {
        _contentType = p0
    }
    override fun getCharacterEncoding() : String? {
        throw UnsupportedOperationException()
    }
    override fun getWriter() : PrintWriter? {
        return PrintWriter(_outSteam, true)
    }
    override fun getBufferSize() : Int {
        throw UnsupportedOperationException()
    }
    override fun setBufferSize(p0 : Int) {
        throw UnsupportedOperationException()
    }
    override fun resetBuffer() {
        throw UnsupportedOperationException()
    }
    override fun setLocale(p0 : Locale?) {
        throw UnsupportedOperationException()
    }
    override fun setContentLength(p0 : Int) {
        throw UnsupportedOperationException()
    }
    override fun getOutputStream() : ServletOutputStream? {
        throw UnsupportedOperationException()
    }
    override fun flushBuffer() {
        throw UnsupportedOperationException()
    }
    override fun isCommitted() : Boolean {
        throw UnsupportedOperationException()
    }
    override fun getContentType() : String? {
        throw UnsupportedOperationException()
    }
    override fun getLocale() : Locale? {
        throw UnsupportedOperationException()
    }
    override fun setCharacterEncoding(p0 : String?) {
        throw UnsupportedOperationException()
    }
    override fun reset() {
        throw UnsupportedOperationException()
    }

    override fun setContentLengthLong(p0: Long) {
        throw UnsupportedOperationException()
    }
}
