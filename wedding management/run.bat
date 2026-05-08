@echo off
echo 🌸 WeddingGenie — Compiling and Launching...
mkdir bin 2>nul
dir /s /b *.java > sources.txt
javac -d bin -cp "lib/*;." @sources.txt
if %errorlevel% neq 0 (
    echo ❌ Compilation failed. Check your Java installation and classpath.
    pause
    exit /b %errorlevel%
)
echo ✔ Compilation successful.
echo 🚀 Launching WeddingGenie...
java -cp "bin;lib/*;." main.WeddingGenie
pause
