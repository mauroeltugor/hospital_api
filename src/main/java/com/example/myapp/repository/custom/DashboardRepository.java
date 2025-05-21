package com.example.myapp.repository.custom;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Repository for dashboard-related queries.
 * Provides key metrics and summaries for dashboard views.
 */
@Repository
public class DashboardRepository {

    @PersistenceContext
    private EntityManager entityManager;
    
    /**
     * Get key metrics for the system dashboard.
     *
     * @param days Number of days to include in trend analysis
     * @return Map containing key metrics
     */
    public Map<String, Object> getSystemDashboardMetrics(int days) {
        Map<String, Object> metrics = new HashMap<>();
        
        // User counts
        metrics.put("userCounts", getUserCounts());
        
        // Activity metrics
        metrics.put("activityMetrics", getActivityMetrics(days));
        
        // Appointment status distribution
        metrics.put("appointmentStatusDistribution", getAppointmentStatusDistribution());
        
        // Top specialties by activity
        metrics.put("topSpecialties", getTopSpecialties());
        
        // Top doctors by appointments
        metrics.put("topDoctors", getTopDoctorsByAppointments(5));
        
        return metrics;
    }
    
    /**
     * Get key metrics for a doctor's dashboard.
     *
     * @param doctorId The doctor's ID
     * @param days Number of days to include in trend analysis
     * @return Map containing doctor-specific metrics
     */
    public Map<String, Object> getDoctorDashboardMetrics(Long doctorId, int days) {
        Map<String, Object> metrics = new HashMap<>();
        
        // Doctor's appointment metrics
        metrics.put("appointmentMetrics", getDoctorAppointmentMetrics(doctorId, days));
        
        // Patient demographics
        metrics.put("patientDemographics", getDoctorPatientDemographics(doctorId));
        
        // Top diagnoses
        metrics.put("topDiagnoses", getDoctorTopDiagnoses(doctorId, 5));
        
        // Top treatments
        metrics.put("topTreatments", getDoctorTopTreatments(doctorId, 5));
        
        // Upcoming appointments
        metrics.put("upcomingAppointments", getDoctorUpcomingAppointments(doctorId));
        
        return metrics;
    }
    
    /**
     * Get key metrics for a patient's dashboard.
     *
     * @param patientId The patient's ID
     * @return Map containing patient-specific metrics
     */
    public Map<String, Object> getPatientDashboardMetrics(Long patientId) {
        Map<String, Object> metrics = new HashMap<>();
        
        // Patient's appointment history
        metrics.put("appointmentHistory", getPatientAppointmentHistory(patientId));
        
        // Current medications
        metrics.put("currentMedications", getPatientCurrentMedications(patientId));
        
        // Recent diagnoses
        metrics.put("recentDiagnoses", getPatientRecentDiagnoses(patientId));
        
        // Upcoming appointments
        metrics.put("upcomingAppointments", getPatientUpcomingAppointments(patientId));
        
        return metrics;
    }
    
    // Private helper methods for dashboard metrics
    
    @SuppressWarnings("unchecked")
    private Map<String, Long> getUserCounts() {
        String jpql = 
            "SELECT " +
            "   u.userType as userType, " +
            "   COUNT(u) as count " +
            "FROM User u " +
            "GROUP BY u.userType";
        
        List<Object[]> results = entityManager.createQuery(jpql).getResultList();
        
        Map<String, Long> counts = new HashMap<>();
        for (Object[] result : results) {
            counts.put(result[0].toString(), (Long) result[1]);
        }
        
        return counts;
    }
    
