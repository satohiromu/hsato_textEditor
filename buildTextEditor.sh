#!/bin/sh

javac -classpath .:./lib/* TextEditor.java
result=$?

if test ${result} -eq 0
then
	java -classpath .:./lib/* TextEditor
else
	echo "Failed to compile"
fi
