package com.example.myapp.entity;


import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

@Entity
@Table(name = "addresses")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Address {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    
    @Column(name = "street_line1")
    private String streetLine1;
    
    @Column(name = "street_line2")
    private String streetLine2;
    
    @Column(name = "postal_code")
    private String postalCode;
    
    @ManyToOne
    @JoinColumn(name = "city_id")
    private City city;
    
    @OneToOne(mappedBy = "address")
    @JsonBackReference
    private User user;
}