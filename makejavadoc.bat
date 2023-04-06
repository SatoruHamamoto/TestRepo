@echo off

rem bat実行時ディレクトリをカレントディレクトリとする
net use x: \\gnomes-lab\www\other\javadoc gnomes /user:gnomes

set WORKSPACE_HOME=%~dp0
pushd %WORKSPACE_HOME%

cd /d %WORKSPACE_HOME%\gnomes_database
call mvn javadoc:javadoc
xcopy /y /R /d /s %WORKSPACE_HOME%\gnomes_database\target\site\apidocs x:\gnomes_database

cd /d %WORKSPACE_HOME%\gnomes_resource
call mvn javadoc:javadoc
xcopy /y /R /d /s %WORKSPACE_HOME%\gnomes_resource\target\site\apidocs x:\gnomes_resource


cd /d %WORKSPACE_HOME%\gnomes_ui
call mvn javadoc:javadoc
xcopy /y /R /d /s %WORKSPACE_HOME%\gnomes_ui\target\site\apidocs x:\gnomes_ui

cd /d %WORKSPACE_HOME%\..\gnomes_demo\gnomes_ui_demo
call mvn javadoc:javadoc
xcopy /y /R /d /s %WORKSPACE_HOME%\..\gnomes_demo\gnomes_ui_demo\target\site\apidocs x:\gnomes_ui_demo


popd
net use x: /delete
pause