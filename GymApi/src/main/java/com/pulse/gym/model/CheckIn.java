package com.pulse.gym.model;
import jakarta.persistence.*;
import java.time.Instant;
@Entity @Table(name="check_ins")
public class CheckIn {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY) public Long id;
    public Long memberId;
    public Long operatorId;
    public String memberName;
    public Instant enteredAt;
}
