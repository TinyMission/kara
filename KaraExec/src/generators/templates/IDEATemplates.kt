package generators.templates

import kara.generators.Generator
import java.util.UUID


fun gradleTemplate(gen: Generator): String {
    val USER_HOME = "\$USER_HOME"
    return """<?xml version="1.0" encoding="UTF-8"?>
    <project version="4">
        <component name="GradleSettings">
            <option name="gradleHome" value="$USER_HOME$/frameworks/gradle-1.0" />
        </component>
    </project>
    """
}

fun compilerTemplate(gen: Generator): String {

    return """<?xml version="1.0" encoding="UTF-8"?>
        <project version="4">
            <component name="CompilerConfiguration">
                <option name="DEFAULT_COMPILER" value="Javac" />
                <resourceExtensions />
                <wildcardResourcePatterns>
                    <entry name="!?*.java" />
                    <entry name="!?*.form" />
                    <entry name="!?*.class" />
                    <entry name="!?*.groovy" />
                    <entry name="!?*.scala" />
                    <entry name="!?*.flex" />
                    <entry name="!?*.kt" />
                    <entry name="!?*.clj" />
                 </wildcardResourcePatterns>
            <annotationProcessing>
                    <profile default="true" name="Default" enabled="false">
                        <processorPath useClasspath="true" />
                    </profile>
                </annotationProcessing>
            </component>
        </project>
    """
}

fun encodingsTemplate(gen: Generator): String {
    return """<?xml version="1.0" encoding="UTF-8"?>
    <project version="4">
        <component name="Encoding" useUTFGuessing="true" native2AsciiForPropertiesFiles="false" />
    </project>
    """
}

