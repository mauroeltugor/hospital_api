package com.example.hospital_api.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "addresses")
public class Address {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    
    private String streetLine1;
    private String streetLine2;
    private String postalCode;
    
    @ManyToOne
    @JoinColumn(name = "city_id")
    private City city;
    
    @OneToOne
    @JoinColumn(name = "user_id")
    private User user;

    // Getters and setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    
    public String getStreetLine1() { return streetLine1; }
    public void setStreetLine1(String streetLine1) { this.streetLine1 = streetLine1; }
    
    public String getStreetLine2() { return streetLine2; }
    public void setStreetLine2(String streetLine2) { this.streetLine2 = streetLine2; }
    
    public String getPostalCode() { return postalCode; }
    public void setPostalCode(String postalCode) { this.postalCode = postalCode; }
    
    public City getCity() { return city; }
    public void setCity(City city) { this.city = city; }
    
    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
}
