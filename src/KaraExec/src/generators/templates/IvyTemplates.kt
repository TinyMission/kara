package generators.templates

import kara.generators.Generator

/**
 * Templates for ivy.xml.
 */

fun Generator.ivyTemplate(projectName : String) : String {
    return """<?xml version="1.0" encoding="UTF-8"?>
<ivy-module version="2.0">
    <info organisation="${appPackage}" module="$projectName"/>
    <dependencies>
    </dependencies>
</ivy-module>
"""
}
