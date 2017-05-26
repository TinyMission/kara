package kara.internal

import kara.*
import kotlinx.reflection.annotationClassCached
import kotlinx.reflection.filterIsAssignable
import kotlinx.reflection.findClasses
import kotlinx.reflection.kotlinCached
import java.util.*
import kotlin.reflect.KAnnotatedElement
import kotlin.reflect.KClass
import kotlin.reflect.full.declaredMemberFunctions
import kotlin.reflect.full.superclasses

val karaAnnotations = listOf(Get::class, Post::class, Put::class, Delete::class, Route::class, Location::class)

fun KAnnotatedElement.getKaraAnnotation() = annotations.find { karaAnnotations.contains(it.annotationClassCached) }

fun KClass<*>.getKaraAnnotationFromSuper() = superclasses.find{ it.ok() }?.getKaraAnnotation()

private fun KAnnotatedElement.ok() = getKaraAnnotation() != null

private fun KClass<*>.inheritAnnotatedInterfaceController() : Boolean {
    return this.superclasses.any { it.isInterfaceController() && it.ok() } && this.isFinal
}

private fun KClass<*>.isInterfaceController() : Boolean {
    return this.annotations.map { it.annotationClassCached }.contains(InterfaceController::class)
}

private fun List<KAnnotatedElement>.toResourceDescriptorWithInerited() : List<Pair<KAnnotatedElement, ResourceDescriptor>> {
    val descriptorList = ArrayList<Pair<KAnnotatedElement, ResourceDescriptor>>()
    for (element in this) {
        val karaAnnotation = element.getKaraAnnotation() ?: (element as? KClass<*>)?.getKaraAnnotationFromSuper()
        if (karaAnnotation != null) {
            descriptorList.add(element to element.route(karaAnnotation))
        }
    }
    return descriptorList
}

@Suppress("UNCHECKED_CAST")
fun scanPackageForResources(prefix: String, classloader: ClassLoader, cache: MutableMap<Pair<Int, String>, List<Class<*>>>)
        : List<Pair<KAnnotatedElement, ResourceDescriptor>> {
    try {
        val classes = classloader.findClasses(prefix, cache)
        val annotatedClasses = classes.filterIsAssignable<Resource>().map { it.kotlinCached }.filter {
            (it.ok() && !it.isInterfaceController()) || it.inheritAnnotatedInterfaceController()
        }.toResourceDescriptorWithInerited()
        val annotatedFunctions = classes.filter{ it.isAnnotationPresent(Controller::class.java) && it.kotlinCached.objectInstance != null}.
                    flatMap { it.kotlinCached.declaredMemberFunctions.filter { it.ok() } }.mapNotNull {
            it.getKaraAnnotation()?.let {karaAnnotation -> it to it.route(karaAnnotation) }
        }
        return annotatedClasses + annotatedFunctions

    } catch(e: Throwable) {
        e.printStackTrace()
        throw RuntimeException("I'm totally failed to start up. See log :(")
    }
}

/** Test only **/
fun scanObjects(objects : Array<Any>, classloader: ClassLoader? = null) : List<Pair<KAnnotatedElement, ResourceDescriptor>> {
    val answer = ArrayList<Pair<KAnnotatedElement, ResourceDescriptor>>()

    @Suppress("UNCHECKED_CAST")
    fun scan(routesObject : Any) {
        val newClass = classloader?.loadClass(routesObject.javaClass.name) ?: routesObject.javaClass
        for (innerClass in newClass.declaredClasses) {
            (innerClass as Class<Any>).kotlinCached.objectInstance?.let {
                scan(it)
            } ?: run {
                if ((innerClass.kotlinCached.ok() && !innerClass.kotlinCached.isInterfaceController()) ||
                        (innerClass.kotlinCached.inheritAnnotatedInterfaceController())) {
                    answer.add(innerClass.kotlinCached to innerClass.kotlinCached.route())
                }
            }
        }

        answer.addAll(newClass.kotlinCached.declaredMemberFunctions.filter { it.ok() }.map { it to it.route() })

    }

    for (o in objects) scan(o)

    return answer
}
