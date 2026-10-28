[CmdletBinding()]
param(
    [string]$JavaHome
)

$ErrorActionPreference = 'Stop'

function Resolve-JavaExecutable {
    param([string]$RequestedJavaHome)

    if ($RequestedJavaHome) {
        $candidate = Join-Path $RequestedJavaHome 'bin\java.exe'
        if (-not (Test-Path -LiteralPath $candidate)) {
            throw "Java was not found at '$candidate'. Pass the root directory of a JDK 21 installation."
        }
        return (Resolve-Path -LiteralPath $candidate).Path
    }

    if ($env:JAVA_HOME) {
        $candidate = Join-Path $env:JAVA_HOME 'bin\java.exe'
        if (Test-Path -LiteralPath $candidate) {
            return (Resolve-Path -LiteralPath $candidate).Path
        }
    }

    $javaCommand = Get-Command java.exe -ErrorAction SilentlyContinue
    if ($javaCommand) {
        return $javaCommand.Source
    }

    throw 'JDK 21 was not found. Install Eclipse Temurin 21, set JAVA_HOME, or pass -JavaHome.'
}

$javaExecutable = Resolve-JavaExecutable -RequestedJavaHome $JavaHome
$versionLine = (& $javaExecutable -version 2>&1 | Select-Object -First 1).ToString()

if ($versionLine -notmatch 'version "(?<version>[^"]+)"') {
    throw "Could not determine the Java version from: $versionLine"
}

$version = $Matches.version
$majorVersion = if ($version.StartsWith('1.')) {
    [int]($version.Split('.')[1])
} else {
    [int]($version.Split('.')[0])
}

if ($majorVersion -ne 21) {
    throw "Harmony Platform requires JDK 21, but '$javaExecutable' reports Java $version. Set JAVA_HOME to JDK 21 or pass -JavaHome."
}

$resolvedJavaHome = Split-Path -Parent (Split-Path -Parent $javaExecutable)
$env:JAVA_HOME = $resolvedJavaHome
$env:Path = "$(Join-Path $resolvedJavaHome 'bin');$env:Path"

if (-not $env:GRADLE_USER_HOME) {
    $env:GRADLE_USER_HOME = Join-Path ([Environment]::GetFolderPath('UserProfile')) '.gradle'
}

$repositoryRoot = Split-Path -Parent $PSScriptRoot
Write-Host "Building Harmony Platform with Java $version from $resolvedJavaHome"

Push-Location $repositoryRoot
try {
    & .\gradlew.bat cleanAll buildAll --no-daemon
    if ($LASTEXITCODE -ne 0) {
        throw "Gradle build failed with exit code $LASTEXITCODE."
    }
}
finally {
    Pop-Location
}
