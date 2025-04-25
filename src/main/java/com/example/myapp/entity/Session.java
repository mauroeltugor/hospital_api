package com.example.myapp.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;
import java.util.Date;

@Entity
@Table(name = "sessions")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Session {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    
    @Column(name = "session_date")
    @Temporal(TemporalType.DATE)
    private Date sessionDate;
    
    @Column(name = "effectiveness")
    private Integer effectiveness;
    
    @Column(name = "observations", columnDefinition = "TEXT")
    private String observations;
}
