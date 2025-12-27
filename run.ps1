param(
    [Parameter(ValueFromRemainingArguments=$true)]
    [string[]]$Args
)

# Default goal
if (-not $Args -or $Args.Length -eq 0) { $Args = @('javafx:run') }

# Find mvn: prefer system `mvn`, then bundled `scripts/tools`, then common user maven folder
function Find-Maven {
    if (Get-Command mvn -ErrorAction SilentlyContinue) { return 'mvn' }
    $bundled = Join-Path $PSScriptRoot 'scripts\tools\apache-maven-3.9.11\bin\mvn.cmd'
    if (Test-Path $bundled) { return $bundled }
    $userMvn = Join-Path $env:USERPROFILE '.maven\maven-3.9.11\bin\mvn.cmd'
    if (Test-Path $userMvn) { return $userMvn }
    return $null
}

$mvn = Find-Maven
if (-not $mvn) {
    Write-Host "Maven (mvn) not found. Install Maven or place it in PATH, or use the bundled Maven under scripts/tools if present." -ForegroundColor Yellow
    Write-Host "See README.md for instructions." -ForegroundColor Yellow
    exit 1
}

Write-Host "Using mvn: $mvn"

# Always skip tests by default; allow passing other flags in $Args
& "$mvn" -DskipTests @Args
