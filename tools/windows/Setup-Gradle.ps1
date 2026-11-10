param([switch]$LibraryOnly)

function Get-GradleUrlHash {
    param([Parameter(Mandatory=$true)][string]$Url)
    Add-Type -AssemblyName System.Numerics
    $md5 = [Security.Cryptography.MD5]::Create()
    try { $bytes = $md5.ComputeHash([Text.Encoding]::UTF8.GetBytes($Url)) }
    finally { $md5.Dispose() }
    $number = [Numerics.BigInteger]::Zero
    foreach ($b in $bytes) { $number = $number * 256 + [int]$b }
    $digits = '0123456789abcdefghijklmnopqrstuvwxyz'
    $result = ''
    do {
        $result = $digits[[int]($number % 36)] + $result
        $number = [Numerics.BigInteger]::Divide($number, 36)
    } while ($number -gt 0)
    return $result
}

function Test-GradleArchive {
    param([string]$Archive, [string]$ExpectedSha)
    if (-not (Test-Path -LiteralPath $Archive -PathType Leaf)) { return $false }
    return (Get-FileHash -Algorithm SHA256 -LiteralPath $Archive).Hash.ToLowerInvariant() -eq $ExpectedSha
}

function Add-VerifiedWrapperArchive {
    param([string]$Archive, [string]$ExpectedSha, [string]$GradleUserHome, [string]$Url)
    if (-not (Test-GradleArchive $Archive $ExpectedSha)) { throw 'Official archive checksum verification failed. Cache was not changed.' }
    $folder = Join-Path $GradleUserHome ('wrapper/dists/gradle-8.9-bin/' + (Get-GradleUrlHash $Url))
    $target = Join-Path $folder 'gradle-8.9-bin.zip'
    New-Item -ItemType Directory -Force -Path $folder -ErrorAction Stop | Out-Null
    if (Test-GradleArchive $target $ExpectedSha) { return $target }
    if (Test-Path -LiteralPath $target) {
        Move-Item -LiteralPath $target -Destination ($target + '.before-pocketkhata-' + [DateTime]::UtcNow.Ticks) -ErrorAction Stop
    }
    Copy-Item -LiteralPath $Archive -Destination $target -ErrorAction Stop
    return $target
}

function Write-SetupLog {
    param([string]$Text)
    Write-Host $Text
    Add-Content -LiteralPath $script:SetupLog -Value $Text -Encoding UTF8
}

function Invoke-PocketKhataGradle {
    param([string]$Java, [string]$ProjectRoot, [string[]]$Arguments)
    Write-SetupLog ('> Gradle ' + ($Arguments -join ' '))
    $wrapper = Join-Path $ProjectRoot 'gradle/wrapper/gradle-wrapper.jar'
    $nativeArguments = @('-Xmx128m', '-classpath', $wrapper, 'org.gradle.wrapper.GradleWrapperMain') + $Arguments
    $savedPreference = $ErrorActionPreference
    $ErrorActionPreference = 'Continue'
    try {
        & $Java @nativeArguments 2>&1 | ForEach-Object { Write-SetupLog $_.ToString() }
        $gradleExit = $LASTEXITCODE
    } finally { $ErrorActionPreference = $savedPreference }
    Write-SetupLog ('Gradle exit code: ' + $gradleExit)
    return [int]$gradleExit
}

