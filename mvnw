#!/bin/sh
# Maven Wrapper Script
MAVEN_VERSION=3.9.6
MVNW_REPO_URL=https://repo.maven.apache.org/maven2
WRAPPER_DIR=$(cd "$(dirname "$0")" && pwd -P)/.mvn/wrapper
WRAPPER_JAR=$WRAPPER_DIR/maven-wrapper.jar
WRAPPER_PROPERTIES=$WRAPPER_DIR/maven-wrapper.properties

if [ ! -f "$WRAPPER_JAR" ]; then
    mkdir -p "$WRAPPER_DIR"
    echo "Downloading maven-wrapper.jar..."
    curl -fsSL "$MVNW_REPO_URL/org/apache/maven/wrapper/maven-wrapper/3.2.0/maven-wrapper-3.2.0.jar" -o "$WRAPPER_JAR"
fi

if [ ! -f "$WRAPPER_PROPERTIES" ]; then
    echo "distributionUrl=$MVNW_REPO_URL/org/apache/maven/apache-maven/$MAVEN_VERSION/apache-maven-$MAVEN_VERSION-bin.zip" > "$WRAPPER_PROPERTIES"
fi

JAVA_EXE=java
if [ -n "$JAVA_HOME" ]; then
    JAVA_EXE="$JAVA_HOME/bin/java"
fi

exec "$JAVA_EXE" -cp "$WRAPPER_JAR" -Dmaven.multiModuleProjectDirectory="$(pwd)" org.apache.maven.wrapper.MavenWrapperMain "$@"