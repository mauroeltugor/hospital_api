package com.example.hospital_api.entity;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.*;

@Entity
@Table(name = "cities")
public class City {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    
    private String name;
    
    @ManyToOne
    @JoinColumn(name = "country_id")
    private Country country;
    
    @OneToMany(mappedBy = "city", cascade = CascadeType.ALL)
    private List<Address> addresses = new ArrayList<>();

    // Getters and setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public Country getCountry() { return country; }
    public void setCountry(Country country) { this.country = country; }
    
    public List<Address> getAddresses() { return addresses; }
    public void setAddresses(List<Address> addresses) { this.addresses = addresses; }
}

