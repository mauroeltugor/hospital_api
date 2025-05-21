package com.example.myapp.repository.custom;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Repository for complex medical statistics queries.
 * Provides advanced analytics across multiple entities.
 */
@Repository
public class MedicalStatisticsRepository {

    @PersistenceContext
    private EntityManager entityManager;
    
    /**
     * Get diagnosis distribution by age group.
     *
     * @param startDate The start date
     * @param endDate The end date
     * @return Map of statistics
     */
    @SuppressWarnings("unchecked")
    public List<Object[]> getDiagnosisDistributionByAgeGroup(LocalDate startDate, LocalDate endDate) {
        String jpql = 
            "SELECT " +
            "   CASE " +
            "       WHEN FUNCTION('TIMESTAMPDIFF', YEAR, p.birthDate, CURRENT_DATE) < 18 THEN 'Under 18' " +
            "       WHEN FUNCTION('TIMESTAMPDIFF', YEAR, p.birthDate, CURRENT_DATE) BETWEEN 18 AND 30 THEN '18-30' " +
            "       WHEN FUNCTION('TIMESTAMPDIFF', YEAR, p.birthDate, CURRENT_DATE) BETWEEN 31 AND 50 THEN '31-50' " +
            "       WHEN FUNCTION('TIMESTAMPDIFF', YEAR, p.birthDate, CURRENT_DATE) BETWEEN 51 AND 65 THEN '51-65' " +
            "       ELSE 'Over 65' " +
            "   END as ageGroup, " +
            "   d.id as diagnosisId, " +
            "   d.name as diagnosisName, " +
            "   COUNT(dmi) as count " +
            "FROM DiagnosisMedItem dmi " +
            "JOIN dmi.diagnosis d " +
            "JOIN dmi.medicalRecordItem mri " +
            "JOIN mri.medicalRecord mr " +
            "JOIN mr.patient p " +
            "WHERE mri.entryDate BETWEEN :startDateTime AND :endDateTime " +
            "GROUP BY ageGroup, d.id, d.name " +
            "ORDER BY ageGroup, count DESC";
        
        LocalDateTime startDateTime = startDate.atStartOfDay();
        LocalDateTime endDateTime = endDate.plusDays(1).atStartOfDay();
        
        return entityManager.createQuery(jpql)
            .setParameter("startDateTime", startDateTime)
            .setParameter("endDateTime", endDateTime)
            .getResultList();
    }
    
    /**
     * Get treatment effectiveness by diagnosis.
     *
     * @param minSessions Minimum number of sessions to include in analysis
     * @return Map of statistics
     */
    @SuppressWarnings("unchecked")
    public List<Object[]> getTreatmentEffectivenessByDiagnosis(int minSessions) {
        String jpql = 
            "SELECT " +
            "   d.id as diagnosisId, " +
            "   d.name as diagnosisName, " +
            "   t.id as treatmentId, " +
            "   t.name as treatmentName, " +
            "   AVG(s.effectiveness) as avgEffectiveness, " +
            "   COUNT(s) as sessionCount " +
            "FROM Session s " +
            "JOIN s.treatment t " +
            "JOIN s.patient p " +
            "JOIN p.medicalRecord mr " +
            "JOIN mr.items mri " +
            "JOIN mri.diagnoses dmi " +
            "JOIN dmi.diagnosis d " +
            "WHERE s.effectiveness IS NOT NULL " +
            "GROUP BY d.id, d.name, t.id, t.name " +
            "HAVING COUNT(s) >= :minSessions " +
            "ORDER BY d.name, avgEffectiveness DESC";
        
        return entityManager.createQuery(jpql)
            .setParameter("minSessions", minSessions)
            .getResultList();
    }
    
