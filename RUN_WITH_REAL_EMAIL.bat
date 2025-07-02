@echo off
echo ====================================
echo University Event Management System
echo ====================================
echo.
echo 🔧 PREPARING REAL EMAIL FUNCTIONALITY...
echo.

echo 📋 Step 1: Checking JAR files...
if exist "javax.mail.jar" (
    echo ✅ javax.mail.jar found
) else (
    echo ❌ javax.mail.jar NOT found
    pause
    exit
)

if exist "activation.jar" (
    echo ✅ activation.jar found
) else (
    echo ❌ activation.jar NOT found
    pause
    exit
)

echo.
echo 🔨 Step 2: Compiling with email dependencies...
javac -cp ".;javax.mail.jar;activation.jar" *.java
if %ERRORLEVEL% NEQ 0 (
    echo ❌ COMPILATION FAILED!
    echo Check for syntax errors in Java files
    pause
    exit
)
echo ✅ Compilation successful

echo.
echo 🚀 Step 3: Starting application with REAL EMAIL...
echo 📧 Email system: javax.mail + activation JAR
echo 📬 OTP delivery: REAL EMAILS to user inbox
echo 🔧 Email configured: universityeventmanagementoffic@gmail.com
echo.
echo 💡 If you don't receive emails, check your spam folder!
echo.

java -cp ".;javax.mail.jar;activation.jar" EventManagementApp

echo.
echo 🏁 Application closed.
pause 