fun uiDesignerTemplate(gen: Generator): String {
    val Separator = "\$Separator"
   return """<?xml version="1.0" encoding="UTF-8"?>
    <project version="4">
    <component name="Palette2">
    <group name="Swing">
    <item class="com.intellij.uiDesigner.HSpacer" tooltip-text="Horizontal Spacer" icon="/com/intellij/uiDesigner/icons/hspacer.png" removable="false" auto-create-binding="false" can-attach-label="false">
    <default-constraints vsize-policy="1" hsize-policy="6" anchor="0" fill="1" />
    </item>
    <item class="com.intellij.uiDesigner.VSpacer" tooltip-text="Vertical Spacer" icon="/com/intellij/uiDesigner/icons/vspacer.png" removable="false" auto-create-binding="false" can-attach-label="false">
    <default-constraints vsize-policy="6" hsize-policy="1" anchor="0" fill="2" />
    </item>
    <item class="javax.swing.JPanel" icon="/com/intellij/uiDesigner/icons/panel.png" removable="false" auto-create-binding="false" can-attach-label="false">
    <default-constraints vsize-policy="3" hsize-policy="3" anchor="0" fill="3" />
    </item>
    <item class="javax.swing.JScrollPane" icon="/com/intellij/uiDesigner/icons/scrollPane.png" removable="false" auto-create-binding="false" can-attach-label="true">
    <default-constraints vsize-policy="7" hsize-policy="7" anchor="0" fill="3" />
    </item>
    <item class="javax.swing.JButton" icon="/com/intellij/uiDesigner/icons/button.png" removable="false" auto-create-binding="true" can-attach-label="false">
    <default-constraints vsize-policy="0" hsize-policy="3" anchor="0" fill="1" />
    <initial-values>
    <property name="text" value="Button" />
    </initial-values>
    </item>
    <item class="javax.swing.JRadioButton" icon="/com/intellij/uiDesigner/icons/radioButton.png" removable="false" auto-create-binding="true" can-attach-label="false">
    <default-constraints vsize-policy="0" hsize-policy="3" anchor="8" fill="0" />
    <initial-values>
    <property name="text" value="RadioButton" />
    </initial-values>
    </item>
    <item class="javax.swing.JCheckBox" icon="/com/intellij/uiDesigner/icons/checkBox.png" removable="false" auto-create-binding="true" can-attach-label="false">
    <default-constraints vsize-policy="0" hsize-policy="3" anchor="8" fill="0" />
    <initial-values>
    <property name="text" value="CheckBox" />
    </initial-values>
    </item>
    <item class="javax.swing.JLabel" icon="/com/intellij/uiDesigner/icons/label.png" removable="false" auto-create-binding="false" can-attach-label="false">
    <default-constraints vsize-policy="0" hsize-policy="0" anchor="8" fill="0" />
    <initial-values>
    <property name="text" value="Label" />
    </initial-values>
    </item>
    <item class="javax.swing.JTextField" icon="/com/intellij/uiDesigner/icons/textField.png" removable="false" auto-create-binding="true" can-attach-label="true">
    <default-constraints vsize-policy="0" hsize-policy="6" anchor="8" fill="1">
    <preferred-size width="150" height="-1" />
    </default-constraints>
    </item>
    <item class="javax.swing.JPasswordField" icon="/com/intellij/uiDesigner/icons/passwordField.png" removable="false" auto-create-binding="true" can-attach-label="true">
    <default-constraints vsize-policy="0" hsize-policy="6" anchor="8" fill="1">
    <preferred-size width="150" height="-1" />
    </default-constraints>
    </item>
    <item class="javax.swing.JFormattedTextField" icon="/com/intellij/uiDesigner/icons/formattedTextField.png" removable="false" auto-create-binding="true" can-attach-label="true">
    <default-constraints vsize-policy="0" hsize-policy="6" anchor="8" fill="1">
    <preferred-size width="150" height="-1" />
    </default-constraints>
    </item>
    <item class="javax.swing.JTextArea" icon="/com/intellij/uiDesigner/icons/textArea.png" removable="false" auto-create-binding="true" can-attach-label="true">
    <default-constraints vsize-policy="6" hsize-policy="6" anchor="0" fill="3">
    <preferred-size width="150" height="50" />
    </default-constraints>
    </item>
    <item class="javax.swing.JTextPane" icon="/com/intellij/uiDesigner/icons/textPane.png" removable="false" auto-create-binding="true" can-attach-label="true">
    <default-constraints vsize-policy="6" hsize-policy="6" anchor="0" fill="3">
    <preferred-size width="150" height="50" />
    </default-constraints>
    </item>
    <item class="javax.swing.JEditorPane" icon="/com/intellij/uiDesigner/icons/editorPane.png" removable="false" auto-create-binding="true" can-attach-label="true">
    <default-constraints vsize-policy="6" hsize-policy="6" anchor="0" fill="3">
    <preferred-size width="150" height="50" />
    </default-constraints>
    </item>
    <item class="javax.swing.JComboBox" icon="/com/intellij/uiDesigner/icons/comboBox.png" removable="false" auto-create-binding="true" can-attach-label="true">
    <default-constraints vsize-policy="0" hsize-policy="2" anchor="8" fill="1" />
    </item>
    <item class="javax.swing.JTable" icon="/com/intellij/uiDesigner/icons/table.png" removable="false" auto-create-binding="true" can-attach-label="false">
    <default-constraints vsize-policy="6" hsize-policy="6" anchor="0" fill="3">
    <preferred-size width="150" height="50" />
    </default-constraints>
    </item>
    <item class="javax.swing.JList" icon="/com/intellij/uiDesigner/icons/list.png" removable="false" auto-create-binding="true" can-attach-label="false">
    <default-constraints vsize-policy="6" hsize-policy="2" anchor="0" fill="3">
    <preferred-size width="150" height="50" />
    </default-constraints>
    </item>
    <item class="javax.swing.JTree" icon="/com/intellij/uiDesigner/icons/tree.png" removable="false" auto-create-binding="true" can-attach-label="false">
    <default-constraints vsize-policy="6" hsize-policy="6" anchor="0" fill="3">
    <preferred-size width="150" height="50" />
    </default-constraints>
    </item>
    <item class="javax.swing.JTabbedPane" icon="/com/intellij/uiDesigner/icons/tabbedPane.png" removable="false" auto-create-binding="true" can-attach-label="false">
    <default-constraints vsize-policy="3" hsize-policy="3" anchor="0" fill="3">
    <preferred-size width="200" height="200" />
    </default-constraints>
    </item>
    <item class="javax.swing.JSplitPane" icon="/com/intellij/uiDesigner/icons/splitPane.png" removable="false" auto-create-binding="false" can-attach-label="false">
    <default-constraints vsize-policy="3" hsize-policy="3" anchor="0" fill="3">
    <preferred-size width="200" height="200" />
    </default-constraints>
    </item>
    <item class="javax.swing.JSpinner" icon="/com/intellij/uiDesigner/icons/spinner.png" removable="false" auto-create-binding="true" can-attach-label="true">
    <default-constraints vsize-policy="0" hsize-policy="6" anchor="8" fill="1" />
    </item>
    <item class="javax.swing.JSlider" icon="/com/intellij/uiDesigner/icons/slider.png" removable="false" auto-create-binding="true" can-attach-label="false">
    <default-constraints vsize-policy="0" hsize-policy="6" anchor="8" fill="1" />
    </item>
    <item class="javax.swing.JSeparator" icon="/com/intellij/uiDesigner/icons/separator.png" removable="false" auto-create-binding="false" can-attach-label="false">
    <default-constraints vsize-policy="6" hsize-policy="6" anchor="0" fill="3" />
    </item>
    <item class="javax.swing.JProgressBar" icon="/com/intellij/uiDesigner/icons/progressbar.png" removable="false" auto-create-binding="true" can-attach-label="false">
    <default-constraints vsize-policy="0" hsize-policy="6" anchor="0" fill="1" />
    </item>
    <item class="javax.swing.JToolBar" icon="/com/intellij/uiDesigner/icons/toolbar.png" removable="false" auto-create-binding="false" can-attach-label="false">
    <default-constraints vsize-policy="0" hsize-policy="6" anchor="0" fill="1">
    <preferred-size width="-1" height="20" />
    </default-constraints>
    </item>
    <item class="javax.swing.JToolBar$Separator" icon="/com/intellij/uiDesigner/icons/toolbarSeparator.png" removable="false" auto-create-binding="false" can-attach-label="false">
    <default-constraints vsize-policy="0" hsize-policy="0" anchor="0" fill="1" />
    </item>
    <item class="javax.swing.JScrollBar" icon="/com/intellij/uiDesigner/icons/scrollbar.png" removable="false" auto-create-binding="true" can-attach-label="false">
    <default-constraints vsize-policy="6" hsize-policy="0" anchor="0" fill="2" />
    </item>
    </group>
    </component>
    </project>
    """

}

