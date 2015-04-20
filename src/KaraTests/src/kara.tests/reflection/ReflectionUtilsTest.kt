package kara.tests.reflection

import kotlinx.reflection.ReflectionCache
import kotlinx.reflection.classObjectInstance
import kotlinx.reflection.companionObjectInstance
import kotlinx.reflection.primaryProperties
import org.junit.Before
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

open class Foo {
    companion object Bar {}
    object Baz : Foo () {}
}

Test class CompanionObject() {

    Before
    fun cleanUpReflectionCache() {
        ReflectionCache.classObjects.clear()
        ReflectionCache.objects.clear()
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