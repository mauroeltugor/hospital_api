package com.example.myapp.repository.custom;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

/**
 * Repository for advanced clinical analytics.
 * Provides complex queries for medical research and quality improvement.
 */
@Repository
public class ClinicalAnalyticsRepository {

    @PersistenceContext
    private EntityManager entityManager;
    
    /**
     * Get treatment outcome analysis.
     * This analysis compares treatment effectiveness across multiple factors.
     *
     * @param startDate The start date
     * @param endDate The end date
     * @return List of treatment outcome statistics
     */
    @SuppressWarnings("unchecked")
    public List<Object[]> getTreatmentOutcomeAnalysis(LocalDate startDate, LocalDate endDate) {
        String jpql = 
            "SELECT " +
            "   t.id as treatmentId, " +
            "   t.name as treatmentName, " +
            "   d.id as diagnosisId, " +
            "   d.name as diagnosisName, " +
            "   AVG(s.effectiveness) as avgEffectiveness, " +
            "   MIN(s.effectiveness) as minEffectiveness, " +
            "   MAX(s.effectiveness) as maxEffectiveness, " +
            "   COUNT(s) as sessionCount, " +
            "   COUNT(DISTINCT p) as patientCount " +
            "FROM Session s " +
            "JOIN s.treatment t " +
            "JOIN s.patient p " +
            "JOIN p.medicalRecord mr " +
            "JOIN mr.items mri " +
            "JOIN mri.diagnoses dmi " +
            "JOIN dmi.diagnosis d " +
            "WHERE s.sessionDate BETWEEN :startDate AND :endDate " +
            "AND s.effectiveness IS NOT NULL " +
            "GROUP BY t.id, t.name, d.id, d.name " +
            "HAVING COUNT(s) >= 5 " +
            "ORDER BY avgEffectiveness DESC";
        
        return entityManager.createQuery(jpql)
            .setParameter("startDate", startDate)
            .setParameter("endDate", endDate)
            .getResultList();
    }
    
    /**
     * Get practice variation analysis.
     * This analysis identifies differences in treatment patterns between doctors.
     *
     * @param diagnosisId The diagnosis ID
     * @return List of practice variation statistics
     */
    @SuppressWarnings("unchecked")
    public List<Object[]> getPracticeVariationAnalysis(Long diagnosisId) {
        String jpql = 
            "SELECT " +
            "   d.id as doctorId, " +
            "   d.firstName as firstName, " +
            "   d.lastName as lastName, " +
            "   ds.specialty.name as specialtyName, " +
            "   ds.experienceLevel as experienceLevel, " +
            "   t.id as treatmentId, " +
            "   t.name as treatmentName, " +
            "   COUNT(pt) as prescriptionCount " +
            "FROM Doctor d " +
            "JOIN d.specialties ds " +
            "JOIN d.prescriptions p " +
            "JOIN p.treatments pt " +
            "JOIN pt.treatment t " +
            "JOIN p.medicalRecordItems mrip " +
            "JOIN mrip.medicalRecordItem mri " +
            "JOIN mri.diagnoses dmi " +
            "WHERE dmi.diagnosis.id = :diagnosisId " +
            "GROUP BY d.id, d.firstName, d.lastName, ds.specialty.name, ds.experienceLevel, t.id, t.name " +
            "ORDER BY ds.specialty.name, ds.experienceLevel, prescriptionCount DESC";
        
        return entityManager.createQuery(jpql)
            .setParameter("diagnosisId", diagnosisId)
            .getResultList();
    }
    
    /**
     * Get diagnostic accuracy analysis.
     * This analysis evaluates how often initial diagnoses are changed.
     *
     * @param minPatients Minimum number of patients to include in analysis
     * @return List of diagnostic accuracy statistics
     */
    @SuppressWarnings("unchecked")
    public List<Object[]> getDiagnosticAccuracyAnalysis(int minPatients) {
        String jpql = 
            "SELECT " +
            "   d.id as doctorId, " +
            "   d.firstName as firstName, " +
            "   d.lastName as lastName, " +
            "   diag.id as diagnosisId, " +
            "   diag.name as diagnosisName, " +
            "   COUNT(DISTINCT p) as patientCount, " +
            "   SUM(CASE WHEN (SELECT COUNT(DISTINCT diag2.id) FROM DiagnosisMedItem dmi2 " +
            "                JOIN dmi2.diagnosis diag2 " +
            "                JOIN dmi2.medicalRecordItem mri2 " +
            "                WHERE mri2.medicalRecord = mr " +
            "                AND diag2.id != diag.id) > 0 " +
            "        THEN 1 ELSE 0 END) as changedDiagnosisCount " +
            "FROM Doctor d " +
            "JOIN d.medicalRecordItems mri " +
            "JOIN mri.diagnoses dmi " +
            "JOIN dmi.diagnosis diag " +
            "JOIN mri.medicalRecord mr " +
            "JOIN mr.patient p " +
            "GROUP BY d.id, d.firstName, d.lastName, diag.id, diag.name " +
            "HAVING COUNT(DISTINCT p) >= :minPatients " +
            "ORDER BY d.lastName, d.firstName, diag.name";
        
        return entityManager.createQuery(jpql)
            .setParameter("minPatients", minPatients)
            .getResultList();
    }
    
