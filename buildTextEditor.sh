#!/bin/sh

echo "buildding"
javac TextEditor.java
result=$?

if test ${result} -eq 0
then
	java TextEditor
else
	echo "Failed to compile"
fi
