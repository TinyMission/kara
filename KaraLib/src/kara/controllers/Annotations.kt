package kara.controllers

import java.lang.annotation.*

/** Used to annotate actions that respond to GET requests.
 */
Retention(RetentionPolicy.RUNTIME) annotation class Get(val route : String = "")

/** Used to annotate actions that respond to POST requests.
 */
Retention(RetentionPolicy.RUNTIME) annotation class Post(val route : String = "")

/** Used to annotate actions that respond to PUT requests.
 */
Retention(RetentionPolicy.RUNTIME) annotation class Put(val route : String = "")

/** Used to annotate actions that respond to DELETE requests.
 */
Retention(RetentionPolicy.RUNTIME) annotation class Delete(val route : String = "")

/** Used to annotate route objects, representing alternative URL path component.
Default is / for top level root object or lowercased object name for inner objects
*/
Retention(RetentionPolicy.RUNTIME) annotation class Path(val path: String? = null)
