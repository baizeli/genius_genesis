$path = 'src\main\resources\assets\genius_genesis'
$files = Get-ChildItem -Path $path -Recurse -Filter '*.json'
$bomFiles = @()
foreach ($file in $files) {
    $bytes = [System.IO.File]::ReadAllBytes($file.FullName)
    if ($bytes.Length -ge 3 -and $bytes[0] -eq 0xEF -and $bytes[1] -eq 0xBB -and $bytes[2] -eq 0xBF) {
        $bomFiles += $file.FullName
    }
}
Write-Output "Found $($bomFiles.Count) files with BOM:"
$bomFiles | ForEach-Object { Write-Output $_ }
