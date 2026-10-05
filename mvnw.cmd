@REM ----------------------------------------------------------------------------
@REM Licensed to the Apache Software Foundation (ASF) under one
@REM or more contributor license agreements.  See the NOTICE file
@REM distributed with this work for additional information
@REM regarding copyright ownership.  The ASF licenses this file
@REM to you under the Apache License, Version 2.0 (the
@REM "License"); you may not use this file except in compliance
@REM with the License.  You may obtain a copy of the License at
@REM
@REM    https://www.apache.org/licenses/LICENSE-2.0
@REM
@REM Unless required by applicable law or agreed to in writing,
@REM software distributed under the License is distributed on an
@REM "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
@REM KIND, either express or implied.  See the License for the
@REM specific language governing permissions and limitations
@REM under the License.
@REM ----------------------------------------------------------------------------

@REM ----------------------------------------------------------------------------
@REM Maven Start Up Batch script
@REM
@REM Required ENV vars:
@REM JAVA_HOME - location of a JDK home dir
@REM
@REM Optional ENV vars
@REM M2_HOME - location of maven2's installed home dir
@REM MAVEN_BATCH_ECHO - set to 'on' to enable the echoing of the batch commands
@REM MAVEN_BATCH_PAUSE - set to 'on' to wait for a keystroke before ending
@REM MAVEN_OPTS - parameters passed to the Java VM when running Maven
@REM     e.g. to debug Maven itself, use
@REM set MAVEN_OPTS=-Xdebug -Xrunjdwp:transport=dt_socket,server=y,suspend=y,address=8000
@REM MAVEN_SKIP_RC - flag to disable loading of mavenrc files
@REM ----------------------------------------------------------------------------

@echo off
setlocal

set "DIRNAME=%~dp0"
if "%DIRNAME%" == "" set "DIRNAME=."

set "APP_BASE_NAME=%~n0"
set "APP_HOME=%DIRNAME%"

@REM Resolve any "." and ".." in APP_HOME to make it shorter.
for %%i in ("%APP_HOME%") do set "APP_HOME=%%~fi"

set "WRAPPER_JAR=%APP_HOME%\.mvn\wrapper\maven-wrapper.jar"
set "WRAPPER_PROPERTIES=%APP_HOME%\.mvn\wrapper\maven-wrapper.properties"

if not exist "%WRAPPER_JAR%" (
    echo Couldn't find %WRAPPER_JAR%, downloading it ...
    @REM fallback to curl or powershell
    if exist "%SystemRoot%\System32\curl.exe" (
        "%SystemRoot%\System32\curl.exe" -s -L -o "%WRAPPER_JAR%" "https://repo.maven.apache.org/maven2/org/apache/maven/wrapper/maven-wrapper/3.3.2/maven-wrapper-3.3.2.jar"
    ) else (
        powershell -Command "&{"^
        "  $webclient = new-object System.Net.WebClient;"^
        "  if (-not ([string]::IsNullOrEmpty('%MVNW_USERNAME%')) -and -not ([string]::IsNullOrEmpty('%MVNW_PASSWORD%'))) {"^
        "    $webclient.Credentials = new-object System.Net.NetworkCredential('%MVNW_USERNAME%', '%MVNW_PASSWORD%');"^
        "  }"^
        "  [Net.ServicePointManager]::SecurityProtocol = [Net.SecurityProtocolType]::Tls12;"^
        "  $webclient.DownloadFile('https://repo.maven.apache.org/maven2/org/apache/maven/wrapper/maven-wrapper/3.3.2/maven-wrapper-3.3.2.jar', '%WRAPPER_JAR%')"^
        "}"
    )
)

if not exist "%WRAPPER_JAR%" (
    echo Error: Could not download the maven-wrapper.jar
    exit /b 1
)

@REM Find java.exe
if defined JAVA_HOME (
    set "JAVACMD=%JAVA_HOME%\bin\java.exe"
    if not exist "!JAVACMD!" (
        echo.
        echo Error: JAVA_HOME is set to an invalid directory.
        echo JAVA_HOME = "%JAVA_HOME%"
        echo Please set the JAVA_HOME variable in your environment to match the
        echo location of your Java installation.
        echo.
        exit /b 1
    )
) else (
    set "JAVACMD=java.exe"
)

"%JAVACMD%" %MAVEN_OPTS% -jar "%WRAPPER_JAR%" %*