fun vcsTemplate(gen: Generator): String {
    return """<?xml version="1.0" encoding="UTF-8"?>
    <project version="4">
    <component name="VcsDirectoryMappings">
    <mapping directory="" vcs="" />
    </component>
    </project>
    """


}

fun miscTemplate(gen: Generator): String {
    val PROJECT_DIR = "\$PROJECT_DIR"
    val guid = UUID.randomUUID().toString().replace("-","")
    return """<?xml version="1.0" encoding="UTF-8"?>
    <project version="4">
      <component name="IdProvider" IDEtalkID="$guid" />
      <component name="ProjectRootManager" version="2" languageLevel="JDK_1_6" assert-keyword="true" jdk-15="true">
        <output url="file://$PROJECT_DIR$/out" />
      </component>
    </project>
"""
}

fun modulesTemplate(gen: Generator): String {
    val PROJECT_DIR = "\$PROJECT_DIR"
    return """<?xml version="1.0" encoding="UTF-8"?>
<project version="4">
  <component name="ProjectModuleManager">
    <modules>
      <module fileurl="file://$PROJECT_DIR$/${gen.appPackage}.iml" filepath="$PROJECT_DIR$/${gen.appPackage}.iml" />
    </modules>
  </component>
</project>
    """

}
fun scopeTemplate(gen: Generator): String {
    return """<component name="DependencyValidationManager">
      <state>
        <option name="SKIP_IMPORT_STATEMENTS" value="false" />
      </state>
    </component>
    """
}

fun nameTemplate(gen: Generator): String {
    return """${gen.projectName}"""
}

fun copyrightTemplate(gen: Generator): String {
    return """<component name="CopyrightManager">
    <settings default="">
        <module2copyright />
     </settings>
    </component>
"""
}

fun libraryKaraTemplate(gen: Generator): String {
    val PROJECT_DIR = "\$PROJECT_DIR"
    return """<component name="libraryTable">
  <library name="KaraLib">
    <CLASSES>
      <root url="jar://$PROJECT_DIR$/lib/KaraLib.jar!/" />
    </CLASSES>
    <JAVADOC />
    <SOURCES>
      <root url="jar://$PROJECT_DIR$/lib/KaraLib.jar!/" />
    </SOURCES>
  </library>
</component>
"""
}