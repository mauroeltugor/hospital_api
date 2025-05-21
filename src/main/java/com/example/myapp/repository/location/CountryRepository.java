package com.example.myapp.repository.location;

import com.example.myapp.entity.Country;
import com.example.myapp.repository.base.BaseRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository for managing country entities.
 * Provides queries for country-related operations.
 */
@Repository
public interface CountryRepository extends BaseRepository<Country, Long> {

    /**
     * Find a country by name.
     *
     * @param name The country name
     * @return The country if found
     */
    Optional<Country> findByName(String name);
    
    /**
     * Find a country by code (case insensitive).
     *
     * @param code The country code (ISO 2-letter code)
     * @return The country if found
     */
    Optional<Country> findByCodeIgnoreCase(String code);
    
    /**
     * Find countries with name containing the specified text.
     *
     * @param searchText The text to search for
     * @return List of countries with matching names
     */
    List<Country> findByNameContainingIgnoreCase(String searchText);
    
    /**
     * Find countries sorted by name.
     *
     * @return List of all countries sorted by name
     */
    List<Country> findAllByOrderByNameAsc();
    
    /**
     * Find countries with cities.
     *
     * @return List of countries that have cities
     */
    @Query("SELECT DISTINCT c FROM Country c JOIN c.cities")
    List<Country> findCountriesWithCities();
    
    /**
     * Count cities by country.
     *
     * @return City counts grouped by country
     */
    @Query("SELECT c.id as countryId, c.name as countryName, COUNT(city) as cityCount " +
           "FROM Country c LEFT JOIN c.cities city " +
           "GROUP BY c.id, c.name ORDER BY cityCount DESC")
    List<Object[]> countCitiesByCountry();
    
    /**
     * Find countries with users.
     * This finds countries that have users through the city and address relationships.
     *
     * @return List of countries with users
     */
    @Query("SELECT DISTINCT c FROM Country c JOIN c.cities city " +
           "JOIN city.addresses addr JOIN addr.users")
    List<Country> findCountriesWithUsers();
    
    /**
     * Count users by country.
     *
     * @return User counts grouped by country
     */
    @Query("SELECT c.id as countryId, c.name as countryName, COUNT(DISTINCT u) as userCount " +
           "FROM Country c JOIN c.cities city " +
           "JOIN city.addresses addr JOIN addr.users u " +
           "GROUP BY c.id, c.name ORDER BY userCount DESC")
    List<Object[]> countUsersByCountry();
    
    /**
     * Find countries with doctors.
     *
     * @return List of countries with doctors
     */
    @Query("SELECT DISTINCT c FROM Country c JOIN c.cities city " +
           "JOIN city.addresses addr JOIN addr.users u " +
           "WHERE TYPE(u) = Doctor")
    List<Country> findCountriesWithDoctors();
    
    /**
     * Find countries with patients.
     *
     * @return List of countries with patients
     */
    @Query("SELECT DISTINCT c FROM Country c JOIN c.cities city " +
           "JOIN city.addresses addr JOIN addr.users u " +
           "WHERE TYPE(u) = Patient")
    List<Country> findCountriesWithPatients();
    
    /**
     * Count doctors by country.
     *
     * @return Doctor counts grouped by country
     */
    @Query("SELECT c.id as countryId, c.name as countryName, COUNT(DISTINCT d) as doctorCount " +
           "FROM Country c JOIN c.cities city " +
           "JOIN city.addresses addr JOIN addr.users d " +
           "WHERE TYPE(d) = Doctor " +
           "GROUP BY c.id, c.name ORDER BY doctorCount DESC")
    List<Object[]> countDoctorsByCountry();
    
    /**
     * Count patients by country.
     *
     * @return Patient counts grouped by country
     */
    @Query("SELECT c.id as countryId, c.name as countryName, COUNT(DISTINCT p) as patientCount " +
           "FROM Country c JOIN c.cities city " +
           "JOIN city.addresses addr JOIN addr.users p " +
           "WHERE TYPE(p) = Patient " +
           "GROUP BY c.id, c.name ORDER BY patientCount DESC")
    List<Object[]> countPatientsByCountry();
}