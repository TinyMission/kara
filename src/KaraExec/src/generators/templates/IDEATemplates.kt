package generators.templates

import kara.generators.Generator
import java.util.UUID

fun Generator.createIDEAFiles() {
    ensureDir(".idea")
    renderTemplate(nameTemplate(), ".idea/.name")
    renderTemplate(modulesTemplate(), ".idea/modules.xml")
    renderTemplate(misc(), ".idea/misc.xml")

    ensureDir(".idea/libraries")
    renderTemplate(libraryKaraLib(), ".idea/libraries/KaraLib.xml")
    renderTemplate(libraryKotlinRuntime(), ".idea/libraries/KotlinRuntime.xml")
    renderTemplate(libraryKaraExec(), ".idea/libraries/KaraExec.xml")
    renderTemplate(moduleImlTemplate(), "${appConfig.appPackage}.iml")

    ensureDir("Launcher")
    renderTemplate(launcherModuleTemplate(), "Launcher/Launcher.iml")

    ensureDir(".idea/runConfigurations")
    renderTemplate(launcherConfiguration(), ".idea/runConfigurations/Server.xml")
}

fun Generator.modulesTemplate(): String {
    val PROJECT_DIR = "\$PROJECT_DIR"
    return """<?xml version="1.0" encoding="UTF-8"?>
<project version="4">
  <component name="ProjectModuleManager">
    <modules>
      <module fileurl="file://$PROJECT_DIR$/${appPackage}.iml" filepath="$PROJECT_DIR$/${appPackage}.iml" />
      <module fileurl="file://$PROJECT_DIR$/Launcher/Launcher.iml" filepath="$PROJECT_DIR$/Launcher/Launcher.iml" />
    </modules>
  </component>
</project>
    """
}

fun Generator.nameTemplate(): String {
    return """${projectName}"""
}

fun Generator.libraryKaraLib(): String {
    val KARA_HOME = "\$KARA_HOME"
    return """<component name="libraryTable">
  <library name="KaraLib">
    <CLASSES>
      <root url="jar://$KARA_HOME$/modules/core/kara-core.jar!/" />
    </CLASSES>
    <JAVADOC />
    <SOURCES>
      <root url="jar://$KARA_HOME$/modules/core/src/kara-core-src.zip!/" />
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
      <root url="jar://$KARA_HOME$/lib/kotlin-runtime.jar!/src" />
    </SOURCES>
  </library>
</component>
"""
}

fun Generator.libraryKaraExec() : String {
    val KARA_HOME = "\$KARA_HOME"
    return """<component name="libraryTable">
  <library name="KaraExec">
    <CLASSES>
      <root url="file://$KARA_HOME$/lib" />
      <root url="jar://$KARA_HOME$/modules/exec/kara-exec.jar!/" />
    </CLASSES>
    <JAVADOC />
    <SOURCES>
      <root url="jar://$KARA_HOME$/modules/exec/src/kara-exec-src.zip!/" />
    </SOURCES>
    <jarDirectory url="file://$KARA_HOME$/lib" recursive="false" />
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

fun Generator.launcherModuleTemplate() : String {
    val MODULE_DIR = "\$MODULE_DIR"
    return """<?xml version="1.0" encoding="UTF-8"?>
<module type="JAVA_MODULE" version="4">
  <component name="NewModuleRootManager" inherit-compiler-output="true">
    <exclude-output />
    <content url="file://$MODULE_DIR$" />
    <orderEntry type="inheritedJdk" />
    <orderEntry type="sourceFolder" forTests="false" />
    <orderEntry type="library" name="KotlinRuntime" level="project" />
    <orderEntry type="library" name="KaraExec" level="project" />
    <orderEntry type="library" name="KaraLib" level="project" />
    <orderEntry type="module" module-name="${appPackage}" scope="PROVIDED" />
  </component>
</module>
"""
}

fun Generator.launcherConfiguration() : String {
    val PROJECT_DIR="\$PROJECT_DIR"
    return """<component name="ProjectRunConfigurationManager">
  <configuration default="false" name="Server" type="Application" factoryName="Application">
    <extension name="coverage" enabled="false" merge="false" sample_coverage="true" runner="idea" />
    <option name="MAIN_CLASS_NAME" value="kara.setup.SetupPackage" />
    <option name="VM_PARAMETERS" value="" />
    <option name="PROGRAM_PARAMETERS" value="s" />
    <option name="WORKING_DIRECTORY" value="file://$PROJECT_DIR$" />
    <option name="ALTERNATIVE_JRE_PATH_ENABLED" value="false" />
    <option name="ALTERNATIVE_JRE_PATH" value="" />
    <option name="ENABLE_SWING_INSPECTOR" value="false" />
    <option name="ENV_VARIABLES" />
    <option name="PASS_PARENT_ENVS" value="true" />
    <module name="Launcher" />
    <envs />
    <method />
  </configuration>
</component>
"""
}
