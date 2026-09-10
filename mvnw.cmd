@echo off
setlocal
set MAVEN_VERSION=3.9.6
set MVNW_REPO_URL=https://repo.maven.apache.org/maven2
set WRAPPER_DIR=%~dp0.mvn\wrapper
set WRAPPER_JAR=%WRAPPER_DIR%\maven-wrapper.jar
set WRAPPER_PROPERTIES=%WRAPPER_DIR%\maven-wrapper.properties

if not exist "%WRAPPER_JAR%" (
    mkdir "%WRAPPER_DIR%" 2>nul
    echo Downloading maven-wrapper.jar...
    curl -fsSL "%MVNW_REPO_URL%/org/apache/maven/wrapper/maven-wrapper/3.2.0/maven-wrapper-3.2.0.jar" -o "%WRAPPER_JAR%"
)

if not exist "%WRAPPER_PROPERTIES%" (
    echo distributionUrl=%MVNW_REPO_URL%/org/apache/maven/apache-maven/%MAVEN_VERSION%/apache-maven-%MAVEN_VERSION%-bin.zip > "%WRAPPER_PROPERTIES%"
)

set JAVA_EXE=java
if not "%JAVA_HOME%"=="" set JAVA_EXE=%JAVA_HOME%\bin\java

"%JAVA_EXE%" -cp "%WRAPPER_JAR%" -Dmaven.multiModuleProjectDirectory=%cd% org.apache.maven.wrapper.MavenWrapperMain %*