Get-ChildItem -Recurse -Filter *.java | ForEach-Object {
    $content = Get-Content $_.FullName
    $utf8NoBomEncoding = New-Object System.Text.UTF8Encoding $false
    [System.IO.File]::WriteAllLines($_.FullName, $content, $utf8NoBomEncoding)
    Write-Host "Convertido: $($_.FullName)"
}

Write-Host "Conversion completa."
