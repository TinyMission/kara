@echo off

set KARA_HOME=%~dp0%

java -DKARA_HOME=%KARA_HOME% -cp "%KARA_HOME%\lib\kotlin-runtime.jar;%KARA_HOME%\modules\exec\kara-exec.jar" kara.setup.SetupPackage %*

