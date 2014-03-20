@echo off

javac -classpath .;./lib/* -encoding utf8 TextEditor.java
set /A result=%ERRORLEVEL%

if %result% == 0 (
	java -classpath .;./lib/* TextEditor
) else (
	echo "Failed to compile"
)