    /**
     * Get doctor performance metrics.
     *
     * @param startDate The start date
     * @param endDate The end date
     * @return Map of statistics
     */
    @SuppressWarnings("unchecked")
    public List<Object[]> getDoctorPerformanceMetrics(LocalDate startDate, LocalDate endDate) {
        String jpql = 
            "SELECT " +
            "   d.id as doctorId, " +
            "   d.firstName as firstName, " +
            "   d.lastName as lastName, " +
            "   COUNT(DISTINCT a) as appointmentCount, " +
            "   COUNT(DISTINCT p) as prescriptionCount, " +
            "   COUNT(DISTINCT mri) as medicalRecordEntries, " +
            "   AVG(a.effectiveness) as avgAppointmentEffectiveness " +
            "FROM Doctor d " +
            "LEFT JOIN d.schedules s " +
            "LEFT JOIN s.appointments a ON a.status = 'COMPLETED' " +
            "AND FUNCTION('DATE', a.createdAt) BETWEEN :startDate AND :endDate " +
            "LEFT JOIN d.prescriptions p ON FUNCTION('DATE', p.issuedAt) BETWEEN :startDate AND :endDate " +
            "LEFT JOIN d.medicalRecordItems mri ON FUNCTION('DATE', mri.entryDate) BETWEEN :startDate AND :endDate " +
            "GROUP BY d.id, d.firstName, d.lastName " +
            "ORDER BY appointmentCount DESC";
        
        return entityManager.createQuery(jpql)
            .setParameter("startDate", startDate)
            .setParameter("endDate", endDate)
            .getResultList();
    }
    
    /**
     * Get patient visit patterns.
     *
     * @param minPatients Minimum number of patients to include in analysis
     * @return Map of statistics
     */
    @SuppressWarnings("unchecked")
    public List<Object[]> getPatientVisitPatterns(int minPatients) {
        String jpql = 
            "SELECT " +
            "   FUNCTION('DAYOFWEEK', a.createdAt) as dayOfWeek, " +
            "   FUNCTION('HOUR', a.createdAt) as hourOfDay, " +
            "   COUNT(a) as appointmentCount " +
            "FROM Appointment a " +
            "WHERE a.status IN ('COMPLETED', 'CONFIRMED') " +
            "GROUP BY dayOfWeek, hourOfDay " +
            "HAVING COUNT(a) >= :minPatients " +
            "ORDER BY appointmentCount DESC";
        
        return entityManager.createQuery(jpql)
            .setParameter("minPatients", minPatients)
            .getResultList();
    }
    
    /**
     * Get geographic distribution of diagnoses.
     *
     * @param diagnosisId The diagnosis ID (optional, can be null for all diagnoses)
     * @return Map of statistics
     */
    @SuppressWarnings("unchecked")
    public List<Object[]> getGeographicDistributionOfDiagnoses(Long diagnosisId) {
        StringBuilder jpql = new StringBuilder(
            "SELECT " +
            "   co.id as countryId, " +
            "   co.name as countryName, " +
            "   c.id as cityId, " +
            "   c.name as cityName, " +
            "   d.id as diagnosisId, " +
            "   d.name as diagnosisName, " +
            "   COUNT(dmi) as count " +
            "FROM DiagnosisMedItem dmi " +
            "JOIN dmi.diagnosis d " +
            "JOIN dmi.medicalRecordItem mri " +
            "JOIN mri.medicalRecord mr " +
            "JOIN mr.patient p " +
            "JOIN p.address a " +
            "JOIN a.city c " +
            "JOIN c.country co " +
            "WHERE 1=1 "
        );
        
        if (diagnosisId != null) {
            jpql.append("AND d.id = :diagnosisId ");
        }
        
        jpql.append(
            "GROUP BY co.id, co.name, c.id, c.name, d.id, d.name " +
            "ORDER BY co.name, c.name, count DESC"
        );
        
        var query = entityManager.createQuery(jpql.toString());
        if (diagnosisId != null) {
            query.setParameter("diagnosisId", diagnosisId);
        }
        
        return query.getResultList();
    }
    
