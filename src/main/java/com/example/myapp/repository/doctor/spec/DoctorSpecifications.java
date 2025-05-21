package com.example.myapp.repository.doctor.spec;

import com.example.myapp.entity.Doctor;
import com.example.myapp.entity.DoctorSpecialty.ExperienceLevel;
import org.springframework.data.jpa.domain.Specification;

import jakarta.persistence.criteria.Root;
import jakarta.persistence.criteria.Subquery;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

/**
 * Specifications for dynamically building Doctor queries.
 * Provides reusable query components that can be combined as needed.
 */
public class DoctorSpecifications {

    /**
     * Creates a specification to find doctors by name.
     *
     * @param searchText Text to search in first and last name
     * @return Specification for name search
     */
    public static Specification<Doctor> nameContains(String searchText) {
        return (root, query, cb) -> {
            if (searchText == null || searchText.trim().isEmpty()) {
                return cb.conjunction(); // Always true predicate
            }
            String pattern = "%" + searchText.toLowerCase() + "%";
            return cb.or(
                cb.like(cb.lower(root.get("firstName")), pattern),
                cb.like(cb.lower(root.get("lastName")), pattern)
            );
        };
    }
    
    /**
     * Creates a specification to find doctors by license number.
     *
     * @param licenseNumber The license number
     * @return Specification for license number search
     */
    public static Specification<Doctor> hasLicenseNumber(String licenseNumber) {
        return (root, query, cb) -> {
            if (licenseNumber == null || licenseNumber.trim().isEmpty()) {
                return cb.conjunction();
            }
            return cb.equal(root.get("licenseNumber"), licenseNumber);
        };
    }
    
    /**
     * Creates a specification to find doctors by specialty ID.
     *
     * @param specialtyId The specialty ID
     * @return Specification for specialty search
     */
    public static Specification<Doctor> hasSpecialty(Long specialtyId) {
        return (root, query, cb) -> {
            if (specialtyId == null) {
                return cb.conjunction();
            }
            return cb.equal(
                root.join("specialties").join("specialty").get("id"), 
                specialtyId
            );
        };
    }
    
    /**
     * Creates a specification to find doctors by multiple specialty IDs (has any).
     *
     * @param specialtyIds List of specialty IDs
     * @return Specification for multiple specialties search
     */
    public static Specification<Doctor> hasAnySpecialty(List<Long> specialtyIds) {
        return (root, query, cb) -> {
            if (specialtyIds == null || specialtyIds.isEmpty()) {
                return cb.conjunction();
            }
            
            if (query != null) {
                query.distinct(true); // Ensure distinct results
            }
            
            var specialtyJoin = root.join("specialties").join("specialty");
            return specialtyJoin.get("id").in(specialtyIds);
        };
    }
    
    /**
     * Creates a specification to find doctors by experience level.
     *
     * @param level The experience level
     * @return Specification for experience level search
     */
    public static Specification<Doctor> hasExperienceLevel(ExperienceLevel level) {
        return (root, query, cb) -> {
            if (level == null) {
                return cb.conjunction();
            }
            
            if (query != null) {
                query.distinct(true);
            }
            
            return cb.equal(
                root.join("specialties").get("experienceLevel"),
                level
            );
        };
    }
    
    /**
     * Creates a specification to find doctors with a minimum experience level or higher.
     *
     * @param minimumLevel The minimum experience level
     * @return Specification for minimum experience level search
     */
    public static Specification<Doctor> hasMinimumExperienceLevel(ExperienceLevel minimumLevel) {
        return (root, query, cb) -> {
            if (minimumLevel == null) {
                return cb.conjunction();
            }
            
            if (query != null) {
                query.distinct(true);
            }
            
            // Convert enum to ordinal for comparison
            return cb.greaterThanOrEqualTo(
                cb.function("get_enum_ordinal", Integer.class, root.join("specialties").get("experienceLevel")),
                cb.literal(minimumLevel.ordinal())
            );
        };
    }
    
    /**
     * Creates a specification to find doctors certified after a certain date.
     *
     * @param afterDate The certification date threshold
     * @return Specification for certification date search
     */
    public static Specification<Doctor> certifiedAfter(LocalDate afterDate) {
        return (root, query, cb) -> {
            if (afterDate == null) {
                return cb.conjunction();
            }
            
            if (query != null) {
                query.distinct(true);
            }
            
            return cb.greaterThanOrEqualTo(
                root.join("specialties").get("certificationDate"),
                afterDate
            );
        };
    }
    
