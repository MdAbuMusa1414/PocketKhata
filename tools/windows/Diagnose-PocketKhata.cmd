@echo off
setlocal EnableExtensions DisableDelayedExpansion
set "PK_DIAG_PROJECT=%USERPROFILE%\AndroidStudioProjects\PocketKhata"
if exist "%~dp0gradle\wrapper\gradle-wrapper.properties" set "PK_DIAG_PROJECT=%~dp0"
for /f "usebackq delims=" %%D in (`powershell.exe -NoLogo -NoProfile -Command "[Environment]::GetFolderPath('Desktop')"`) do set "PK_DIAG_DESKTOP=%%D"
if not defined PK_DIAG_DESKTOP set "PK_DIAG_DESKTOP=%USERPROFILE%\Desktop"
if not exist "%PK_DIAG_DESKTOP%" set "PK_DIAG_DESKTOP=%TEMP%"
set "PK_DIAG_OUT=%PK_DIAG_DESKTOP%\PocketKhata-Diagnostic.txt"

echo PocketKhata: collecting Gradle setup and connection evidence.
echo The report will open in Notepad for you to review and share.
echo.
> "%PK_DIAG_OUT%" echo PocketKhata Gradle diagnostic report
>> "%PK_DIAG_OUT%" echo Collected: %DATE% %TIME%
>> "%PK_DIAG_OUT%" echo Project: %PK_DIAG_PROJECT%
>> "%PK_DIAG_OUT%" ver
>> "%PK_DIAG_OUT%" echo.
>> "%PK_DIAG_OUT%" echo === Local wrapper settings ===
if exist "%PK_DIAG_PROJECT%\gradle\wrapper\gradle-wrapper.properties" (
  findstr /b /c:"distributionUrl=" /c:"networkTimeout=" /c:"distributionSha256Sum=" "%PK_DIAG_PROJECT%\gradle\wrapper\gradle-wrapper.properties" >> "%PK_DIAG_OUT%"
) else (
  >> "%PK_DIAG_OUT%" echo Expected project folder was not found. Report the actual clone folder.
)
>> "%PK_DIAG_OUT%" echo.
>> "%PK_DIAG_OUT%" echo === Local commit ===
git -C "%PK_DIAG_PROJECT%" rev-parse --short HEAD >> "%PK_DIAG_OUT%" 2>&1
git -C "%PK_DIAG_PROJECT%" status --short --untracked-files=no >> "%PK_DIAG_OUT%" 2>&1
>> "%PK_DIAG_OUT%" echo.
>> "%PK_DIAG_OUT%" echo === IDE linked Gradle settings ===
powershell.exe -NoLogo -NoProfile -Command "$p=Join-Path $env:PK_DIAG_PROJECT '.idea\gradle.xml'; if(Test-Path -LiteralPath $p){[xml]$xml=Get-Content -Raw -LiteralPath $p; $xml.SelectNodes('//option') | Where-Object {$_.name -in @('externalProjectPath','distributionType','gradleHome','serviceDirectoryPath','gradleJvm')} | ForEach-Object {$_.name + '=' + $_.value}}else{'No IDE Gradle settings file found'}" >> "%PK_DIAG_OUT%" 2>&1
>> "%PK_DIAG_OUT%" echo.
>> "%PK_DIAG_OUT%" echo === Android Studio bundled Java ===
if exist "%ProgramFiles%\Android\Android Studio\jbr\bin\java.exe" (
  "%ProgramFiles%\Android\Android Studio\jbr\bin\java.exe" -version >> "%PK_DIAG_OUT%" 2>&1
) else (
  >> "%PK_DIAG_OUT%" echo Bundled Java not found at the standard install location.
)
>> "%PK_DIAG_OUT%" echo.
>> "%PK_DIAG_OUT%" echo === Proxy environment presence, values omitted ===
powershell.exe -NoLogo -NoProfile -Command "foreach($name in @('HTTP_PROXY','HTTPS_PROXY','ALL_PROXY','JAVA_TOOL_OPTIONS','GRADLE_OPTS')){$set=-not [string]::IsNullOrEmpty([Environment]::GetEnvironmentVariable($name)); $name + ' configured=' + $set}" >> "%PK_DIAG_OUT%" 2>&1

echo Connection check 1 of 2: Gradle service. Maximum 25 seconds.
>> "%PK_DIAG_OUT%" echo.
>> "%PK_DIAG_OUT%" echo === services.gradle.org distribution connection ===
curl.exe --head --location --connect-timeout 8 --max-time 25 --silent --show-error --output NUL --write-out "HTTP=%%{http_code} seconds=%%{time_total}\n" https://services.gradle.org/distributions/gradle-8.9-bin.zip >> "%PK_DIAG_OUT%" 2>&1
>> "%PK_DIAG_OUT%" echo curl exit=%ERRORLEVEL%
echo Connection check 2 of 2: official Gradle GitHub release. Maximum 25 seconds.
>> "%PK_DIAG_OUT%" echo.
>> "%PK_DIAG_OUT%" echo === Official GitHub distribution connection ===
curl.exe --head --location --connect-timeout 8 --max-time 25 --silent --show-error --output NUL --write-out "HTTP=%%{http_code} seconds=%%{time_total}\n" https://github.com/gradle/gradle-distributions/releases/download/v8.9.0/gradle-8.9-bin.zip >> "%PK_DIAG_OUT%" 2>&1
>> "%PK_DIAG_OUT%" echo curl exit=%ERRORLEVEL%

>> "%PK_DIAG_OUT%" echo.
>> "%PK_DIAG_OUT%" echo === Latest IDE download errors: labels and public source only ===
powershell.exe -NoLogo -NoProfile -Command "$base=Join-Path $env:LOCALAPPDATA 'Google'; $logs=Get-ChildItem -LiteralPath $base -Directory -Filter 'AndroidStudio*' -ErrorAction SilentlyContinue | ForEach-Object {Join-Path $_.FullName 'log\idea.log'} | Where-Object {Test-Path -LiteralPath $_} | ForEach-Object {Get-Item -LiteralPath $_} | Sort-Object LastWriteTime -Descending; $f=$logs | Select-Object -First 1; if($null -eq $f){'No Android Studio log found at the standard location'}else{'Log modified: '+$f.LastWriteTime.ToString('s'); $hits=Select-String -LiteralPath $f.FullName -Pattern 'Could not install Gradle distribution|SocketTimeoutException|Connect timed out|UnknownHostException|SSLHandshakeException|PKIX path building failed' | Select-Object -Last 20; foreach($hit in $hits){foreach($label in @('Could not install Gradle distribution','SocketTimeoutException','Connect timed out','UnknownHostException','SSLHandshakeException','PKIX path building failed')){if($hit.Line.Contains($label)){'Line '+$hit.LineNumber+': '+$label}}; foreach($source in @('services.gradle.org','downloads.gradle.org','github.com/gradle/gradle-distributions')){if($hit.Line.Contains($source)){'Download source: '+$source}}}}" >> "%PK_DIAG_OUT%" 2>&1

echo.
echo Saved report: "%PK_DIAG_OUT%"
echo Send PocketKhata-Diagnostic.txt in the chat after reviewing it.
start "" notepad.exe "%PK_DIAG_OUT%"
pause
exit /b
