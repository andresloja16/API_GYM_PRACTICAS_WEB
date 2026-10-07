package com.pulse.gym.model;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
@Entity @Table(name="payments")
public class Payment {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY) public Long id;
    public Long memberId;
    public Long operatorId;
    public String memberName;
    public String planName;
    public String method;
    @Column(nullable=false,precision=12,scale=2) public BigDecimal amount;
    public Instant createdAt;
    public LocalDate expiresOn;
}
