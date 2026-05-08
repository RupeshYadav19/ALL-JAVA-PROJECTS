@echo off
echo ================================================
echo  Student Academic System - Launcher
echo ================================================
java -cp "out;lib\*" com.academic.Main
if errorlevel 1 (
    echo.
    echo Application exited with an error. Please compile first using compile.bat
    pause
)
