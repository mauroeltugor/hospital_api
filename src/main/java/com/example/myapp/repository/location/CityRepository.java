package com.example.myapp.repository.location;

import com.example.myapp.entity.City;
import com.example.myapp.repository.base.BaseRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository for managing city entities.
 * Provides queries for city-related operations.
 */
@Repository
public interface CityRepository extends BaseRepository<City, Long> {

    /**
     * Find a city by name and country ID.
     *
     * @param name The city name
     * @param countryId The country ID
     * @return The city if found
     */
    Optional<City> findByNameAndCountryId(String name, Long countryId);
    
    /**
     * Find cities by country ID.
     *
     * @param countryId The country ID
     * @return List of cities in the country
     */
    List<City> findByCountryId(Long countryId);
    
    /**
     * Find cities with name containing the specified text.
     *
     * @param searchText The text to search for
     * @return List of cities with matching names
     */
    List<City> findByNameContainingIgnoreCase(String searchText);
    
    /**
     * Find cities by country code.
     *
     * @param countryCode The country code
     * @return List of cities in the country
     */
    @Query("SELECT c FROM City c JOIN c.country co WHERE co.code = :countryCode")
    List<City> findByCountryCode(@Param("countryCode") String countryCode);
    
    /**
     * Find cities sorted by name within a country.
     *
     * @param countryId The country ID
     * @return List of cities in the country sorted by name
     */
    List<City> findByCountryIdOrderByNameAsc(Long countryId);
    
    /**
     * Find cities with addresses.
     *
     * @return List of cities that have addresses
     */
    @Query("SELECT DISTINCT c FROM City c JOIN c.addresses")
    List<City> findCitiesWithAddresses();
    
    /**
     * Count addresses by city.
     *
     * @return Address counts grouped by city
     */
    @Query("SELECT c.id as cityId, c.name as cityName, " +
           "c.country.name as countryName, COUNT(addr) as addressCount " +
           "FROM City c LEFT JOIN c.addresses addr " +
           "GROUP BY c.id, c.name, c.country.name ORDER BY addressCount DESC")
    List<Object[]> countAddressesByCity();
    
    /**
     * Find cities with users.
     * This finds cities that have users through the address relationship.
     *
     * @return List of cities with users
     */
    @Query("SELECT DISTINCT c FROM City c JOIN c.addresses addr JOIN addr.users")
    List<City> findCitiesWithUsers();
    
    /**
     * Count users by city.
     *
     * @return User counts grouped by city
     */
    @Query("SELECT c.id as cityId, c.name as cityName, " +
           "c.country.name as countryName, COUNT(DISTINCT u) as userCount " +
           "FROM City c JOIN c.addresses addr JOIN addr.users u " +
           "GROUP BY c.id, c.name, c.country.name ORDER BY userCount DESC")
    List<Object[]> countUsersByCity();
    
    /**
     * Find cities with doctors.
     *
     * @return List of cities with doctors
     */
    @Query("SELECT DISTINCT c FROM City c JOIN c.addresses addr JOIN addr.users u " +
           "WHERE TYPE(u) = Doctor")
    List<City> findCitiesWithDoctors();
    
    /**
     * Find cities with patients.
     *
     * @return List of cities with patients
     */
    @Query("SELECT DISTINCT c FROM City c JOIN c.addresses addr JOIN addr.users u " +
           "WHERE TYPE(u) = Patient")
    List<City> findCitiesWithPatients();
    
    /**
     * Count doctors by city.
     *
     * @return Doctor counts grouped by city
     */
    @Query("SELECT c.id as cityId, c.name as cityName, " +
           "c.country.name as countryName, COUNT(DISTINCT d) as doctorCount " +
           "FROM City c JOIN c.addresses addr JOIN addr.users d " +
           "WHERE TYPE(d) = Doctor " +
           "GROUP BY c.id, c.name, c.country.name ORDER BY doctorCount DESC")
    List<Object[]> countDoctorsByCity();
    
    /**
     * Count patients by city.
     *
     * @return Patient counts grouped by city
     */
    @Query("SELECT c.id as cityId, c.name as cityName, " +
           "c.country.name as countryName, COUNT(DISTINCT p) as patientCount " +
           "FROM City c JOIN c.addresses addr JOIN addr.users p " +
           "WHERE TYPE(p) = Patient " +
           "GROUP BY c.id, c.name, c.country.name ORDER BY patientCount DESC")
    List<Object[]> countPatientsByCity();
    
    /**
     * Find most active cities based on appointment count.
     *
     * @param limit The maximum number of results
     * @return City appointment statistics
     */
    @Query("SELECT c.id as cityId, c.name as cityName, " +
           "c.country.name as countryName, COUNT(a) as appointmentCount " +
           "FROM City c JOIN c.addresses addr JOIN addr.users u " +
           "JOIN Patient p ON p.id = u.id " +
           "JOIN p.appointments a " +
           "GROUP BY c.id, c.name, c.country.name ORDER BY appointmentCount DESC")
    List<Object[]> findMostActiveCitiesByAppointments(@Param("limit") Integer limit);
}