package kara.server

import kara.AppConfig
import java.io.File
import java.net.URL
import java.util.ArrayList

public class JettyAppConfig(val appRoot: String, env: String) : AppConfig(env, {
    File("$appRoot/config/$it").toURI().toURL()
}) {

    protected override fun buildClasspath() : Array<URL> {
        val answer = ArrayList<URL>()
        answer.add(File(appRoot, "bin").toURI().toURL())
        appendJars(File(appRoot, "lib"), answer)
        return Array<URL>(answer.size) {answer.get(it)}
    }

    private fun appendJars(dir: File, answer : MutableList<URL>) {
        dir.listFiles()?.forEach { file ->
            val name = file.getName()
            when {
                name == "src" || name == "sources" -> {}
                name.endsWith("-src.jar") || name.endsWith("-sources.jar") -> {}
                file.isDirectory() -> appendJars(file, answer)
                name.endsWith(".jar") -> {
                    answer.add(file.toURI().toURL())
                }
                else -> {}
            }
        }
    }

}
