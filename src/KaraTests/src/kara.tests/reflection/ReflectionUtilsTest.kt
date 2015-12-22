package kara.tests.reflection

import kara.Application
import kara.ApplicationConfig
import kara.internal.scanPackageForResources
import kotlinx.reflection.companionObjectInstance
import kotlinx.reflection.objectInstance0
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

    @Suppress("DEPRECATION")
    @Test fun testDeprecatedObjectInstanceViaReflection() {
        assertNotNull(Foo2::class.java.objectInstance0())
        assertNotNull(Foo2.Test::class.java.objectInstance0())
    }

    @Suppress("DEPRECATION")
    @Test fun testObjectInstance() {
        assertNotNull(Foo2::class.objectInstance)
        assertNotNull(Foo2.Test::class.objectInstance)
        assertEquals(Foo2.Test::class.objectInstance, Foo2.Test::class.java.objectInstance0())
    }

    @Test fun testObjectInstanceIsNotCompanion() {
        assertNull(Foo::class.objectInstance)
    }


    @Test fun testCompanionObjectViaReflection() {
        val companion = Foo::class.java.companionObjectInstance()
        assertNotNull(companion)
        assert(companion is Foo.Bar)
    }
}

class ClassLoaderFunctionsTest() {

    @Test fun scanPackageForResourcesTest() {
        val app = Application.load(ApplicationConfig.loadFrom("src/KaraTests/src/kara.tests/test.conf"),"")
        val classLoader = app.requestClassloader()
        assertTrue((classLoader as URLClassLoader).urLs.any{ it.file.endsWith("kootstrap.jar")}, "Can't find test jar file in classpath")
        assertTrue(scanPackageForResources("kotlin.html.bootstrap", classLoader, hashMapOf()).isNotEmpty())
    }
}