    @SuppressWarnings("unchecked")
    private List<Object[]> getActivityMetrics(int days) {
        LocalDateTime startDate = LocalDateTime.now().minusDays(days);
        
        String jpql = 
            "SELECT " +
            "   FUNCTION('DATE', a.createdAt) as date, " +
            "   COUNT(a) as appointmentCount, " +
            "   COUNT(DISTINCT a.patient) as patientCount " +
            "FROM Appointment a " +
            "WHERE a.createdAt >= :startDate " +
            "GROUP BY FUNCTION('DATE', a.createdAt) " +
            "ORDER BY date";
        
        return entityManager.createQuery(jpql)
            .setParameter("startDate", startDate)
            .getResultList();
    }
    
    @SuppressWarnings("unchecked")
    private List<Object[]> getAppointmentStatusDistribution() {
        String jpql = 
            "SELECT " +
            "   a.status as status, " +
            "   COUNT(a) as count " +
            "FROM Appointment a " +
            "GROUP BY a.status";
        
        return entityManager.createQuery(jpql).getResultList();
    }
    
    @SuppressWarnings("unchecked")
    private List<Object[]> getTopSpecialties() {
        String jpql = 
            "SELECT " +
            "   s.id as specialtyId, " +
            "   s.name as specialtyName, " +
            "   COUNT(a) as appointmentCount " +
            "FROM Appointment a " +
            "JOIN a.specialty s " +
            "GROUP BY s.id, s.name " +
            "ORDER BY appointmentCount DESC";
        
        return entityManager.createQuery(jpql)
            .setMaxResults(5)
            .getResultList();
    }
    
    @SuppressWarnings("unchecked")
    private List<Object[]> getTopDoctorsByAppointments(int limit) {
        String jpql = 
            "SELECT " +
            "   d.id as doctorId, " +
            "   d.firstName as firstName, " +
            "   d.lastName as lastName, " +
            "   COUNT(a) as appointmentCount " +
            "FROM Doctor d " +
            "JOIN d.schedules s " +
            "JOIN s.appointments a " +
            "GROUP BY d.id, d.firstName, d.lastName " +
            "ORDER BY appointmentCount DESC";
        
        return entityManager.createQuery(jpql)
            .setMaxResults(limit)
            .getResultList();
    }
    
    private Map<String, Object> getDoctorAppointmentMetrics(Long doctorId, int days) {
        LocalDate startDate = LocalDate.now().minusDays(days);
        
        String jpql = 
            "SELECT " +
            "   COUNT(a) as totalAppointments, " +
            "   COUNT(CASE WHEN a.status = 'COMPLETED' THEN 1 ELSE NULL END) as completedAppointments, " +
            "   COUNT(CASE WHEN a.status = 'CANCELLED' THEN 1 ELSE NULL END) as cancelledAppointments, " +
            "   COUNT(CASE WHEN a.status = 'NO_SHOW' THEN 1 ELSE NULL END) as noShowAppointments, " +
            "   COUNT(DISTINCT a.patient) as uniquePatients, " +
            "   AVG(a.effectiveness) as avgEffectiveness " +
            "FROM Doctor d " +
            "JOIN d.schedules s " +
            "JOIN s.appointments a " +
            "WHERE d.id = :doctorId " +
            "AND FUNCTION('DATE', a.createdAt) >= :startDate";
        
        Object[] result = (Object[]) entityManager.createQuery(jpql)
            .setParameter("doctorId", doctorId)
            .setParameter("startDate", startDate)
            .getSingleResult();
        
        Map<String, Object> metrics = new HashMap<>();
        metrics.put("totalAppointments", result[0]);
        metrics.put("completedAppointments", result[1]);
        metrics.put("cancelledAppointments", result[2]);
        metrics.put("noShowAppointments", result[3]);
        metrics.put("uniquePatients", result[4]);
        metrics.put("avgEffectiveness", result[5]);
        
        return metrics;
    }
    
