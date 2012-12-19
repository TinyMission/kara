package generators.templates

import kara.generators.Generator

/**
 * Templates for project generation.
 */

val MODULE_DIR = "\$MODULE_DIR"

fun moduleImlTemplate(gen : Generator) : String {
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
