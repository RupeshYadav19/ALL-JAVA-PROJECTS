$sourceFiles = Get-ChildItem -Path "src" -Recurse -Filter "*.java" | Select-Object -ExpandProperty FullName
if ($sourceFiles.Count -eq 0) {
    Write-Host "No Java files found to compile."
    exit 0
}

if (-not (Test-Path "bin")) {
    New-Item -ItemType Directory -Force -Path "bin" | Out-Null
}

$classpath = "bin;lib\*"
Write-Host "Compiling $($sourceFiles.Count) Java files..."
javac -d bin -cp $classpath $sourceFiles

if ($LASTEXITCODE -eq 0) {
    Write-Host "Compilation successful!" -ForegroundColor Green
} else {
    Write-Host "Compilation failed." -ForegroundColor Red
}
