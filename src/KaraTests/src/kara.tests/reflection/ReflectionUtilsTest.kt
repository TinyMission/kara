package kara.tests.reflection

import kara.Application
import kara.ApplicationConfig
import kara.internal.scanPackageForResources
import kotlinx.reflection.ReflectionCache
import kotlinx.reflection.companionObjectInstance
import kotlinx.reflection.objectInstance
import kotlinx.reflection.objectInstance0
import org.junit.Before
import org.junit.Test
import java.net.URLClassLoader
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

open class Foo {
    companion object Bar {}
    object Baz : Foo () {}
}

object Foo2 {
    object Test {}
}

class ObjectInstances() {

    Before
    fun cleanUpReflectionCache() {
        ReflectionCache.companionObjects.clear()
        ReflectionCache.objects.clear()
    }

    Test fun testDeprecatedObjectInstanceViaReflection() {
        assertNotNull(javaClass<Foo2>().objectInstance0())
        assertNotNull(javaClass<Foo2.Test>().objectInstance0())
    }

    Test fun testObjectInstance() {
        assertNotNull(javaClass<Foo2>().objectInstance())
        assertNotNull(javaClass<Foo2.Test>().objectInstance())
        assertEquals(javaClass<Foo2.Test>().objectInstance(), javaClass<Foo2.Test>().objectInstance0())
    }

    Test fun testObjectInstanceIsNotCompanion() {
        assertNull(javaClass<Foo>().objectInstance())
    }


    Test fun testCompanionObjectViaReflection() {
        val companion = javaClass<Foo>().companionObjectInstance()
        assertNotNull(companion)
        assert(companion is Foo.Bar)
    }
}

class ClassLoaderFunctionsTest() {

    Test fun scanPackageForResourcesTest() {
        val app = Application.load(ApplicationConfig.loadFrom("src/KaraTests/src/kara.tests/test.conf"),"")
        val classLoader = app.requestClassloader()
        assertTrue((classLoader as URLClassLoader).urLs.any{ it.file.endsWith("kootstrap.jar")}, "Can't find test jar file in classpath")
        assertTrue(scanPackageForResources("kotlin.html.bootstrap", classLoader, hashMapOf()).isNotEmpty())
    }
}
