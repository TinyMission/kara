package kara.setup

import java.io.File
import java.io.FileFilter
import java.net.URL
import java.net.URLClassLoader
import java.net.URLDecoder

fun main(args: Array<String>) {
    val cl = URLClassLoader(HomePathResolver.collectJars(), null)
    Thread.currentThread().setContextClassLoader(cl)
    val clazz = cl.loadClass("kara.cli.CliPackage")!!
    clazz.getMethod("main2", javaClass<Array<String>>()).invoke(null, args)
}

object HomePathResolver {
    fun resolveHome(): File {
        val IDEA_DEBUG = System.getProperty("kara.development.dist")
        if (IDEA_DEBUG != null && File(IDEA_DEBUG).isDirectory()) {
            return File(IDEA_DEBUG)
        }
        //jar location is <home>/modules/exec
        return File(File(ClassPathHelper.getClasspathEntry()).getParentFile(), "../..").getCanonicalFile()
    }

    fun collectJars(): Array<URL> {
        val home = resolveHome()
        println("Kara home: ${home}")
        val libs = File(home, "lib").listJars()
        val modules = File(home, "modules").listDirs().flatMap { it.listJars().toList() }
        return (libs + modules).map { it.toURI().toURL() }.toArray(array<URL>())
    }

    val jarFilter = object : FileFilter {
        public override fun accept(pathname: File): Boolean
                = pathname.isFile() && pathname.getName().endsWith(".jar") && !pathname.getName().endsWith("-sources.jar")
    }

    val dirFilter = object : FileFilter {
        public override fun accept(pathname: File): Boolean = pathname.isDirectory()
    }

    fun File.listJars(): Array<out File> = this.listFiles(jarFilter)?:array<File>()
    fun File.listDirs(): Array<out File> = this.listFiles(dirFilter)?:array<File>()

    object ClassPathHelper {
        fun classNameToResourcePath(className: String) = "/" + className.replace('.', '/') + ".class";
        fun encodePlusCharacter(orig: String) = orig.replace("+", "%2B");

        fun resourceUrlToClasspathEntry(className: String, resUrl: URL): String {
            val path = classNameToResourcePath(className);
            // we have to encode '+' character manually because otherwise URLDecoder.decode will
            // transform it into the space character
            var urlStr = URLDecoder.decode(encodePlusCharacter(resUrl.toExternalForm()), "UTF-8");
            if (resUrl.getProtocol() != null) {
                // drop path within jar file only if protocol in the URL is 'jar:'
                if ("jar".equals(resUrl.getProtocol())) {
                    val jarSeparatorIndex = urlStr.indexOf("!");
                    if (jarSeparatorIndex >= 0) {
                        urlStr = urlStr.substring(0, jarSeparatorIndex);
                    }
                }

                var startIndex = urlStr.indexOf(':');
                while (startIndex >= 0 && urlStr.charAt(startIndex + 1) != '/') {
                    startIndex = urlStr.indexOf(':', startIndex + 1);
                }
                if (startIndex >= 0) {
                    urlStr = urlStr.substring(startIndex + 1);
                }
            }

            if (urlStr.endsWith(path)) {
                urlStr = urlStr.substring(0, urlStr.length() - path.length());
            }

            // !Workaround for /D:/some/path/a.jar, which doesn't work if D is subst disk
            if (urlStr.startsWith("/") && urlStr.indexOf(":") == 2) {
                urlStr = urlStr.substring(1);
            }

            // URL may contain spaces, that is why we need to decode it
            return File(urlStr).getPath();
        }

        private fun getClasspathEntry<T>(aClass: Class<T>): String {
            val path = classNameToResourcePath(aClass.getName());
            return resourceUrlToClasspathEntry(aClass.getName(), aClass.getResource(path)!!);
        }

        public fun getClasspathEntry(): String = getClasspathEntry(javaClass);
    }
}
