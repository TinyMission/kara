package generators.templates

import kara.generators.Generator
import java.util.UUID

fun Generator.createIDEAFiles() {
    ensureDir(".idea")
    ensureDir(".idea/libraries")
    renderTemplate(nameTemplate(), ".idea/.name")
    renderTemplate(modulesTemplate(), ".idea/modules.xml")
    renderTemplate(misc(), ".idea/misc.xml")
    renderTemplate(libraryKaraTemplate(), ".idea/libraries/KaraLib.xml")
    renderTemplate(libraryKotlinRuntime(), ".idea/libraries/KotlinRuntime.xml")
    renderTemplate(moduleImlTemplate(), "${appConfig.appPackage}.iml")
}

fun Generator.modulesTemplate(): String {
    val PROJECT_DIR = "\$PROJECT_DIR"
    return """<?xml version="1.0" encoding="UTF-8"?>
<project version="4">
  <component name="ProjectModuleManager">
    <modules>
      <module fileurl="file://$PROJECT_DIR$/${appPackage}.iml" filepath="$PROJECT_DIR$/${appPackage}.iml" />
    </modules>
  </component>
</project>
    """
}

fun Generator.nameTemplate(): String {
    return """${projectName}"""
}

fun Generator.libraryKaraTemplate(): String {
    val KARA_HOME = "\$KARA_HOME"
    return """<component name="libraryTable">
  <library name="KaraLib">
    <CLASSES>
      <root url="jar://$KARA_HOME$/modules/core/KaraLib.jar!/" />
    </CLASSES>
    <JAVADOC />
    <SOURCES>
      <root url="jar://$KARA_HOME$/modules/core/KaraLib-sources.zip!/" />
    </SOURCES>
  </library>
</component>
"""
}

fun Generator.libraryKotlinRuntime() : String {
    val KARA_HOME = "\$KARA_HOME"
    return """<component name="libraryTable">
  <library name="KotlinRuntime">
    <CLASSES>
      <root url="jar://$KARA_HOME$/lib/kotlin-runtime.jar!/" />
    </CLASSES>
    <JAVADOC />
    <SOURCES>
      <root url="jar://$KARA_HOME/lib/kotlin-runtime.jar!/src" />
    </SOURCES>
  </library>
</component>
"""
}

fun Generator.misc() : String {
    return """<?xml version="1.0" encoding="UTF-8"?>
<project version="4">
  <component name="ProjectRootManager" version="2" languageLevel="JDK_1_7" assert-keyword="true" jdk-15="true" project-jdk-name="1.7" project-jdk-type="JavaSDK" />
</project>
"""
}

fun Generator.moduleImlTemplate() : String {
    val MODULE_DIR = "\$MODULE_DIR"
    return """<?xml version="1.0" encoding="UTF-8"?>
<module type="JAVA_MODULE" version="4">
  <component name="NewModuleRootManager" inherit-compiler-output="false">
    <output url="file://$MODULE_DIR$/bin" />
    <output-test url="file://$MODULE_DIR$/bin/test" />
    <exclude-output />
    <content url="file://$MODULE_DIR$">
      <sourceFolder url="file://$MODULE_DIR$/src" isTestSource="false" />
    </content>
    <orderEntry type="jdk" jdkName="1.7" jdkType="JavaSDK" />
    <orderEntry type="sourceFolder" forTests="false" />
    <orderEntry type="library" name="KotlinRuntime" level="project" />
    <orderEntry type="library" name="KaraLib" level="project" />
  </component>
</module>
"""
}
