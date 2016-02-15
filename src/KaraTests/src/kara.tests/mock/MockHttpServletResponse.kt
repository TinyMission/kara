package kara.tests.mock

import javax.servlet.http.HttpServletResponse
import javax.servlet.http.Cookie
import java.io.PrintWriter
import java.util.Locale
import javax.servlet.ServletOutputStream
import java.io.ByteArrayOutputStream

class MockHttpServletResponse() : HttpServletResponse {

    val _outSteam = ByteArrayOutputStream()
    var _status = HttpServletResponse.SC_OK
    public var _contentType : String? = null

    public fun stringOutput() : String? {
        _outSteam.flush()
        return String(_outSteam.toByteArray()!!, Charsets.UTF_8)
    }

    public override fun sendError(p0 : Int) {
        _status = p0
        _outSteam.reset()
    }
    public override fun sendError(p0 : Int, p1 : String?) {
        _status = p0
        _outSteam.reset()
        val writer = PrintWriter(_outSteam)
        p1?.let {writer.write(it)}
        writer.flush()
    }
    public override fun sendRedirect(p0 : String?) {
        _status = HttpServletResponse.SC_MOVED_TEMPORARILY
        _outSteam.reset()
        val writer = PrintWriter(_outSteam)
        writer.write(p0!!)
        writer.flush()
    }
    public override fun encodeURL(p0 : String?) : String? {
        throw UnsupportedOperationException()
    }
    public override fun getHeaderNames() : MutableCollection<String>? {
        throw UnsupportedOperationException()
    }
    public override fun encodeUrl(p0 : String?) : String? {
        throw UnsupportedOperationException()
    }
    public override fun encodeRedirectUrl(p0 : String?) : String? {
        throw UnsupportedOperationException()
    }
    public override fun addCookie(p0 : Cookie?) {
    }
    public override fun setIntHeader(p0 : String?, p1 : Int) {
        throw UnsupportedOperationException()
    }
    public override fun getHeader(p0 : String?) : String? {
        throw UnsupportedOperationException()
    }
    public override fun addDateHeader(p0 : String?, p1 : Long) {
        throw UnsupportedOperationException()
    }
    public override fun getStatus() : Int {
        return _status
    }
    public override fun setStatus(p0 : Int, p1 : String?) {
        throw UnsupportedOperationException()
    }
    public override fun setStatus(p0 : Int) {
        throw UnsupportedOperationException()
    }
    public override fun containsHeader(p0 : String?) : Boolean {
        throw UnsupportedOperationException()
    }
    public override fun setHeader(p0 : String?, p1 : String?) {
        throw UnsupportedOperationException()
    }
    public override fun encodeRedirectURL(p0 : String?) : String? {
        throw UnsupportedOperationException()
    }
    public override fun addHeader(p0 : String?, p1 : String?) {
        throw UnsupportedOperationException()
    }
    public override fun addIntHeader(p0 : String?, p1 : Int) {
        throw UnsupportedOperationException()
    }
    public override fun getHeaders(p0 : String?) : MutableCollection<String>? {
        throw UnsupportedOperationException()
    }
    public override fun setDateHeader(p0 : String?, p1 : Long) {
        throw UnsupportedOperationException()
    }
    public override fun setContentType(p0 : String?) {
        _contentType = p0
    }
    public override fun getCharacterEncoding() : String? {
        throw UnsupportedOperationException()
    }
    public override fun getWriter() : PrintWriter? {
        return PrintWriter(_outSteam, true)
    }
    public override fun getBufferSize() : Int {
        throw UnsupportedOperationException()
    }
    public override fun setBufferSize(p0 : Int) {
        throw UnsupportedOperationException()
    }
    public override fun resetBuffer() {
        throw UnsupportedOperationException()
    }
    public override fun setLocale(p0 : Locale?) {
        throw UnsupportedOperationException()
    }
    public override fun setContentLength(p0 : Int) {
        throw UnsupportedOperationException()
    }
    public override fun getOutputStream() : ServletOutputStream? {
        throw UnsupportedOperationException()
    }
    public override fun flushBuffer() {
        throw UnsupportedOperationException()
    }
    public override fun isCommitted() : Boolean {
        throw UnsupportedOperationException()
    }
    public override fun getContentType() : String? {
        throw UnsupportedOperationException()
    }
    public override fun getLocale() : Locale? {
        throw UnsupportedOperationException()
    }
    public override fun setCharacterEncoding(p0 : String?) {
        throw UnsupportedOperationException()
    }
    public override fun reset() {
        throw UnsupportedOperationException()
    }

    override fun setContentLengthLong(p0: Long) {
        throw UnsupportedOperationException()
    }
}