if ($LibraryOnly) { return }
$ErrorActionPreference = 'Stop'
$desktop = [Environment]::GetFolderPath('Desktop')
if ([string]::IsNullOrWhiteSpace($desktop) -or -not (Test-Path -LiteralPath $desktop)) { $desktop = $env:TEMP }
$script:SetupLog = Join-Path $desktop 'PocketKhata-Gradle-Setup.txt'
Set-Content -LiteralPath $script:SetupLog -Encoding UTF8 -Value ('PocketKhata Gradle setup - ' + [DateTime]::Now.ToString('s'))
$previousJavaHome = $env:JAVA_HOME
$locationPushed = $false
$setupSucceeded = $false
try {
    $project = Join-Path $env:USERPROFILE 'AndroidStudioProjects/PocketKhata'
    $properties = Join-Path $project 'gradle/wrapper/gradle-wrapper.properties'
    if (-not (Test-Path -LiteralPath $properties)) { throw 'PocketKhata was not found in AndroidStudioProjects. Send this report with the actual clone folder.' }
    $expectedSha = 'd725d707bfabd4dfdc958c624003b3c80accc03f7037b5122c4b1d0ef15cecab'
    $officialUrls = @('https://github.com/gradle/gradle-distributions/releases/download/v8.9.0/gradle-8.9-bin.zip', 'https://services.gradle.org/distributions/gradle-8.9-bin.zip')
    $urlLine = Get-Content -LiteralPath $properties | Where-Object { $_ -match '^distributionUrl=' } | Select-Object -First 1
    if (-not $urlLine) { throw 'No Gradle distribution URL was found.' }
    $url = $urlLine.Substring('distributionUrl='.Length).Replace('\:', ':')
    if ($url -notin $officialUrls) { throw 'This helper supports the project official Gradle 8.9 URLs only.' }
    Write-SetupLog ('Project: ' + $project)
    Write-SetupLog ('Current distribution: ' + $url)
    if (Get-Process -Name 'studio64','studio' -ErrorAction SilentlyContinue) {
        Write-Host 'Save your work and close Android Studio normally. Then press Enter here.'
        Read-Host | Out-Null
        if (Get-Process -Name 'studio64','studio' -ErrorAction SilentlyContinue) { throw 'Android Studio is still open. Close it normally before running this helper.' }
    }
    $studio = Join-Path $env:ProgramFiles 'Android/Android Studio'
    $javaHome = Join-Path $studio 'jbr'
    $java = Join-Path $javaHome 'bin/java.exe'
    if (-not (Test-Path -LiteralPath $java)) { throw 'Android Studio bundled Java was not found at the location confirmed in the diagnostic report.' }
    $env:JAVA_HOME = $javaHome
    Write-SetupLog ('Java: ' + $java)
    Push-Location -LiteralPath $project
    $locationPushed = $true
    Write-SetupLog 'Step 1: running the actual Java Gradle wrapper. The wrapper network timeout is two minutes.'
    $versionExit = Invoke-PocketKhataGradle -Java $java -ProjectRoot $project -Arguments @('--version')
    if ($versionExit -ne 0) {
        Write-SetupLog 'The wrapper failed. Preparing the official verified ZIP with Windows curl, then letting Gradle install it.'
        $downloads = Join-Path $env:LOCALAPPDATA 'PocketKhata/gradle-downloads'
        New-Item -ItemType Directory -Force -Path $downloads | Out-Null
        $archive = Join-Path $downloads 'gradle-8.9-bin.zip'
        if (-not (Test-GradleArchive $archive $expectedSha)) {
            $partial = Join-Path $downloads ('gradle-8.9-' + [DateTime]::UtcNow.Ticks + '.partial')
            $curlExit = -1
            $savedPreference = $ErrorActionPreference
            try {
                $ErrorActionPreference = 'Continue'
                & curl.exe --fail --location --connect-timeout 15 --max-time 600 --progress-bar --output $partial $officialUrls[0]
                $curlExit = $LASTEXITCODE
            } finally { $ErrorActionPreference = $savedPreference }
            if ($curlExit -ne 0) { throw ('Actual ZIP download failed; Windows curl exit code ' + $curlExit) }
            if (-not (Test-GradleArchive $partial $expectedSha)) { throw 'Downloaded ZIP checksum did not match official Gradle 8.9. Installation was stopped.' }
            if (Test-Path -LiteralPath $archive) { Move-Item -LiteralPath $archive -Destination ($archive + '.before-pocketkhata-' + [DateTime]::UtcNow.Ticks) }
            Move-Item -LiteralPath $partial -Destination $archive
        }
        Write-SetupLog 'Official ZIP SHA-256 verified.'
        $gradleUserHome = $env:GRADLE_USER_HOME
        if ([string]::IsNullOrWhiteSpace($gradleUserHome)) { $gradleUserHome = Join-Path $env:USERPROFILE '.gradle' }
        foreach ($sourceUrl in $officialUrls) {
            $cached = Add-VerifiedWrapperArchive $archive $expectedSha $gradleUserHome $sourceUrl
            Write-SetupLog ('Verified archive available to wrapper: ' + $cached)
        }
        $versionExit = Invoke-PocketKhataGradle -Java $java -ProjectRoot $project -Arguments @('--version')
        if ($versionExit -ne 0) { throw 'The Java wrapper still failed after the verified archive was cached. The fresh error is in this report.' }
    }
    Write-SetupLog 'WRAPPER_RUNTIME_PASS: Gradle 8.9 starts using Android Studio Java.'
    Write-SetupLog 'Step 2: loading project configuration and plugins. Dependency downloads can take several minutes.'
    $projectExit = Invoke-PocketKhataGradle -Java $java -ProjectRoot $project -Arguments @('help','--no-daemon','--console=plain','--stacktrace')
    if ($projectExit -ne 0) { throw 'Gradle starts, but project configuration failed. The new configuration error is captured above.' }
    Write-SetupLog 'PROJECT_CONFIGURATION_PASS: Gradle and PocketKhata project configuration both succeeded.'
    $setupSucceeded = $true
    Pop-Location
    $locationPushed = $false
    $studioExe = Join-Path $studio 'bin/studio64.exe'
    if (Test-Path -LiteralPath $studioExe) {
        Write-SetupLog 'Opening PocketKhata in a fresh Android Studio process. Let the IDE finish its sync.'
        Start-Process -FilePath $studioExe -ArgumentList ('"' + $project + '"') | Out-Null
    }
} catch {
    Write-SetupLog ('SETUP_STOPPED: ' + $_.Exception.Message)
} finally {
    if ($locationPushed) { Pop-Location }
    $env:JAVA_HOME = $previousJavaHome
    Write-SetupLog ('Report saved: ' + $script:SetupLog)
    if (-not $setupSucceeded) {
        Write-Host 'Send PocketKhata-Gradle-Setup.txt from your Desktop in the chat.'
        Start-Process -FilePath 'notepad.exe' -ArgumentList ('"' + $script:SetupLog + '"') | Out-Null
    }
}
