package com.example.myapp.repository.doctor.spec;

import com.example.myapp.entity.DoctorSchedule;
import com.example.myapp.entity.DoctorSchedule.WorkDay;
import org.springframework.data.jpa.domain.Specification;

import jakarta.persistence.criteria.Root;
import jakarta.persistence.criteria.Subquery;
import java.time.LocalDate;
import java.time.LocalTime;

/**
 * Specifications for dynamically building DoctorSchedule queries.
 * Provides reusable query components that can be combined as needed.
 */
public class DoctorScheduleSpecifications {

    /**
     * Creates a specification to find schedules by doctor ID.
     *
     * @param doctorId The doctor ID
     * @return Specification for doctor search
     */
    public static Specification<DoctorSchedule> belongsToDoctor(Long doctorId) {
        return (root, query, cb) -> {
            if (doctorId == null) {
                return cb.conjunction();
            }
            return cb.equal(root.join("doctor").get("id"), doctorId);
        };
    }
    
    /**
     * Creates a specification to find schedules by work day.
     *
     * @param workDay The work day
     * @return Specification for work day search
     */
    public static Specification<DoctorSchedule> hasWorkDay(WorkDay workDay) {
        return (root, query, cb) -> {
            if (workDay == null) {
                return cb.conjunction();
            }
            return cb.equal(root.get("workDay"), workDay);
        };
    }
    
    /**
     * Creates a specification to find schedules for a specific date.
     *
     * @param date The date to check
     * @return Specification for date search
     */
    public static Specification<DoctorSchedule> isActiveOnDate(LocalDate date) {
        return (root, query, cb) -> {
            if (date == null) {
                return cb.conjunction();
            }
            
            if (query != null) {
                query.distinct(true);
            }
            
            return cb.and(
                cb.equal(root.join("scheduleDates").get("date"), date),
                cb.equal(root.join("scheduleDates").get("status"), "ACTIVE")
            );
        };
    }
    
    /**
     * Creates a specification to find schedules that overlap with a time range.
     *
     * @param startTime The start time
     * @param endTime The end time
     * @return Specification for time overlap search
     */
    public static Specification<DoctorSchedule> overlapsWithTimeRange(LocalTime startTime, LocalTime endTime) {
        return (root, query, cb) -> {
            if (startTime == null || endTime == null) {
                return cb.conjunction();
            }
            return cb.and(
                cb.lessThanOrEqualTo(root.get("startTime"), endTime),
                cb.greaterThanOrEqualTo(root.get("endTime"), startTime)
            );
        };
    }
    
    /**
     * Creates a specification to find schedules with a specific maximum appointments.
     *
     * @param maxAppointments The maximum number of appointments
     * @return Specification for maximum appointments search
     */
    public static Specification<DoctorSchedule> hasMaxAppointments(Integer maxAppointments) {
        return (root, query, cb) -> {
            if (maxAppointments == null) {
                return cb.conjunction();
            }
            return cb.equal(root.get("maxAppointments"), maxAppointments);
        };
    }
    
    /**
     * Creates a specification to find schedules with available slots on a date.
     *
     * @param date The date to check
     * @return Specification for available slots search
     */
    public static Specification<DoctorSchedule> hasAvailableSlotsOnDate(LocalDate date) {
        return (root, query, cb) -> {
            if (date == null) {
                return cb.conjunction();
            }
            
            if (query != null) {
                query.distinct(true);
                
                // Create and use subquery
                Subquery<Long> subquery = query.subquery(Long.class);
                Root<DoctorSchedule> subRoot = subquery.from(DoctorSchedule.class);
                
                subquery.select(cb.count(subRoot.join("appointments")))
                        .where(
                            cb.equal(subRoot, root),
                            cb.equal(cb.function("DATE", LocalDate.class, subRoot.join("appointments").get("createdAt")), date)
                        );
                
                return cb.and(
                    isActiveOnDate(date).toPredicate(root, query, cb),
                    cb.lessThan(subquery, root.get("maxAppointments"))
                );
            } else {
                // Fallback if query is null (should not happen in normal execution)
                return isActiveOnDate(date).toPredicate(root, null, cb);
            }
        };
    }
    
    /**
     * Creates a specification to find schedules for doctors with a specific specialty.
     *
     * @param specialtyId The specialty ID
     * @return Specification for specialty search
     */
    public static Specification<DoctorSchedule> forDoctorWithSpecialty(Long specialtyId) {
        return (root, query, cb) -> {
            if (specialtyId == null) {
                return cb.conjunction();
            }
            
            if (query != null) {
                query.distinct(true);
            }
            
            return cb.equal(
                root.join("doctor").join("specialties").join("specialty").get("id"),
                specialtyId
            );
        };
    }
    
    /**
     * Creates a specification to find schedules with a break.
     *
     * @param hasBreak Whether to find schedules with breaks (true) or without breaks (false)
     * @return Specification for break search
     */
    public static Specification<DoctorSchedule> hasBreak(Boolean hasBreak) {
        return (root, query, cb) -> {
            if (hasBreak == null) {
                return cb.conjunction();
            }
            
            if (hasBreak) {
                return cb.and(
                    cb.isNotNull(root.get("breakStart")),
                    cb.isNotNull(root.get("breakEnd"))
                );
            } else {
                return cb.or(
                    cb.isNull(root.get("breakStart")),
                    cb.isNull(root.get("breakEnd"))
                );
            }
        };
    }
    
    /**
     * Creates a specification to find schedules with a minimum duration.
     *
     * @param minMinutes The minimum duration in minutes
     * @return Specification for duration search
     */
    public static Specification<DoctorSchedule> hasMinimumDuration(Integer minMinutes) {
        return (root, query, cb) -> {
            if (minMinutes == null || minMinutes <= 0) {
                return cb.conjunction();
            }
            
            // Calculate duration by subtracting end time from start time (converted to minutes)
            var durationExpr = cb.diff(
                cb.function("TIME_TO_MINUTES", Integer.class, root.get("endTime")),
                cb.function("TIME_TO_MINUTES", Integer.class, root.get("startTime"))
            );
            
            return cb.greaterThanOrEqualTo(durationExpr, minMinutes);
        };
    }
    
    /**
     * Creates a specification to find schedules with specific work days.
     *
     * @param workDays Array of work days to include
     * @return Specification for multiple work days search
     */
    public static Specification<DoctorSchedule> hasAnyWorkDay(WorkDay[] workDays) {
        return (root, query, cb) -> {
            if (workDays == null || workDays.length == 0) {
                return cb.conjunction();
            }
            return root.get("workDay").in((Object[]) workDays);
        };
    }
}