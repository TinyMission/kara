package kara.tests.reflection

import kara.Application
import kara.ApplicationConfig
import kara.internal.scanPackageForResources
import kara.tests.mock.MockApplication
import kotlinx.reflection.*
import org.junit.Before
import org.junit.Test
import java.net.URLClassLoader
import kotlin.test.*

open class Foo {
    companion object Bar {}
    object Baz : Foo () {}
}

object Foo2 {
    object Test {}
}

Test class ObjectInstances() {

    Before
    fun cleanUpReflectionCache() {
        ReflectionCache.classObjects.clear()
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

    Test fun testDeprecatedClassObjectViaReflection() {
        val companion = javaClass<Foo>().classObjectInstance()
        assertNotNull(companion)
        assert(companion is Foo.Bar)
    }

    Test fun testCompanionObjectViaReflection() {
        val companion = javaClass<Foo>().companionObjectInstance()
        assertNotNull(companion)
        assert(companion is Foo.Bar)
        assertEquals(companion, javaClass<Foo>().classObjectInstance())
    }
}

Test class ClassLoaderFunctionsTest() {

    Test fun scanPackageForResourcesTest() {
        val app = Application.load(ApplicationConfig.loadFrom("src/KaraTests/src/kara.tests/test.conf"))
        val classLoader = app.requestClassloader()
        assertTrue((classLoader as URLClassLoader).getURLs().any{ it.getFile().endsWith("kara-test-related.jar")}, "Can't find test jar file in classpath")
        assertTrue(scanPackageForResources("kotlin.html.bootstrap", classLoader).isNotEmpty())
    }
}