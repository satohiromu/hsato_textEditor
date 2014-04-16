@echo off

javac -classpath .;./lib/* -encoding utf8 TextEditorV2.java
set /A result=%ERRORLEVEL%

if %result% == 0 (
	java -classpath .;./lib/* TextEditorV2
) else (
	echo "Failed to compile"
)