@echo off
setLocal EnableDelayedExpansion

set kara_home=%~dp0

set jars=%kara_home%lib\*.jar

set cp_jars=%kara_home%out\jars\KaraLib.jar;%kara_home%out\jars\KaraExec.jar


for %%j in (%jars%) do (
   set cp_jars=%%j;!cp_jars!
)

set cp_jars=!cp_jars!

java -DKARA_HOME=%kara_home% -classpath %cp_jars% kara.KaraPackage %*

