package com.pulse.gym.model;
import jakarta.persistence.*;
@Entity @Table(name="gym_users")
public class GymUser {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY) public Long id;
    @Column(nullable=false,length=80) public String name;
    @Column(nullable=false,unique=true,length=120) public String email;
    @Column(nullable=false) public String passwordHash;
    @Enumerated(EnumType.STRING) @Column(nullable=false) public Role role;
    @Column(unique=true) public String employeeCode;
    public Long memberId;
}
