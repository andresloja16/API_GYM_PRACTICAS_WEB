package com.pulse.gym.model;
import jakarta.persistence.*;
import java.time.LocalDate;
@Entity @Table(name="members")
public class GymMember {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY) public Long id;
    @Column(nullable=false,length=80) public String name;
    @Column(nullable=false,unique=true,length=12) public String dni;
    @Column(length=120) public String email;
    public String planId;
    public LocalDate expiry;
    public boolean enabled=true;
    @Version public Long version;
}
