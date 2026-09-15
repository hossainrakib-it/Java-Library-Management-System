@echo off
cd /d "%~dp0"
echo Compiling Java files...
javac *.java
if errorlevel 1 (
    echo.
    echo Compilation failed. Make sure JDK 8 or later is installed.
    pause
    exit /b 1
)
echo.
echo Starting Library Management System...
java Main
pause
