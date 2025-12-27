@echo off
setlocal

if "%1"=="" (
  set "ARGS=javafx:run"
) else (
  set "ARGS=%*"
)

where mvn >nul 2>nul
if %errorlevel%==0 (
  mvn -DskipTests %ARGS%
  exit /b %errorlevel%
)

if exist "%~dp0scripts\tools\apache-maven-3.9.11\bin\mvn.cmd" (
  "%~dp0scripts\tools\apache-maven-3.9.11\bin\mvn.cmd" -DskipTests %ARGS%
  exit /b %errorlevel%
)

if exist "%USERPROFILE%\.maven\maven-3.9.11\bin\mvn.cmd" (
  "%USERPROFILE%\.maven\maven-3.9.11\bin\mvn.cmd" -DskipTests %ARGS%
  exit /b %errorlevel%
)

echo Maven (mvn) not found. Install Maven or put it in PATH, or add a local Maven under scripts\tools.
exit /b 1
