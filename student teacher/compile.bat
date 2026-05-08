@echo off
echo ================================================
echo  Student Academic System - Compiler
echo ================================================

:: Check Java
java -version 2>nul
if errorlevel 1 (
    echo ERROR: Java not found. Please install JDK 8 or higher.
    pause
    exit /b 1
)

:: Check for MySQL connector
if not exist "lib\mysql-connector-j-*.jar" (
    echo.
    echo WARNING: No MySQL Connector JAR found in lib\
    echo Download from: https://dev.mysql.com/downloads/connector/j/
    echo Place the JAR file in the lib\ folder, then re-run this script.
    echo.
    pause
    exit /b 1
)

:: Clean output folder
if exist "out" rmdir /S /Q out
mkdir out

:: Find all .java source files
echo Collecting source files...
dir /S /B src\*.java > sources.txt

:: Compile
echo Compiling...
javac -cp "lib\*" -d out -encoding UTF-8 @sources.txt

if errorlevel 1 (
    echo.
    echo BUILD FAILED. See errors above.
    del sources.txt
    pause
    exit /b 1
)

del sources.txt
echo.
echo BUILD SUCCESSFUL! Run using: run.bat
echo ================================================
pause
