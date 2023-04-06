
@echo off

set DEPLOY_PATH=C:\EX\UI\wildfly-10.1.0.Final\standalone\deployments\UI.war

:TOP
TASKLIST | FIND "java.exe" > NUL
IF NOT ERRORLEVEL 1  (
  ECHO ëŒè€APÇÕãNìÆÇµÇƒÇ¢Ç‹Ç∑ÅBCtrl+cÇ≈í‚é~ÇµÇƒÇ≠ÇæÇ≥Ç¢

  timeout /T 5

  GOTO TOP
) ELSE (
  GOTO COPYOK
)


:COPYOK
@echo on

xcopy /R /Y /D gnomes_database\target\gnomes_database.jar        %DEPLOY_PATH%\WEB-INF\lib\gnomes_database.jar
xcopy /R /Y /D gnomes_resource\target\gnomes_resource.jar        %DEPLOY_PATH%\WEB-INF\lib\gnomes_resource.jar

xcopy /R /Y /D gnomes_ui\target\gnomes_ui.jar                    %DEPLOY_PATH%\WEB-INF\lib\gnomes_ui.jar
xcopy /R /Y /M /S gnomes_ui\resources\jsp                        %DEPLOY_PATH%\jsp
xcopy /R /Y /M /S gnomes_ui\resources\js                         %DEPLOY_PATH%\js
xcopy /R /Y /M /S gnomes_ui\resources\css                        %DEPLOY_PATH%\css
xcopy /R /Y /M /S gnomes_ui\resources\WEB-INF\common-command     %DEPLOY_PATH%\WEB-INF\common-command

pause