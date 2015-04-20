package kotlinx.reflection

import java.util.*
import java.lang.reflect.*
import kotlin.Array
import java.util.concurrent.ConcurrentHashMap
import org.jetbrains.kotlin.name.Name
import kotlin.reflect.KClass
import kotlin.reflect.KMemberProperty
import kotlin.reflect.jvm.*
import kotlin.reflect.jvm.internal.DescriptorBasedProperty
import kotlin.reflect.jvm.internal.KClassImpl


object ReflectionCache {
    val objects = ConcurrentHashMap<Class<*>, Any>()
    val classObjects = ConcurrentHashMap<Class<*>, Any>()
    val consMetadata = ConcurrentHashMap<Class<*>, Pair<Constructor<*>?, Array<Class<*>>>>()
    val primaryProperites = ConcurrentHashMap<Class<*>, List<String>>()
    val properites = ConcurrentHashMap<Class<*>, List<String>>()
    val propertyGetters = ConcurrentHashMap<Pair<KClass<*>, String>, KMemberProperty<Any, Any?>?>()
}

private object NullMask
private fun Any.unmask():Any? = if (this == NullMask) null else this

fun Class<*>.objectInstance(): Any? {
    return ReflectionCache.objects.getOrPut(this) {
        try {
            val field = getDeclaredField("INSTANCE\$")
            if (Modifier.isStatic(field.getModifiers()) && Modifier.isPublic(field.getModifiers())) {
                field[null]!!
            }
            else NullMask
        }
        catch (e: NoSuchFieldException) {
            NullMask
        }
    }.unmask()
}

deprecated("use #companionObjectInstance() method instead")
fun Class<*>.classObjectInstance(): Any? {
    return ReflectionCache.classObjects.getOrPut(this) {
        try {
            val field = getDeclaredField("OBJECT\$")
            if (Modifier.isStatic(field.getModifiers()) && Modifier.isPublic(field.getModifiers())) {
                field[null]!!
            }
            else NullMask
        }
        catch (e: NoSuchFieldException) {
            NullMask
        }
    }.unmask()
}

fun Class<*>.companionObjectInstance(): Any? {
    return ReflectionCache.classObjects.getOrPut(this) {
        getFields().firstOrNull { (it.getType().kotlin as KClassImpl<*>).descriptor.isCompanionObject() }?.get(null)
    }
}

[suppress("UNCHECKED_CAST")]
fun <T> KClass<out T>.propertyGetter(property: String): KMemberProperty<Any, *>? {
    return ReflectionCache.propertyGetters.getOrPut(Pair(this, property)) {
        return properties.singleOrNull {
            property == it.javaField?.getName() ?: it.javaGetter?.getName()?.removePrefix("get")
        } as KMemberProperty<Any, *>?
    }
}

fun Any.propertyValue(property: String): Any? {
    with(javaClass.kotlin) {
        return propertyGetter(property)!!.get(this@propertyValue)
            ?: if ((this as KClassImpl).scope.getProperties(Name.identifier(property)).first().getType().isMarkedNullable()) {
                null
            } else {
            error("Invalid property ${property} on type ${javaClass.getName()}")
        }
    }
}

val emptyClasses : Array<Class<*>> = array()

private fun Class<*>.consMetaData(): Pair<Constructor<*>?, Array<Class<*>>> {
    return ReflectionCache.consMetadata.getOrPut(this) {
        with(primaryConstructor()) {
            return this to (this?.getParameterTypes() ?: emptyClasses)
        }
    }
}

public class MissingArgumentException(val name: String) : RuntimeException("Required argument $name is missing")

[suppress("UNCHECKED_CAST")]
fun <T> Class<out T>.buildBeanInstance(params: (String) -> String?): T {
    objectInstance()?.let {
        return@let it as T
    }

    val (ktor, paramTypes) = consMetaData()
    if (ktor == null) return objectInstance() as T

    val args = (this.kotlin as KClassImpl<T>).descriptor.getUnsubstitutedPrimaryConstructor()?.getValueParameters()?.mapIndexed { i, param ->
        params(param.getName().asString())?.let {
            Serialization.deserialize(it, paramTypes[i] as Class<Any>)
        } ?: if (param.getType().isMarkedNullable()) {
            null
        } else {
            throw MissingArgumentException(param.getName().asString())

        }
    }?.copyToArray() ?: array()


    return ktor.newInstance(*args) as T
}

fun Any.primaryProperties() : List<String> {
    return ReflectionCache.primaryProperites.getOrPut(javaClass) {
        (javaClass.kotlin as KClassImpl<*>).descriptor
                .getUnsubstitutedPrimaryConstructor()?.getValueParameters()
                ?.map { it.getName().asString() }
    }
}

[suppress("UNCHECKED_CAST")]
fun <T> Class<out T>.primaryConstructor() : Constructor<T>? {
    return getConstructors().firstOrNull() as? Constructor<T>
}

public fun Class<*>.isEnumClass(): Boolean = javaClass<Enum<*>>().isAssignableFrom(this)

fun ClassLoader.loadedClasses(prefix: String ="") : List<Class<*>> {
    return  ClassLoader::class.java.getDeclaredField("classes").let {
        it.setAccessible(true);
        val result = it.get(this)
        it.setAccessible(false)
        (result as Vector<Class<*>>).toArrayList()
    }.filter{
        !prefix.isNullOrBlank() && it.getPackage()?.getName().orEmpty().startsWith(prefix)
    }
}