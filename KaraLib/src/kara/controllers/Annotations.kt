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