    /**
     * Get treatment duration analysis.
     * This analysis evaluates how long treatments typically last.
     *
     * @param treatmentId The treatment ID (optional, can be null for all treatments)
     * @return List of treatment duration statistics
     */
    @SuppressWarnings("unchecked")
    public List<Object[]> getTreatmentDurationAnalysis(Long treatmentId) {
        StringBuilder jpql = new StringBuilder(
            "SELECT " +
            "   t.id as treatmentId, " +
            "   t.name as treatmentName, " +
            "   diag.id as diagnosisId, " +
            "   diag.name as diagnosisName, " +
            "   AVG(FUNCTION('DATEDIFF', MAX(s.sessionDate), MIN(s.sessionDate))) as avgDurationDays, " +
            "   MIN(FUNCTION('DATEDIFF', MAX(s.sessionDate), MIN(s.sessionDate))) as minDurationDays, " +
            "   MAX(FUNCTION('DATEDIFF', MAX(s.sessionDate), MIN(s.sessionDate))) as maxDurationDays, " +
            "   COUNT(DISTINCT p) as patientCount " +
            "FROM Treatment t " +
            "JOIN t.sessions s " +
            "JOIN s.patient p " +
            "JOIN p.medicalRecord mr " +
            "JOIN mr.items mri " +
            "JOIN mri.diagnoses dmi " +
            "JOIN dmi.diagnosis diag " +
            "WHERE 1=1 "
        );
        
        if (treatmentId != null) {
            jpql.append("AND t.id = :treatmentId ");
        }
        
        jpql.append(
            "GROUP BY t.id, t.name, diag.id, diag.name " +
            "HAVING COUNT(DISTINCT p) >= 5 " +
            "ORDER BY t.name, diag.name"
        );
        
        var query = entityManager.createQuery(jpql.toString());
        if (treatmentId != null) {
            query.setParameter("treatmentId", treatmentId);
        }
        
        return query.getResultList();
    }
    
    /**
     * Get treatment adherence analysis.
     * This analysis evaluates how well patients follow through with treatment sessions.
     *
     * @param startDate The start date
     * @param endDate The end date
     * @return List of treatment adherence statistics
     */
    @SuppressWarnings("unchecked")
    public List<Object[]> getTreatmentAdherenceAnalysis(LocalDate startDate, LocalDate endDate) {
        String jpql = 
            "SELECT " +
            "   t.id as treatmentId, " +
            "   t.name as treatmentName, " +
            "   COUNT(DISTINCT p) as patientCount, " +
            "   AVG(sessionCount) as avgSessionsPerPatient, " +
            "   SUM(CASE WHEN missedSessions > 0 THEN 1 ELSE 0 END) * 100.0 / COUNT(DISTINCT p) as patientsMissedPercentage, " +
            "   SUM(missedSessions) * 100.0 / SUM(sessionCount + missedSessions) as missedSessionPercentage " +
            "FROM (" +
            "   SELECT t.id as treatmentId, t.name as treatmentName, p.id as patientId, " +
            "   COUNT(s) as sessionCount, " +
            "   (SELECT COUNT(a) FROM Appointment a " +
            "    JOIN a.schedule sch JOIN sch.scheduleDates sd " +
            "    WHERE a.patient.id = p.id " +
            "    AND sd.date BETWEEN :startDate AND :endDate " +
            "    AND a.status = 'NO_SHOW') as missedSessions " +
            "   FROM Treatment t " +
            "   JOIN t.sessions s " +
            "   JOIN s.patient p " +
            "   WHERE s.sessionDate BETWEEN :startDate AND :endDate " +
            "   GROUP BY t.id, t.name, p.id" +
            ") as patientSessions " +
            "GROUP BY treatmentId, treatmentName " +
            "HAVING COUNT(DISTINCT patientId) >= 5 " +
            "ORDER BY missedSessionPercentage";
        
        return entityManager.createQuery(jpql)
            .setParameter("startDate", startDate)
            .setParameter("endDate", endDate)
            .getResultList();
    }
    
