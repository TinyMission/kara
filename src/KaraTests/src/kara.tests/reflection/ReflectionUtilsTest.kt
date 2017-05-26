package kara.tests.reflection

import kara.Application
import kara.ApplicationConfig
import kara.internal.scanPackageForResources
import org.junit.Test
import java.net.URLClassLoader
import kotlin.test.assertTrue

class ClassLoaderFunctionsTest {

    @Test fun scanPackageForResourcesTest() {
        val app = Application.load(ApplicationConfig.loadFrom("src/KaraTests/src/kara.tests/test.conf"),"")
        val classLoader = app.requestClassloader()
        assertTrue((classLoader as URLClassLoader).urLs.any{ it.file.endsWith("kootstrap.jar")}, "Can't find test jar file in classpath")
        assertTrue(scanPackageForResources("kotlinx.html.bootstrap", classLoader, hashMapOf()).isNotEmpty())
    }
}
