package dependencies

import kara.*
import org.apache.ivy.core.settings.IvySettings
import org.apache.ivy.Ivy
import org.apache.ivy.core.event.EventManager
import org.apache.ivy.core.event.IvyListener
import org.apache.ivy.core.event.IvyEvent
import java.io.File
import org.apache.ivy.plugins.resolver.URLResolver
import org.apache.ivy.core.retrieve.RetrieveOptions
import org.apache.ivy.util.filter.Filter
import org.apache.ivy.util.filter.FilterHelper
import org.apache.ivy.Main

public class DependenciesResolver(val appConfig : AppConfig) {
    public fun exec() {
        val pattern = "${appConfig.appRoot}/lib/[artifact]-[revision].[ext]"
        Main.main(array("-retrieve", pattern, "-types", "jar"))
    }
}
