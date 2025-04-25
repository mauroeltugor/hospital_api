# Script para crear archivos de repositorio JPA completamente vacíos
# Ruta donde se crearán los archivos
$repoPath = ".\src\main\java\com\example\myapp\repository"

# Asegúrate de que el directorio existe
if (-not (Test-Path $repoPath)) {
    New-Item -ItemType Directory -Path $repoPath -Force
    Write-Host "Directorio creado: $repoPath"
}

# Lista de nombres de repositorios a crear
$repositories = @(
    "UserRepository",
    "PatientRepository",
    "DoctorRepository",
    "SpecialtyRepository",
    "DoctorSpecialtyRepository",
    "DoctorScheduleRepository",
    "DoctorScheduleDateRepository",
    "AppointmentRepository",
    "MedicalRecordRepository",
    "MedicalRecordItemRepository",
    "DiagnosisRepository",
    "TreatmentRepository",
    "PrescriptionRepository",
    "AllergyRepository",
    "PatientAllergyRepository",
    "AddressRepository",
    "CityRepository",
    "CountryRepository",
    "NotificationRepository",
    "UserNotificationRepository",
    "SessionRepository"
)

# Crear cada archivo de repositorio completamente vacío
foreach ($repo in $repositories) {
    $filePath = Join-Path -Path $repoPath -ChildPath "$repo.java"
    
    # Crear archivo vacío (sin contenido)
    New-Item -ItemType File -Path $filePath -Force | Out-Null
    Write-Host "Archivo vacío creado: $filePath"
}

Write-Host "Proceso completado. Se han creado $($repositories.Count) archivos de repositorio vacíos."