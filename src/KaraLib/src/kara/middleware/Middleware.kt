package kara.middleware

/**
 * Base class for Kara middleware.
 * Middleware is code that is injected inside the request pipeline,
 * either before or after a request is handled by the application.
 */
import kara.views.ActionContext

abstract class Middleware() {

    /**
     * Gets called before the application is allowed to handle the request.
     * Return false to stop the request pipeline from executing anything else.
     */
    abstract fun beforeRequest(context : ActionContext) : Boolean

    /**
     * Gets called after the application is allowed to handle the request.
     * Return false to stop the request pipeline from executing anything else.
     */
    abstract fun afterRequest(context : ActionContext) : Boolean

}