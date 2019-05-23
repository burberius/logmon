#!/bin/bash

docker build --build-arg JAR_FILE=target/logmon-0.0.1-SNAPSHOT.jar -t logmon .
