package com.pulse.gym.dto;
import com.pulse.gym.model.*;
import java.time.*;
import java.math.BigDecimal;
import java.util.List;
public final class Views {
    private Views() {}
    public record User(Long id,String name,String email,Role role,String employeeCode,Long memberId) {
        public static User of(GymUser u) {return new User(u.id,u.name,u.email,u.role,u.employeeCode,u.memberId);}
    }
    public record Auth(String token,Instant expiresAt,User user) {}
    public record Member(Long id,String name,String dni,String email,String planId,String planName,LocalDate expiry,String status,boolean enabled) {}
    public record Bar(LocalDate date,String day,long visits) {}
    public record Dashboard(long activeMembers,BigDecimal dailyRevenue,long expiringMemberships,long visitsToday,List<Bar> attendance) {}
    public record Entry(boolean allowed,String title,String message,Member member,Instant enteredAt) {}
    public record Profile(User user,Member member,List<Payment> payments,List<CheckIn> visits) {}
}
