@echo off
setlocal

echo ===================================================
echo   Running Pessimistic Lock Spring Boot Simulation
echo ===================================================

set "MAVEN_PATH=%USERPROFILE%\.m2\wrapper\dists\apache-maven-3.9.9-bin\4nf9hui3q3djbarqar9g711ggc\apache-maven-3.9.9\bin\mvn.cmd"

if exist "%MAVEN_PATH%" (
    echo Using Maven from: %MAVEN_PATH%
    call "%MAVEN_PATH%" -f "%~dp0pom.xml" clean compile spring-boot:run
) else (
    echo Attempting to use system mvn...
    mvn -f "%~dp0pom.xml" clean compile spring-boot:run
)

pause
