$ErrorActionPreference = "Stop"

if (-not (Test-Path "out")) {
    New-Item -ItemType Directory -Path "out" | Out-Null
}

$javaFiles = Get-ChildItem -Path "src" -Recurse -Filter "*.java" | ForEach-Object { $_.FullName }
javac -d out $javaFiles

chcp 65001 | Out-Null
java -Dfile.encoding=UTF-8 -cp out com.todo.Main
