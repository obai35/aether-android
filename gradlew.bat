@echo off
set GRADLE_HOME=%GRADLE_HOME%%USERPROFILE%\.gradle\wrapper\dists\gradle-8.5-bin
if not exist "%GRADLE_HOME%in\gradle.bat" (
    echo Gradle not found, downloading...
    powershell -Command "Invoke-WebRequest -Uri 'https://services.gradle.org/distributions/gradle-8.5-bin.zip' -OutFile '%TEMP%\gradle.zip'"
    powershell -Command "Expand-Archive -Path '%TEMP%\gradle.zip' -DestinationPath '%GRADLE_HOME%\..'"
    move "%GRADLE_HOME%\..\gradle-8.5\*" "%GRADLE_HOME%" 2>nul
)
"%GRADLE_HOME%in\gradle.bat" %*
