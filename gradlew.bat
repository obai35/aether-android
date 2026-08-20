@echo off
set GRADLE_HOME=%GRADLE_HOME%%USERPROFILE%\.gradle\wrapper\dists\gradle-8.6-bin
if not exist "%GRADLE_HOME%\bin\gradle.bat" (
    echo Gradle not found, downloading...
    powershell -Command "Invoke-WebRequest -Uri 'https://services.gradle.org/distributions/gradle-8.6-bin.zip' -OutFile '%TEMP%\gradle.zip'"
    powershell -Command "Expand-Archive -Path '%TEMP%\gradle.zip' -DestinationPath '%GRADLE_HOME%\..'"
    move "%GRADLE_HOME%\..\gradle-8.6\*" "%GRADLE_HOME%" 2>nul
)
"%GRADLE_HOME%\bin\gradle.bat" %*