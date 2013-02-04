---
layout: default
title: Kara Middleware
isDoc: true
docPage: middleware
displayName: Middleware
icon: wrench
---

## Middleware

It is possible to write code that will get executed inside the request pipeline in Kara.
This code is known as middleware, and is defined by subclassing the kara.middleware.Middleware class:

	/**
	 * Base class for Kara middleware.
	 * Middleware is code that is injected inside the request pipeline,
	 * either before or after a request is handled by the application.
	 */
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

Once you define a middleware class (or import one from a third party source), it can be added to your application inside the application constructor (or anywhere the appConfig is available) like this:

	appConfig.middleware.add(MyMiddleware(), "/books")

In this case, the middleware will only be exectued when the path starts with */books*.
By omitting the second argument, the middleware will be executed for all requests.