    /**
     * Get readmission analysis.
     * This analysis evaluates how often patients require repeat treatment for the same diagnosis.
     *
     * @param diagnosisId The diagnosis ID
     * @param monthsWindow The time window in months to consider for readmissions
     * @return List of readmission statistics
     */
    @SuppressWarnings("unchecked")
    public List<Object[]> getReadmissionAnalysis(Long diagnosisId, int monthsWindow) {
        String jpql = 
            "SELECT " +
            "   d.specialty.name as specialtyName, " +
            "   COUNT(DISTINCT p) as patientCount, " +
            "   SUM(CASE WHEN readmissionCount > 0 THEN 1 ELSE 0 END) * 100.0 / COUNT(DISTINCT p) as readmissionRate, " +
            "   AVG(CASE WHEN readmissionCount > 0 THEN daysTillReadmission ELSE NULL END) as avgDaysTillReadmission " +
            "FROM (" +
            "   SELECT d.id as diagnosisId, d.specialty.id as specialtyId, p.id as patientId, " +
            "   MIN(mri.entryDate) as firstDiagnosisDate, " +
            "   (SELECT COUNT(dmi2) FROM DiagnosisMedItem dmi2 " +
            "    JOIN dmi2.diagnosis d2 JOIN dmi2.medicalRecordItem mri2 " +
            "    WHERE d2.id = d.id AND mri2.medicalRecord.patient.id = p.id " +
            "    AND mri2.entryDate > MIN(mri.entryDate) " +
            "    AND mri2.entryDate <= FUNCTION('DATE_ADD', MIN(mri.entryDate), INTERVAL :monthsWindow MONTH)) as readmissionCount, " +
            "   (SELECT MIN(FUNCTION('DATEDIFF', mri3.entryDate, MIN(mri.entryDate))) " +
            "    FROM DiagnosisMedItem dmi3 JOIN dmi3.diagnosis d3 JOIN dmi3.medicalRecordItem mri3 " +
            "    WHERE d3.id = d.id AND mri3.medicalRecord.patient.id = p.id " +
            "    AND mri3.entryDate > MIN(mri.entryDate) " +
            "    AND mri3.entryDate <= FUNCTION('DATE_ADD', MIN(mri.entryDate), INTERVAL :monthsWindow MONTH)) as daysTillReadmission " +
            "   FROM Diagnosis d " +
            "   JOIN d.medicalRecordItems dmi " +
            "   JOIN dmi.medicalRecordItem mri " +
            "   JOIN mri.medicalRecord mr " +
            "   JOIN mr.patient p " +
            "   WHERE d.id = :diagnosisId " +
            "   GROUP BY d.id, d.specialty.id, p.id" +
            ") as patientReadmissions " +
            "JOIN Diagnosis d ON d.id = diagnosisId " +
            "GROUP BY d.specialty.name " +
            "ORDER BY readmissionRate DESC";
        
        return entityManager.createQuery(jpql)
            .setParameter("diagnosisId", diagnosisId)
            .setParameter("monthsWindow", monthsWindow)
            .getResultList();
    }
    
    /**
     * Get clinical pathway analysis.
     * This analysis identifies common sequences of treatments for specific diagnoses.
     *
     * @param diagnosisId The diagnosis ID
     * @param minPatients Minimum number of patients to include in analysis
     * @return List of clinical pathway statistics
     */
    @SuppressWarnings("unchecked")
    public List<Object[]> getClinicalPathwayAnalysis(Long diagnosisId, int minPatients) {
        String jpql = 
            "SELECT " +
            "   t1.id as firstTreatmentId, " +
            "   t1.name as firstTreatmentName, " +
            "   t2.id as secondTreatmentId, " +
            "   t2.name as secondTreatmentName, " +
            "   COUNT(DISTINCT p) as patientCount, " +
            "   AVG(FUNCTION('DATEDIFF', p2.issuedAt, p1.issuedAt)) as avgDaysBetweenTreatments " +
            "FROM Patient p " +
            "JOIN p.medicalRecord mr " +
            "JOIN mr.items mri " +
            "JOIN mri.diagnoses dmi " +
            "JOIN p.prescriptions p1 " +
            "JOIN p1.treatments pt1 " +
            "JOIN pt1.treatment t1 " +
            "JOIN p.prescriptions p2 " +
            "JOIN p2.treatments pt2 " +
            "JOIN pt2.treatment t2 " +
            "WHERE dmi.diagnosis.id = :diagnosisId " +
            "AND p1.id != p2.id " +
            "AND p1.issuedAt < p2.issuedAt " +
            "AND NOT EXISTS (" +
            "    SELECT 1 FROM Prescription p3 " +
            "    WHERE p3.patient = p " +
            "    AND p3.issuedAt > p1.issuedAt " +
            "    AND p3.issuedAt < p2.issuedAt" +
            ") " +
            "GROUP BY t1.id, t1.name, t2.id, t2.name " +
            "HAVING COUNT(DISTINCT p) >= :minPatients " +
            "ORDER BY patientCount DESC";
        
        return entityManager.createQuery(jpql)
            .setParameter("diagnosisId", diagnosisId)
            .setParameter("minPatients", minPatients)
            .getResultList();
    }
}