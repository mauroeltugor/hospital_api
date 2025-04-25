# Script para crear archivos de entidades Java vacíos
# Ejecutar desde la raíz del proyecto (C:\hospital_API\app)

# Crear el directorio para las entidades si no existe
$entityDir = ".\src\main\java\com\example\myapp\entity"
if (-not (Test-Path $entityDir)) {
    New-Item -ItemType Directory -Path $entityDir -Force
    Write-Host "Directorio $entityDir creado exitosamente" -ForegroundColor Green
}

# Lista de entidades a crear
$entities = @(
    "User",
    "Patient",
    "Doctor",
    "Specialty",
    "DoctorSpecialty",
    "DoctorSchedule",
    "DoctorScheduleDate",
    "Appointment",
    "MedicalRecord",
    "MedicalRecordItem",
    "Diagnosis",
    "Treatment",
    "Prescription",
    "Allergy",
    "PatientAllergy",
    "Address",
    "City",
    "Country",
    "Notification",
    "UserNotification",
    "Session"
)

# Plantilla para archivos Java
$javaTemplate = @"
package com.example.myapp.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "{0}s")
public class {0} {
    
    // TODO: Implementar entidad
    
}
"@

# Crear cada archivo Java
foreach ($entity in $entities) {
    $fileName = "$entity.java"
    $filePath = Join-Path -Path $entityDir -ChildPath $fileName
    
    # Crear el contenido del archivo reemplazando el nombre de la entidad
    $content = $javaTemplate -f $entity
    
    # Caso especial para User ya que es abstracta
    if ($entity -eq "User") {
        $content = $content.Replace("public class User", "public abstract class User")
    }
    
    # Guardar el archivo
    $content | Out-File -FilePath $filePath -Encoding utf8
    Write-Host "Archivo $fileName creado exitosamente" -ForegroundColor Green
}

Write-Host "Todos los archivos de entidades han sido creados!" -ForegroundColor Cyan