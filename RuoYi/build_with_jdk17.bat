@echo off
set JAVA_HOME=D:\Program Files\Java\jdk-17
set PATH=%JAVA_HOME%\bin;%PATH%
echo Using Java:
"%JAVA_HOME%\bin\java" -version
echo.
echo Building...
call "D:\apache-maven-3.9.9\bin\mvn.cmd" clean package -DskipTests -Dmaven.repo.local=D:\apache-maven-3.9.9\repository -f D:\RuoYi\pom.xml
pause