    @SuppressWarnings("unchecked")
    private List<Object[]> getDoctorPatientDemographics(Long doctorId) {
        String jpql = 
            "SELECT " +
            "   CASE " +
            "       WHEN FUNCTION('TIMESTAMPDIFF', YEAR, p.birthDate, CURRENT_DATE) < 18 THEN 'Under 18' " +
            "       WHEN FUNCTION('TIMESTAMPDIFF', YEAR, p.birthDate, CURRENT_DATE) BETWEEN 18 AND 30 THEN '18-30' " +
            "       WHEN FUNCTION('TIMESTAMPDIFF', YEAR, p.birthDate, CURRENT_DATE) BETWEEN 31 AND 50 THEN '31-50' " +
            "       WHEN FUNCTION('TIMESTAMPDIFF', YEAR, p.birthDate, CURRENT_DATE) BETWEEN 51 AND 65 THEN '51-65' " +
            "       ELSE 'Over 65' " +
            "   END as ageGroup, " +
            "   p.gender as gender, " +
            "   COUNT(DISTINCT p) as patientCount " +
            "FROM Doctor d " +
            "JOIN d.schedules s " +
            "JOIN s.appointments a " +
            "JOIN a.patient p " +
            "WHERE d.id = :doctorId " +
            "GROUP BY ageGroup, p.gender";
        
        return entityManager.createQuery(jpql)
            .setParameter("doctorId", doctorId)
            .getResultList();
    }
    
    @SuppressWarnings("unchecked")
    private List<Object[]> getDoctorTopDiagnoses(Long doctorId, int limit) {
        String jpql = 
            "SELECT " +
            "   d.id as diagnosisId, " +
            "   d.name as diagnosisName, " +
            "   COUNT(dmi) as count " +
            "FROM Doctor doc " +
            "JOIN doc.medicalRecordItems mri " +
            "JOIN mri.diagnoses dmi " +
            "JOIN dmi.diagnosis d " +
            "WHERE doc.id = :doctorId " +
            "GROUP BY d.id, d.name " +
            "ORDER BY count DESC";
        
        return entityManager.createQuery(jpql)
            .setParameter("doctorId", doctorId)
            .setMaxResults(limit)
            .getResultList();
    }
    
    @SuppressWarnings("unchecked")
    private List<Object[]> getDoctorTopTreatments(Long doctorId, int limit) {
        String jpql = 
            "SELECT " +
            "   t.id as treatmentId, " +
            "   t.name as treatmentName, " +
            "   COUNT(pt) as count " +
            "FROM Doctor d " +
            "JOIN d.prescriptions p " +
            "JOIN p.treatments pt " +
            "JOIN pt.treatment t " +
            "WHERE d.id = :doctorId " +
            "GROUP BY t.id, t.name " +
            "ORDER BY count DESC";
        
        return entityManager.createQuery(jpql)
            .setParameter("doctorId", doctorId)
            .setMaxResults(limit)
            .getResultList();
    }
    
    @SuppressWarnings("unchecked")
    private List<Object[]> getDoctorUpcomingAppointments(Long doctorId) {
        LocalDate today = LocalDate.now();
        
        String jpql = 
            "SELECT " +
            "   a.id as appointmentId, " +
            "   sd.date as appointmentDate, " +
            "   s.startTime as startTime, " +
            "   p.id as patientId, " +
            "   p.firstName as patientFirstName, " +
            "   p.lastName as patientLastName, " +
            "   sp.name as specialtyName " +
            "FROM Doctor d " +
            "JOIN d.schedules s " +
            "JOIN s.scheduleDates sd " +
            "JOIN s.appointments a " +
            "JOIN a.patient p " +
            "JOIN a.specialty sp " +
            "WHERE d.id = :doctorId " +
            "AND sd.date >= :today " +
            "AND a.status IN ('SCHEDULED', 'CONFIRMED') " +
            "ORDER BY sd.date, s.startTime";
        
        return entityManager.createQuery(jpql)
            .setParameter("doctorId", doctorId)
            .setParameter("today", today)
            .setMaxResults(10)
            .getResultList();
    }
    
