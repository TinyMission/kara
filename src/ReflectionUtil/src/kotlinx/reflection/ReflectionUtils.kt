package kotlinx.reflection

import java.io.File
import java.lang.reflect.Modifier
import java.net.URL
import java.util.concurrent.ConcurrentHashMap
import java.util.jar.JarFile
import kotlin.reflect.*
import kotlin.reflect.jvm.javaType

private object ReflectionCache {
    val objects = ConcurrentHashMap<Class<*>, Any>()
    val companionObjects = ConcurrentHashMap<Class<*>, Any>()
    val primaryParameters = ConcurrentHashMap<Class<out Any>, List<KParameter>>()
    val propertyGetters = ConcurrentHashMap<Pair<KClass<out Any>, String>, Any>()
}

private object NullMask
private fun Any.unmask():Any? = if (this == NullMask) null else this

@Suppress("UNCHECKED_CAST")
@Deprecated("use KClass<T>.objectInstance. For backword compatibilibty test only.")
fun <T:Any> Class<T>.objectInstance0(): T? {
    return ReflectionCache.objects.concurrentGetOrPut(this) {
        try {
            val field = getDeclaredField("INSTANCE\$")
            if (Modifier.isStatic(field.modifiers) && Modifier.isPublic(field.modifiers)) {
                if (!field.isAccessible) {
                    field.isAccessible = true
                }
                field.get(null)!!
            }
            else NullMask
        }
        catch (e: NoSuchFieldException) {
            NullMask
        }
    }.unmask() as T
}

fun Class<*>.companionObjectInstance(): Any? {
    return ReflectionCache.companionObjects.concurrentGetOrPut(this) {
        kotlin.companionObjectInstance ?: NullMask
    }.unmask()
}

@Suppress("UNCHECKED_CAST")
fun <T: Any, R:Any?> KClass<out T>.propertyGetter(property: String): KProperty1<T, R>? {
    return ReflectionCache.propertyGetters.concurrentGetOrPut(Pair(this, property),  {
        memberProperties.firstOrNull { it.name == property } ?: NullMask
    }).unmask() as KProperty1<T, R>?
}


fun <T:Any, R:Any?> T.propertyValue(property: String): R? {
    val getter = javaClass.kotlin.propertyGetter<T,R>(property) ?: error("Invalid property $property on type ${javaClass.name}")
    return getter.get(this)
}

public class MissingArgumentException(message: String) : RuntimeException(message)

@Suppress("UNCHECKED_CAST")
fun <T:Any> Class<T>.buildBeanInstance(allParams: Map<String, String>): T {
    kotlin.objectInstance?.let {
        return it
    }

    val args = primaryParameters.map { param ->
        param to (run {
            val stringValue = allParams[param.name]
            when {
                stringValue == "null" && param.type.isMarkedNullable -> null
                stringValue != null -> Serialization.deserialize(stringValue, param.type.javaType as Class<Any>, classLoader)
                param.isOptional -> NullMask
                param.type.isMarkedNullable -> null
                else -> throw MissingArgumentException("Required argument '${param.name}' is missing, available params: $allParams")
            }
        })
    }.filter { it.second != NullMask }.toMap()

    return kotlin.primaryConstructor!!.callBy(args)
}

val <T:Any> Class<T>.primaryParameters : List<KParameter> get() {
    return ReflectionCache.primaryParameters.concurrentGetOrPut(this) {
        kotlin.primaryConstructor?.parameters.orEmpty()
    }
}

fun Any.primaryParametersNames() = javaClass.primaryParameters.map { it.name!! }

public fun Class<*>.isEnumClass(): Boolean = Enum::class.java.isAssignableFrom(this)

fun ClassLoader.findClasses(prefix: String, cache: MutableMap<Pair<Int, String>, List<Class<*>>>) : List<Class<*>> {
    synchronized(cache) {
        return cache.getOrPut(this.hashCode() to prefix) {
            scanForClasses(prefix)
        }
    }
}

fun ClassLoader.scanForClasses(prefix: String) : List<Class<*>> {
    val urls = arrayListOf<URL>()
    val clazzz = arrayListOf<Class<*>>()
    val path = prefix.replace(".", "/")
    val enumeration = this.getResources(path)
    while(enumeration.hasMoreElements()) {
        urls.add(enumeration.nextElement())
    }
    clazzz.addAll(urls.map {
        it.scanForClasses(prefix, this@scanForClasses)
    }.flatten())
    return clazzz
}

private fun URL.scanForClasses(prefix: String = "", classLoader: ClassLoader): List<Class<*>> {
    return when (protocol) {
        "jar" -> JarFile(urlDecode(toExternalForm().substringAfter("file:").substringBeforeLast("!"))).scanForClasses(prefix, classLoader)
        else -> File(urlDecode(path)).scanForClasses(prefix, classLoader)
    }
}

private fun String.packageToPath() = replace(".", File.separator) + File.separator

private fun isAnonClass(name: String): Boolean {
    var idx = name.indexOf('$')

    while (idx >= 0) {
        if (idx + 1 < name.length() && name[idx + 1] in '0'..'9') return true
        idx = name.indexOf('$', idx + 1)
    }

    return false
}

private fun File.scanForClasses(prefix: String, classLoader: ClassLoader): List<Class<*>> {
    val path = prefix.packageToPath()
    return FileTreeWalk(this, filter = {
        it.isDirectory || (it.isFile && it.extension == "class" && !isAnonClass(it.name))
    }).toList()
    .filter{
        it.isFile && it.absolutePath.contains(path)
    }.map {
        classLoader.loadClass(prefix +"." + it.absolutePath.substringAfterLast(path).removeSuffix(".class").replace(File.separator, "."))
    }.filterNotNull().toList()
}

private fun JarFile.scanForClasses(prefix: String, classLoader: ClassLoader): List<Class<*>> {
    val classes = arrayListOf<Class<*>>()
    val path = prefix.replace(".", "/") + "/"
    val entries = this.entries()
    while(entries.hasMoreElements()) {
        entries.nextElement().let {
            if (!it.isDirectory && it.name.endsWith(".class") && it.name.contains(path) && !isAnonClass(it.name)) {
                classLoader.loadClass(prefix + "." + it.name.substringAfterLast(path).removeSuffix(".class").replace("/", "."))?.let{
                    classes.add(it)
                }
            }
        }
    }
    return classes
}

@Suppress("UNCHECKED_CAST")
fun <T> Iterable<Class<*>>.filterIsAssignable(clazz: Class<T>): List<Class<T>> = filter { clazz.isAssignableFrom(it) } as List<Class<T>>

@Suppress("UNCHECKED_CAST")
inline fun <reified T: Any> Iterable<Class<*>>.filterIsAssignable(): List<Class<T>> = filterIsAssignable(T::class.java)

fun <T:Any?> Array<T>.safeGet(index: Int): T? {
    return if (index in this.indices) this[index] else null
}
