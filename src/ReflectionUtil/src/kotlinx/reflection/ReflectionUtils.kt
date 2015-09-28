package kotlinx.reflection

import java.io.File
import java.lang.reflect.Constructor
import java.lang.reflect.Modifier
import java.net.URL
import java.util.concurrent.ConcurrentHashMap
import java.util.jar.JarFile
import kotlin.reflect.*
import kotlin.reflect.jvm.javaField
import kotlin.reflect.jvm.javaGetter

object ReflectionCache {
    val objects = ConcurrentHashMap<Class<*>, Any>()
    val companionObjects = ConcurrentHashMap<Class<*>, Any>()
    val consMetadata = ConcurrentHashMap<Class<*>, Triple<Constructor<*>, Array<Class<*>>, List<KParameter>>>()
    val primaryProperites = ConcurrentHashMap<Class<*>, List<String>>()
    val propertyGetters = ConcurrentHashMap<Pair<KClass<*>, String>, KProperty1<Any, Any?>>()
}

private object NullMask
private fun Any.unmask():Any? = if (this == NullMask) null else this

fun Class<*>.objectInstance0(): Any? {
    return ReflectionCache.objects.concurrentGetOrPut(this) {
        try {
            val field = getDeclaredField("INSTANCE\$")
            if (Modifier.isStatic(field.modifiers) && Modifier.isPublic(field.modifiers)) {
                if (!field.isAccessible) {
                    field.isAccessible = true
                }
                field[null]!!
            }
            else NullMask
        }
        catch (e: NoSuchFieldException) {
            NullMask
        }
    }.unmask()
}

fun Class<*>.objectInstance(): Any? {
    return ReflectionCache.objects.concurrentGetOrPut(this) {
        val k: KClass<out Any> = kotlin
        k.objectInstance ?: NullMask
    }.unmask()
}

fun Class<*>.companionObjectInstance(): Any? {
    return ReflectionCache.companionObjects.concurrentGetOrPut(this) {
        kotlin.companionObjectInstance ?: NullMask
    }.unmask()
}

@Suppress("UNCHECKED_CAST")
fun <T: Any> KClass<out T>.propertyGetter(property: String): KProperty1<Any, *>? {
    return ReflectionCache.propertyGetters.concurrentGetOrPut(Pair(this, property)) {
        memberProperties.singleOrNull {
            property == it.javaField?.name ?: it.javaGetter?.name?.removePrefix("get")?.decapitalize()
        } as KProperty1<Any, *>
    }
}

fun Any.propertyValue(property: String): Any? {
    val getter = javaClass.kotlin.propertyGetter(property) ?: error("Invalid property $property on type ${javaClass.name}")
    return getter.get(this)
}

private fun Class<*>.consMetaData(): Triple<Constructor<*>, Array<Class<*>>, List<KParameter>> {
    return ReflectionCache.consMetadata.concurrentGetOrPut(this) {
        val cons = primaryConstructor() ?: error("Expecting single constructor for the bean")
        val consDesc: KFunction<Any> = (this.kotlin as KClass<*>).primaryConstructor!!
        Triple(cons, cons.parameterTypes, consDesc.parameters)
    }
}

public class MissingArgumentException(message: String) : RuntimeException(message)

@Suppress("UNCHECKED_CAST")
fun <T> Class<out T>.buildBeanInstance(allParams: Map<String,String>): T {
    objectInstance()?.let {
        return it as T
    }

    val (ktor, paramTypes, valueParams) = consMetaData()

    val args = valueParams.mapIndexed { i, param ->
        allParams[param.name]?.let {
            Serialization.deserialize(it, paramTypes[i] as Class<Any>, classLoader)
        } ?: if (param.type.isMarkedNullable) {
            null
        } else {
            throw MissingArgumentException("Required argument '${param.name}' is missing, available params: $allParams")

        }
    }.toTypedArray()

    return ktor.newInstance(*args) as T
}

fun Any.primaryProperties() : List<String> {
    return ReflectionCache.primaryProperites.concurrentGetOrPut(javaClass) {
        javaClass.kotlin.primaryConstructor?.parameters?.map { it.name ?: "" }.orEmpty()
    }
}

@Suppress("UNCHECKED_CAST")
fun <T> Class<out T>.primaryConstructor() : Constructor<T>? {
    return constructors.singleOrNull() as? Constructor<T>
}

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
    return when {
        protocol == "jar" -> JarFile(urlDecode(toExternalForm().substringAfter("file:").substringBeforeLast("!"))).scanForClasses(prefix, classLoader)
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
