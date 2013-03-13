@echo off
setLocal EnableDelayedExpansion

set kara_home=%~dp0

set jars=%kara_home%lib\*.jar

for %%j in (%jars%) do (
   set cp_jars=%%j;!cp_jars!
)

set cp_jars=!cp_jars!;%kara_home%\modules\core\kara-core.jar;%kara_home%\modules\exec\kara-exec.jar

java -DKARA_HOME=%kara_home% -classpath %cp_jars% kara.KaraPackage %*
