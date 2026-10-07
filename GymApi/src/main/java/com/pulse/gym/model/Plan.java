package com.pulse.gym.model;
import jakarta.persistence.*;
import java.math.BigDecimal;
@Entity @Table(name="plans")
public class Plan {
    @Id public String id;
    @Column(nullable=false) public String name;
    @Column(nullable=false,precision=12,scale=2) public BigDecimal price;
    public int days;
    public String description;
    public String benefits;
}
