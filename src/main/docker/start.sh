#!/bin/bash

echo "**********************************************"
echo "------------Environment Variables------------"
echo "Java Options : $JAVA_OPTS"
echo "**********************************************"

java $JAVA_OPTS -jar app.jar
