$MavenPath = "$env:USERPROFILE\.m2\wrapper\dists\apache-maven-3.9.9-bin\4nf9hui3q3djbarqar9g711ggc\apache-maven-3.9.9\bin\mvn.cmd"
$PomPath = "$PSScriptRoot\pom.xml"

Write-Host "===================================================" -ForegroundColor Cyan
Write-Host "  Running Pessimistic Lock Spring Boot Simulation  " -ForegroundColor Cyan
Write-Host "===================================================" -ForegroundColor Cyan

if (Test-Path $MavenPath) {
    & $MavenPath -f $PomPath clean compile spring-boot:run
} else {
    mvn -f $PomPath clean compile spring-boot:run
}
