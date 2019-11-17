#!/bin/bash

echo "**********************************************"
echo "------------Environment Variables------------"
echo "Java Options : $JAVA_OPTS"
echo "**********************************************"

java $JAVA_OPTS -Dspring.profiles.active=$SPRING_PROFILE -jar app.jar &
