#!/bin/sh

javac -classpath .:./lib/* TextEditorV2.java
result=$?

if test ${result} -eq 0
then
	java -classpath .:./lib/* TextEditorV2
else
	echo "Failed to compile"
fi
