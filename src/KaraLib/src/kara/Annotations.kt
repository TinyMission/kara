package kara


/** Used to annotate actions that respond to HTTP requests.
 */
@Retention(AnnotationRetention.RUNTIME) annotation class Route(val method : HttpMethod, val route : String, val allowCrossOrigin: String = "")

/** Used to annotate actions that respond to HTTP GET requests.
 */
@Retention(AnnotationRetention.RUNTIME) annotation class Get(val route : String = "")

/** Used to annotate actions that respond to POST requests.
 */
@Retention(AnnotationRetention.RUNTIME) annotation class Post(val route : String = "", val allowCrossOrigin: String = "")

/** Used to annotate actions that respond to PUT requests.
 */
@Retention(AnnotationRetention.RUNTIME) annotation class Put(val route : String = "", val allowCrossOrigin: String = "")

/** Used to annotate actions that respond to DELETE requests.
 */
@Retention(AnnotationRetention.RUNTIME) annotation class Delete(val route : String = "", val allowCrossOrigin: String = "")

/** Used to annotate route objects, representing alternative URL path component.
Default is / for top level root object or lowercased object name for inner objects
*/
@Retention(AnnotationRetention.RUNTIME) annotation class Location(val path: String = "")

/** Used to annotate route objects, for which HttpSession should not be used (service calls, non-browser user agents) */
@Retention(AnnotationRetention.RUNTIME) annotation class NoSession()
