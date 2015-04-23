package kara.tests.reflection

import kotlinx.reflection.*
import org.junit.Before
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

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

    Test fun testDeprecatedObjectInstance() {
        assertNotNull(javaClass<Foo2>().objectInstance0())
        assertNotNull(javaClass<Foo2.Test>().objectInstance0())
    }

    Test fun testObjectInstance() {
        assertNotNull(javaClass<Foo2>().objectInstance())
        assertNotNull(javaClass<Foo2.Test>().objectInstance())
        assertEquals(javaClass<Foo2.Test>().objectInstance(), javaClass<Foo2.Test>().objectInstance0())
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