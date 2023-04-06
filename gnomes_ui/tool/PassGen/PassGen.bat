@echo off
rem 初期化
set USER_ID=
echo ########## パスワード生成 ##########
cd %~dp0
echo;
rem 入力要求
set /P USER_ID="ユーザID を入力してください: "
rem コンパイル
javac -classpath picketbox-commons-1.0.0.final.jar;..\..\target\gnomes_ui.jar; -encoding UTF-8 PassGen.java
rem javac -classpath picketbox-commons-1.0.0.final.jar;..\..\WEB-INF\lib\gnomes_ui.jar; -encoding UTF-8 PassGen.java
rem echo PassGen: Generating password for %USER_ID%...
echo;
rem 実行
java -classpath picketbox-commons-1.0.0.final.jar;..\..\target\gnomes_ui.jar; PassGen %USER_ID%
rem java -classpath picketbox-commons-1.0.0.final.jar;..\..\WEB-INF\lib\gnomes_ui.jar; PassGen %USER_ID%
echo;
pause