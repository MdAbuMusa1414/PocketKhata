@echo off
setlocal EnableExtensions DisableDelayedExpansion
set "PK_SETUP_DIR=%~dp0"
echo PocketKhata Gradle setup
powershell.exe -NoLogo -NoProfile -Command "$p=Join-Path $env:PK_SETUP_DIR 'Setup-Gradle.ps1'; & ([scriptblock]::Create([IO.File]::ReadAllText($p)))"
echo.
pause
exit /b
