Get-ChildItem -Path .\src -Recurse -Include *.java,*.kt,*.json,*.toml | ForEach-Object {
    $bytes = [System.IO.File]::ReadAllBytes($_.FullName)
    if ($bytes.Length -ge 3 -and $bytes[0] -eq 0xEF -and $bytes[1] -eq 0xBB -and $bytes[2] -eq 0xBF) {
        [System.IO.File]::WriteAllBytes($_.FullName, $bytes[3..($bytes.Length - 1)])
        Write-Host "BOM removed: $($_.FullName)"
    }
}
Write-Host "Done."
