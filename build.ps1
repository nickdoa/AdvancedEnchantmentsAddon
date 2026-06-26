param(
    [string]$AdvancedEnchantmentsJar = "libs\AdvancedEnchantments-9.22.9.jar",
    [string]$SpigotApiVersion = "1.20.4-R0.1-SNAPSHOT"
)

$ErrorActionPreference = "Stop"

$Root = Split-Path -Parent $MyInvocation.MyCommand.Path
$BuildDir = Join-Path $Root "build"
$DepsDir = Join-Path $BuildDir "deps"
$ClassesDir = Join-Path $BuildDir "classes"
$LibsDir = Join-Path $BuildDir "libs"
$SourcesFile = Join-Path $BuildDir "sources.txt"
$SpigotJar = Join-Path $DepsDir "spigot-api-$SpigotApiVersion.jar"
$OutputJar = Join-Path $LibsDir "AdvancedEnchantmentsAddon-0.1.0.jar"

New-Item -ItemType Directory -Force -Path $DepsDir, $ClassesDir, $LibsDir | Out-Null

$AdvancedEnchantmentsPath = Resolve-Path -LiteralPath (Join-Path $Root $AdvancedEnchantmentsJar)

if (!(Test-Path -LiteralPath $SpigotJar)) {
    $Repo = "https://hub.spigotmc.org/nexus/content/repositories/snapshots"
    $GroupPath = "org/spigotmc/spigot-api/$SpigotApiVersion"
    $MetadataUrl = "$Repo/$GroupPath/maven-metadata.xml"
    Write-Host "Downloading Spigot API metadata..."
    [xml]$Metadata = (Invoke-WebRequest -UseBasicParsing -Uri $MetadataUrl).Content

    $JarVersion = $Metadata.metadata.versioning.snapshotVersions.snapshotVersion |
        Where-Object { $_.extension -eq "jar" -and -not $_.classifier } |
        Select-Object -Last 1 -ExpandProperty value

    if (!$JarVersion) {
        throw "Could not find a jar snapshot version for $SpigotApiVersion"
    }

    $JarUrl = "$Repo/$GroupPath/spigot-api-$JarVersion.jar"
    Write-Host "Downloading Spigot API $JarVersion..."
    Invoke-WebRequest -UseBasicParsing -Uri $JarUrl -OutFile $SpigotJar
}

if (Test-Path -LiteralPath $ClassesDir) {
    Remove-Item -LiteralPath $ClassesDir -Recurse -Force
}
New-Item -ItemType Directory -Force -Path $ClassesDir | Out-Null

Get-ChildItem -Path (Join-Path $Root "src\main\java") -Recurse -Filter "*.java" |
    ForEach-Object { $_.FullName } |
    Set-Content -LiteralPath $SourcesFile -Encoding ASCII

$Classpath = "$SpigotJar;$AdvancedEnchantmentsPath"
javac --release 17 -encoding UTF-8 -cp $Classpath -d $ClassesDir "@$SourcesFile"
if ($LASTEXITCODE -ne 0) {
    throw "javac failed with exit code $LASTEXITCODE"
}

Copy-Item -Path (Join-Path $Root "src\main\resources\*") -Destination $ClassesDir -Recurse -Force

if (Test-Path -LiteralPath $OutputJar) {
    Remove-Item -LiteralPath $OutputJar -Force
}

jar --create --file $OutputJar -C $ClassesDir .
if ($LASTEXITCODE -ne 0) {
    throw "jar failed with exit code $LASTEXITCODE"
}
Write-Host "Built $OutputJar"