    /**
     * Get comorbidity analysis.
     * This analyzes which diagnoses commonly occur together.
     *
     * @param minOccurrences Minimum number of co-occurrences
     * @return Map of statistics
     */
    @SuppressWarnings("unchecked")
    public List<Object[]> getComorbidityAnalysis(int minOccurrences) {
        String jpql = 
            "SELECT " +
            "   d1.id as diagnosis1Id, " +
            "   d1.name as diagnosis1Name, " +
            "   d2.id as diagnosis2Id, " +
            "   d2.name as diagnosis2Name, " +
            "   COUNT(mr.id) as patientCount " +
            "FROM DiagnosisMedItem dmi1 " +
            "JOIN dmi1.diagnosis d1 " +
            "JOIN dmi1.medicalRecordItem mri1 " +
            "JOIN mri1.medicalRecord mr " +
            "JOIN mr.items mri2 " +
            "JOIN mri2.diagnoses dmi2 " +
            "JOIN dmi2.diagnosis d2 " +
            "WHERE d1.id < d2.id " +
            "GROUP BY d1.id, d1.name, d2.id, d2.name " +
            "HAVING COUNT(mr.id) >= :minOccurrences " +
            "ORDER BY patientCount DESC";
        
        return entityManager.createQuery(jpql)
            .setParameter("minOccurrences", minOccurrences)
            .getResultList();
    }
    
    /**
     * Get seasonal diagnosis trends.
     *
     * @param yearCount Number of years to analyze
     * @return Map of statistics
     */
    @SuppressWarnings("unchecked")
    public List<Object[]> getSeasonalDiagnosisTrends(int yearCount) {
        String jpql = 
            "SELECT " +
            "   FUNCTION('MONTH', mri.entryDate) as month, " +
            "   d.id as diagnosisId, " +
            "   d.name as diagnosisName, " +
            "   COUNT(dmi) as count " +
            "FROM DiagnosisMedItem dmi " +
            "JOIN dmi.diagnosis d " +
            "JOIN dmi.medicalRecordItem mri " +
            "WHERE mri.entryDate >= FUNCTION('DATE_SUB', CURRENT_DATE, INTERVAL :yearCount YEAR) " +
            "GROUP BY month, d.id, d.name " +
            "ORDER BY month, count DESC";
        
        return entityManager.createQuery(jpql)
            .setParameter("yearCount", yearCount)
            .getResultList();
    }
    
    /**
     * Get referral patterns between specialties.
     * This analyzes how patients move between medical specialties.
     *
     * @param startDate The start date
     * @param endDate The end date
     * @return Map of statistics
     */
    @SuppressWarnings("unchecked")
    public List<Object[]> getReferralPatternsBetweenSpecialties(LocalDate startDate, LocalDate endDate) {
        String jpql = 
            "SELECT " +
            "   s1.id as sourceSpecialtyId, " +
            "   s1.name as sourceSpecialtyName, " +
            "   s2.id as targetSpecialtyId, " +
            "   s2.name as targetSpecialtyName, " +
            "   COUNT(DISTINCT p.id) as patientCount " +
            "FROM Appointment a1 " +
            "JOIN a1.specialty s1 " +
            "JOIN a1.patient p " +
            "JOIN p.appointments a2 " +
            "JOIN a2.specialty s2 " +
            "WHERE s1.id != s2.id " +
            "AND FUNCTION('DATE', a1.createdAt) BETWEEN :startDate AND :endDate " +
            "AND FUNCTION('DATE', a2.createdAt) BETWEEN :startDate AND :endDate " +
            "AND a1.createdAt < a2.createdAt " +
            "GROUP BY s1.id, s1.name, s2.id, s2.name " +
            "ORDER BY patientCount DESC";
        
        return entityManager.createQuery(jpql)
            .setParameter("startDate", startDate)
            .setParameter("endDate", endDate)
            .getResultList();
    }
}