    /**
     * Creates a specification to find doctors available on a specific date.
     *
     * @param date The date to check availability
     * @return Specification for date availability search
     */
    public static Specification<Doctor> isAvailableOnDate(LocalDate date) {
        return (root, query, cb) -> {
            if (date == null) {
                return cb.conjunction();
            }
            
            if (query != null) {
                query.distinct(true);
            }
            
            return cb.and(
                cb.equal(root.join("schedules").join("scheduleDates").get("date"), date),
                cb.equal(root.join("schedules").join("scheduleDates").get("status"), "ACTIVE")
            );
        };
    }
    
    /**
     * Creates a specification to find doctors available during a specific time slot.
     *
     * @param startTime The start time
     * @param endTime The end time
     * @return Specification for time availability search
     */
    public static Specification<Doctor> isAvailableDuringTimeSlot(LocalTime startTime, LocalTime endTime) {
        return (root, query, cb) -> {
            if (startTime == null || endTime == null) {
                return cb.conjunction();
            }
            
            if (query != null) {
                query.distinct(true);
            }
            
            var scheduleJoin = root.join("schedules");
            return cb.and(
                cb.lessThanOrEqualTo(scheduleJoin.get("startTime"), startTime),
                cb.greaterThanOrEqualTo(scheduleJoin.get("endTime"), endTime)
            );
        };
    }
    
    /**
     * Creates a specification to find doctors by their ability to treat a specific diagnosis.
     *
     * @param diagnosisId The diagnosis ID
     * @return Specification for diagnosis capability search
     */
    public static Specification<Doctor> canTreatDiagnosis(Long diagnosisId) {
        return (root, query, cb) -> {
            if (diagnosisId == null) {
                return cb.conjunction();
            }
            
            if (query != null) {
                query.distinct(true);
            }
            
            return cb.equal(
                root.join("specialties").join("specialty").join("diagnoses").get("id"),
                diagnosisId
            );
        };
    }
    
    /**
     * Creates a specification to find doctors who can perform a specific treatment.
     *
     * @param treatmentId The treatment ID
     * @return Specification for treatment capability search
     */
    public static Specification<Doctor> canPerformTreatment(Long treatmentId) {
        return (root, query, cb) -> {
            if (treatmentId == null) {
                return cb.conjunction();
            }
            
            if (query != null) {
                query.distinct(true);
            }
            
            return cb.equal(
                root.join("specialties").join("specialty").join("treatments").get("id"),
                treatmentId
            );
        };
    }
    
    /**
     * Creates a specification to find doctors with a minimum number of specialties.
     *
     * @param minSpecialties The minimum number of specialties
     * @return Specification for specialty count search
     */
    public static Specification<Doctor> hasMinimumSpecialtiesCount(Integer minSpecialties) {
        return (root, query, cb) -> {
            if (minSpecialties == null || minSpecialties <= 0) {
                return cb.conjunction();
            }
            
            if (query != null) {
                query.distinct(true);
                
                // Create and use subquery
                Subquery<Long> subquery = query.subquery(Long.class);
                Root<Doctor> subRoot = subquery.from(Doctor.class);
                
                subquery.select(cb.count(subRoot.join("specialties")))
                        .where(cb.equal(subRoot, root));
                
                return cb.greaterThanOrEqualTo(subquery, (long) minSpecialties);
            } else {
                // Fallback if query is null (should not happen in normal execution)
                return cb.conjunction();
            }
        };
    }
    
    /**
     * Creates a specification to find doctors by activation status.
     *
     * @param isActivated The activation status
     * @return Specification for activation status search
     */
    public static Specification<Doctor> hasActivationStatus(Boolean isActivated) {
        return (root, query, cb) -> {
            if (isActivated == null) {
                return cb.conjunction();
            }
            return cb.equal(root.get("isActivated"), isActivated);
        };
    }
    
    /**
     * Creates a specification to find doctors by city ID.
     *
     * @param cityId The city ID
     * @return Specification for city search
     */
    public static Specification<Doctor> livesInCity(Long cityId) {
        return (root, query, cb) -> {
            if (cityId == null) {
                return cb.conjunction();
            }
            
            if (query != null) {
                query.distinct(true);
            }
            
            return cb.equal(
                root.join("address").join("city").get("id"),
                cityId
            );
        };
    }
    
    /**
     * Creates a specification to find doctors with appointments in a date range.
     *
     * @param startDate The start date
     * @param endDate The end date
     * @return Specification for appointment date range search
     */
    public static Specification<Doctor> hasAppointmentsInDateRange(LocalDate startDate, LocalDate endDate) {
        return (root, query, cb) -> {
            if (startDate == null || endDate == null) {
                return cb.conjunction();
            }
            
            if (query != null) {
                query.distinct(true);
            }
            
            return cb.between(
                root.join("schedules").join("appointments").get("createdAt").as(LocalDate.class),
                startDate,
                endDate
            );
        };
    }
}