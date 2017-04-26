package kotlinx.reflection

import java.io.File
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type
import java.net.URL
import java.util.concurrent.ConcurrentHashMap
import java.util.jar.JarFile
import kotlin.jvm.internal.FunctionReference
import kotlin.reflect.*
import kotlin.reflect.full.memberProperties
import kotlin.reflect.full.primaryConstructor
import kotlin.reflect.jvm.javaType
import kotlin.jvm.internal.Reflection

private object NullMask
private fun Any.unmask():Any? = if (this == NullMask) null else this

// can be removed when fixed: KT-17594 consider caching for Class<T>.kotlin
@Suppress("USELESS_CAST")
val <T: Any> Class<T>.kotlinCached: KClass<T>
    get() = Reflection.getOrCreateKotlinClass(this) as KClass<T>

private val propertyGetters = ConcurrentHashMap<Pair<KClass<out Any>, String>, Any>()

fun <T:Any, R:Any?> T.propertyValue(property: String): R? {
    @Suppress("UNCHECKED_CAST")
    val getter = propertyGetters.getOrPut(Pair(this::class, property),  {
        this::class.memberProperties.firstOrNull { it.name == property } ?: NullMask
    }).unmask() as KProperty1<T, R>? ?: error("Invalid property $property on type ${this::class.simpleName}")
    return getter.get(this)
}

class MissingArgumentException(message: String) : RuntimeException(message)

fun <T:Any> KClass<T>.buildBeanInstance(allParams: Map<String, String>): T {
    return objectInstance ?: primaryConstructor!!.resolveAndCall(allParams, java.classLoader)
}

fun KCallable<*>.boundReceiver() = (this as? FunctionReference)?.boundReceiver ?:
        (parameters.find { it.kind == KParameter.Kind.INSTANCE && it.index == 0 }?.type?.classifier as? KClass<*>)?.objectInstance

fun <R:Any> KCallable<R>.resolveAndCall(allParams: Map<String, String>, classLoader: ClassLoader? = javaClass.classLoader) : R {
    val args = parameters.map { param ->
        val stringValue = allParams[param.name]
        val kclazz = paramJavaType(param.type.javaType).kotlinCached
        val isNullable = param.type.isMarkedNullable
        param to when {
            param.kind == KParameter.Kind.INSTANCE -> boundReceiver()!!
            stringValue == "null" && isNullable -> null
            stringValue == "" && kclazz != String::class && isNullable -> null
            stringValue != null -> Serialization.deserialize(stringValue, kclazz, classLoader)  ?: throw MissingArgumentException("Bad argument ${param.name}='$stringValue'")
            param.isOptional -> NullMask
            isNullable -> null
            else -> throw MissingArgumentException("Required argument '${param.name}' is missing, available params: $allParams")
        }
    }.filter { it.second != NullMask }.toMap()

    return callBy(args)
}

@Suppress("UNCHECKED_CAST")
private fun paramJavaType(javaType: Type): Class<Any> {
    return when (javaType) {
        is ParameterizedType -> paramJavaType(javaType.rawType)
        is Class<*> -> javaType as Class<Any>
        else -> error("Unsupported type")
    }
}

fun Class<*>.isEnumClass(): Boolean = Enum::class.java.isAssignableFrom(this)

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
        if (idx + 1 < name.length && name[idx + 1] in '0'..'9') return true
        idx = name.indexOf('$', idx + 1)
    }

    return false
}

private fun File.scanForClasses(prefix: String, classLoader: ClassLoader): List<Class<*>> {
    val path = prefix.packageToPath()
    return walk().filter {
        it.isDirectory || (it.isFile && it.extension == "class" && !isAnonClass(it.name))
    }.toList()
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
