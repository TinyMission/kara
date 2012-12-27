package karatests.mock

import javax.servlet.http.HttpServletRequest
import java.security.Principal
import javax.servlet.http.HttpSession
import java.util.Enumeration
import javax.servlet.RequestDispatcher
import javax.servlet.ServletInputStream
import java.util.Locale
import java.io.BufferedReader
import javax.servlet.http.Cookie


class MockHttpServletRequest(method : String, url : String) : HttpServletRequest {

    var _method : String = method
    var _url : String = url

    public override fun startAsync(val p0 : javax.servlet.ServletRequest?, val p1 : javax.servlet.ServletResponse?) : javax.servlet.AsyncContext? {
        throw UnsupportedOperationException()
    }
    public override fun startAsync() : javax.servlet.AsyncContext? {
        throw UnsupportedOperationException()
    }
    public override fun getPart(val p0 : jet.String?) : javax.servlet.http.Part? {
        throw UnsupportedOperationException()
    }
    public override fun isAsyncSupported() : Boolean {
        return false
    }
    public override fun isAsyncStarted() : Boolean {
        return false
    }
    public override fun authenticate(val p0 : javax.servlet.http.HttpServletResponse?) : jet.Boolean {
        return false
    }
    public override fun logout() : Unit {
    }
    public override fun login(val p0 : jet.String?, val p1 : jet.String?) : Unit {
    }
    public override fun getAsyncContext() : javax.servlet.AsyncContext? {
        throw UnsupportedOperationException()
    }
    public override fun getDispatcherType() : javax.servlet.DispatcherType? {
        throw UnsupportedOperationException()
    }
    public override fun getServletContext() : javax.servlet.ServletContext? {
        throw UnsupportedOperationException()
    }
    public override fun getParts() :MutableCollection<javax.servlet.http.Part?>? {
        throw UnsupportedOperationException()
    }

    public override fun getUserPrincipal() : Principal? {
        throw UnsupportedOperationException()
    }
    public override fun getPathInfo() : String? {
        throw UnsupportedOperationException()
    }
    public override fun getSession() : HttpSession? {
        throw UnsupportedOperationException()
    }
    public override fun getSession(p0 : Boolean) : HttpSession? {
        throw UnsupportedOperationException()
    }
    public override fun getHeaders(p0 : String?) : Enumeration<String?>? {
        throw UnsupportedOperationException()
    }
    public override fun isRequestedSessionIdFromCookie() : Boolean {
        throw UnsupportedOperationException()
    }
    public override fun getHeaderNames() : Enumeration<String?>? {
        throw UnsupportedOperationException()
    }
    public override fun getRemoteUser() : String? {
        throw UnsupportedOperationException()
    }
    public override fun getMethod() : String? {
        return _method
    }
    public override fun getServletPath() : String? {
        throw UnsupportedOperationException()
    }
    public override fun getRequestURL() : StringBuffer? {
        throw UnsupportedOperationException()
    }
    public override fun isUserInRole(p0 : String?) : Boolean {
        throw UnsupportedOperationException()
    }
    public override fun getCookies() : Array<Cookie?>? {
        throw UnsupportedOperationException()
    }
    public override fun getRequestURI() : String? {
        return _url
    }
    public override fun getQueryString() : String? {
        val comps = _url.split("\\?")
        if (comps.size > 1)
            return comps[1]
        return null
    }
    public override fun isRequestedSessionIdValid() : Boolean {
        throw UnsupportedOperationException()
    }
    public override fun getPathTranslated() : String? {
        throw UnsupportedOperationException()
    }
    public override fun isRequestedSessionIdFromUrl() : Boolean {
        throw UnsupportedOperationException()
    }
    public override fun getContextPath() : String? {
        throw UnsupportedOperationException()
    }
    public override fun getIntHeader(p0 : String?) : Int {
        throw UnsupportedOperationException()
    }
    public override fun getDateHeader(p0 : String?) : Long {
        throw UnsupportedOperationException()
    }
    public override fun getAuthType() : String? {
        throw UnsupportedOperationException()
    }
    public override fun getHeader(p0 : String?) : String? {
        throw UnsupportedOperationException()
    }
    public override fun isRequestedSessionIdFromURL() : Boolean {
        throw UnsupportedOperationException()
    }
    public override fun getRequestedSessionId() : String? {
        throw UnsupportedOperationException()
    }
    public override fun getLocalPort() : Int {
        throw UnsupportedOperationException()
    }
    public override fun getRequestDispatcher(p0 : String?) : RequestDispatcher? {
        throw UnsupportedOperationException()
    }
    public override fun getAttribute(p0 : String?) : Any? {
        throw UnsupportedOperationException()
    }
    public override fun getParameter(p0 : String?) : String? {
        throw UnsupportedOperationException()
    }
    public override fun getRemoteHost() : String? {
        throw UnsupportedOperationException()
    }
    public override fun getAttributeNames() : Enumeration<String?>? {
        throw UnsupportedOperationException()
    }
    public override fun getParameterNames() : Enumeration<String?>? {
        throw UnsupportedOperationException()
    }
    public override fun setAttribute(p0 : String?, p1 : Any?) {
        throw UnsupportedOperationException()
    }
    public override fun setCharacterEncoding(p0 : String?) {
        throw UnsupportedOperationException()
    }
    public override fun isSecure() : Boolean {
        throw UnsupportedOperationException()
    }
    public override fun getScheme() : String? {
        throw UnsupportedOperationException()
    }
    public override fun getLocalName() : String? {
        throw UnsupportedOperationException()
    }
    public override fun getCharacterEncoding() : String? {
        throw UnsupportedOperationException()
    }
    public override fun getServerName() : String? {
        throw UnsupportedOperationException()
    }
    public override fun getProtocol() : String? {
        throw UnsupportedOperationException()
    }
    public override fun getContentLength() : Int {
        throw UnsupportedOperationException()
    }
    public override fun getRealPath(p0 : String?) : String? {
        throw UnsupportedOperationException()
    }
    public override fun getParameterMap() : MutableMap<String?, Array<String?>?>? {
        throw UnsupportedOperationException()
    }
    public override fun getInputStream() : ServletInputStream? {
        throw UnsupportedOperationException()
    }
    public override fun getLocalAddr() : String? {
        throw UnsupportedOperationException()
    }
    public override fun removeAttribute(p0 : String?) {
        throw UnsupportedOperationException()
    }
    public override fun getServerPort() : Int {
        throw UnsupportedOperationException()
    }
    public override fun getRemoteAddr() : String? {
        throw UnsupportedOperationException()
    }
    public override fun getLocales() : Enumeration<Locale?>? {
        throw UnsupportedOperationException()
    }
    public override fun getLocale() : Locale? {
        throw UnsupportedOperationException()
    }
    public override fun getContentType() : String? {
        throw UnsupportedOperationException()
    }
    public override fun getRemotePort() : Int {
        throw UnsupportedOperationException()
    }
    public override fun getParameterValues(p0 : String?) : Array<String?>? {
        throw UnsupportedOperationException()
    }
    public override fun getReader() : BufferedReader? {
        throw UnsupportedOperationException()
    }

}
