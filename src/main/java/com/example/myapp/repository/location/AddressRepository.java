package com.example.myapp.repository.location;

import com.example.myapp.entity.Address;
import com.example.myapp.repository.base.BaseRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository for managing address entities.
 * Provides queries for address-related operations.
 */
@Repository
public interface AddressRepository extends BaseRepository<Address, Long> {

    /**
     * Find addresses by city ID.
     *
     * @param cityId The city ID
     * @return List of addresses in the city
     */
    List<Address> findByCityId(Long cityId);
    
    /**
     * Find addresses with street containing the specified text.
     *
     * @param searchText The text to search for
     * @return List of addresses with matching street
     */
    @Query("SELECT a FROM Address a WHERE " +
           "LOWER(a.streetLine1) LIKE LOWER(CONCAT('%', :searchText, '%')) OR " +
           "LOWER(a.streetLine2) LIKE LOWER(CONCAT('%', :searchText, '%'))")
    List<Address> findByStreetContainingIgnoreCase(@Param("searchText") String searchText);
    
    /**
     * Find addresses by postal code.
     *
     * @param postalCode The postal code
     * @return List of addresses with the postal code
     */
    List<Address> findByPostalCode(String postalCode);
    
    /**
     * Find addresses by city name and country name.
     *
     * @param cityName The city name
     * @param countryName The country name
     * @return List of addresses in the city and country
     */
    @Query("SELECT a FROM Address a JOIN a.city c JOIN c.country co " +
           "WHERE LOWER(c.name) = LOWER(:cityName) AND LOWER(co.name) = LOWER(:countryName)")
    List<Address> findByCityNameAndCountryName(
            @Param("cityName") String cityName,
            @Param("countryName") String countryName);
    
    /**
     * Find addresses with users.
     *
     * @return List of addresses that have users
     */
    @Query("SELECT DISTINCT a FROM Address a JOIN a.users")
    List<Address> findAddressesWithUsers();
    
    /**
     * Count users by address.
     *
     * @return User counts grouped by address
     */
    @Query("SELECT a.id as addressId, a.streetLine1 as street, " +
           "a.city.name as cityName, COUNT(u) as userCount " +
           "FROM Address a LEFT JOIN a.users u " +
           "GROUP BY a.id, a.streetLine1, a.city.name ORDER BY userCount DESC")
    List<Object[]> countUsersByAddress();
    
    /**
     * Find addresses with doctors.
     *
     * @return List of addresses with doctors
     */
    @Query("SELECT DISTINCT a FROM Address a JOIN a.users u WHERE TYPE(u) = Doctor")
    List<Address> findAddressesWithDoctors();
    
    /**
     * Find addresses with patients.
     *
     * @return List of addresses with patients
     */
    @Query("SELECT DISTINCT a FROM Address a JOIN a.users u WHERE TYPE(u) = Patient")
    List<Address> findAddressesWithPatients();
    
    /**
     * Count doctors by address.
     *
     * @return Doctor counts grouped by address
     */
    @Query("SELECT a.id as addressId, a.streetLine1 as street, " +
           "a.city.name as cityName, COUNT(d) as doctorCount " +
           "FROM Address a JOIN a.users d " +
           "WHERE TYPE(d) = Doctor " +
           "GROUP BY a.id, a.streetLine1, a.city.name ORDER BY doctorCount DESC")
    List<Object[]> countDoctorsByAddress();
    
    /**
     * Count patients by address.
     *
     * @return Patient counts grouped by address
     */
    @Query("SELECT a.id as addressId, a.streetLine1 as street, " +
           "a.city.name as cityName, COUNT(p) as patientCount " +
           "FROM Address a JOIN a.users p " +
           "WHERE TYPE(p) = Patient " +
           "GROUP BY a.id, a.streetLine1, a.city.name ORDER BY patientCount DESC")
    List<Object[]> countPatientsByAddress();
    
    /**
     * Find addresses by multiple criteria.
     *
     * @param cityId The city ID (optional)
     * @param postalCode The postal code (optional)
     * @param streetText The street text to search for (optional)
     * @return List of addresses matching the criteria
     */
    @Query("SELECT a FROM Address a WHERE " +
           "(:cityId IS NULL OR a.city.id = :cityId) AND " +
           "(:postalCode IS NULL OR a.postalCode = :postalCode) AND " +
           "(:streetText IS NULL OR " +
           "LOWER(a.streetLine1) LIKE LOWER(CONCAT('%', :streetText, '%')) OR " +
           "LOWER(a.streetLine2) LIKE LOWER(CONCAT('%', :streetText, '%')))")
    List<Address> findByMultipleCriteria(
            @Param("cityId") Long cityId,
            @Param("postalCode") String postalCode,
            @Param("streetText") String streetText);
    
    /**
     * Find unique postal codes in a city.
     *
     * @param cityId The city ID
     * @return List of unique postal codes
     */
    @Query("SELECT DISTINCT a.postalCode FROM Address a WHERE a.city.id = :cityId AND a.postalCode IS NOT NULL")
    List<String> findUniquePostalCodesByCityId(@Param("cityId") Long cityId);
    
    /**
     * Find addresses with highest patient density.
     * This could be useful for epidemiology studies.
     *
     * @param limit The maximum number of results
     * @return Address patient density statistics
     */
    @Query("SELECT a.id as addressId, a.streetLine1 as street, " +
           "a.city.name as cityName, a.postalCode as postalCode, " +
           "COUNT(DISTINCT p) as patientCount " +
           "FROM Address a JOIN a.users p " +
           "WHERE TYPE(p) = Patient " +
           "GROUP BY a.id, a.streetLine1, a.city.name, a.postalCode " +
           "ORDER BY patientCount DESC")
    List<Object[]> findAddressesWithHighestPatientDensity(@Param("limit") Integer limit);
}