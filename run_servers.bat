@echo off
REM Launch backend and frontend in separate command windows from project root
set "ROOT=%~dp0"

start "Backend" /D "%ROOT%backend" cmd /k "mvnw.cmd spring-boot:run"
start "Frontend" /D "%ROOT%web" cmd /k "npm run dev"

echo Launched Backend and Frontend windows.
exit /b 0
