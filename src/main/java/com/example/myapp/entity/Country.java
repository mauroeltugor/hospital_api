package com.example.myapp.entity;


import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "countries")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Country {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    
    @Column(name = "name", unique = true)
    private String name;
    
    @Column(name = "code", unique = true)
    private String code;
    
    @OneToMany(mappedBy = "country")
    private Set<City> cities = new HashSet<>();
}