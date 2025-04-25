package com.example.myapp.repository;

import com.example.myapp.entity.Address;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AddressRepository extends JpaRepository<Address, Integer> {
    List<Address> findByUserId(Integer userId);
    List<Address> findByPostalCode(String postalCode);
    List<Address> findByCityId(Integer cityId);
}