    @SuppressWarnings("unchecked")
    private List<Object[]> getPatientAppointmentHistory(Long patientId) {
        String jpql = 
            "SELECT " +
            "   a.id as appointmentId, " +
            "   FUNCTION('DATE', a.createdAt) as appointmentDate, " +
            "   d.firstName as doctorFirstName, " +
            "   d.lastName as doctorLastName, " +
            "   sp.name as specialtyName, " +
            "   a.status as status, " +
            "   a.effectiveness as effectiveness " +
            "FROM Patient p " +
            "JOIN p.appointments a " +
            "JOIN a.schedule s " +
            "JOIN s.doctor d " +
            "JOIN a.specialty sp " +
            "WHERE p.id = :patientId " +
            "ORDER BY a.createdAt DESC";
        
        return entityManager.createQuery(jpql)
            .setParameter("patientId", patientId)
            .setMaxResults(20)
            .getResultList();
    }
    
    @SuppressWarnings("unchecked")
    private List<Object[]> getPatientCurrentMedications(Long patientId) {
        LocalDate today = LocalDate.now();
        
        String jpql = 
            "SELECT " +
            "   t.id as treatmentId, " +
            "   t.name as treatmentName, " +
            "   p.issuedAt as prescribedDate, " +
            "   p.expiresAt as expiresDate, " +
            "   d.firstName as doctorFirstName, " +
            "   d.lastName as doctorLastName, " +
            "   p.notes as notes " +
            "FROM Patient pt " +
            "JOIN pt.prescriptions p " +
            "JOIN p.treatments prt " +
            "JOIN prt.treatment t " +
            "JOIN p.doctor d " +
            "WHERE pt.id = :patientId " +
            "AND (p.expiresAt IS NULL OR p.expiresAt >= :today) " +
            "ORDER BY p.issuedAt DESC";
        
        return entityManager.createQuery(jpql)
            .setParameter("patientId", patientId)
            .setParameter("today", today)
            .getResultList();
    }
    
    @SuppressWarnings("unchecked")
    private List<Object[]> getPatientRecentDiagnoses(Long patientId) {
        String jpql = 
            "SELECT " +
            "   d.id as diagnosisId, " +
            "   d.name as diagnosisName, " +
            "   mri.entryDate as diagnosisDate, " +
            "   doc.firstName as doctorFirstName, " +
            "   doc.lastName as doctorLastName, " +
            "   mri.notes as notes " +
            "FROM Patient p " +
            "JOIN p.medicalRecord mr " +
            "JOIN mr.items mri " +
            "JOIN mri.diagnoses dmi " +
            "JOIN dmi.diagnosis d " +
            "JOIN mri.doctor doc " +
            "WHERE p.id = :patientId " +
            "ORDER BY mri.entryDate DESC";
        
        return entityManager.createQuery(jpql)
            .setParameter("patientId", patientId)
            .setMaxResults(10)
            .getResultList();
    }
    
    @SuppressWarnings("unchecked")
    private List<Object[]> getPatientUpcomingAppointments(Long patientId) {
        LocalDate today = LocalDate.now();
        
        String jpql = 
            "SELECT " +
            "   a.id as appointmentId, " +
            "   sd.date as appointmentDate, " +
            "   s.startTime as startTime, " +
            "   d.firstName as doctorFirstName, " +
            "   d.lastName as doctorLastName, " +
            "   sp.name as specialtyName, " +
            "   a.status as status " +
            "FROM Patient p " +
            "JOIN p.appointments a " +
            "JOIN a.schedule s " +
            "JOIN s.scheduleDates sd " +
            "JOIN s.doctor d " +
            "JOIN a.specialty sp " +
            "WHERE p.id = :patientId " +
            "AND sd.date >= :today " +
            "AND a.status IN ('SCHEDULED', 'CONFIRMED') " +
            "ORDER BY sd.date, s.startTime";
        
        return entityManager.createQuery(jpql)
            .setParameter("patientId", patientId)
            .setParameter("today", today)
            .getResultList();
    }
}