@echo off
rem ������
set USER_ID=
echo ########## �p�X���[�h���� ##########
cd %~dp0
echo;
rem ���͗v��
set /P USER_ID="���[�UID ����͂��Ă�������: "
rem �R���p�C��
javac -classpath picketbox-commons-1.0.0.final.jar;..\..\target\gnomes_ui.jar; -encoding UTF-8 PassGen.java
rem javac -classpath picketbox-commons-1.0.0.final.jar;..\..\WEB-INF\lib\gnomes_ui.jar; -encoding UTF-8 PassGen.java
rem echo PassGen: Generating password for %USER_ID%...
echo;
rem ���s
java -classpath picketbox-commons-1.0.0.final.jar;..\..\target\gnomes_ui.jar; PassGen %USER_ID%
rem java -classpath picketbox-commons-1.0.0.final.jar;..\..\WEB-INF\lib\gnomes_ui.jar; PassGen %USER_ID%
echo;
pause