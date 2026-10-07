package com.pulse.gym.dto;
import jakarta.validation.constraints.*;
import java.time.LocalDate;
import com.pulse.gym.model.Role;
public final class Requests {
    private Requests() {}
    public record Login(@NotBlank @Email @Size(max=120) String email, @NotBlank @Size(max=72) String password) {}
    public record Register(@NotBlank @Size(max=80) String name, @NotBlank @Email @Size(max=120) String email,
        @NotBlank @Size(min=8,max=72) String password, @NotBlank @Pattern(regexp="[0-9]{6,12}") String dni) {}
    public record Member(@NotBlank @Size(max=80) String name, @NotBlank @Pattern(regexp="[0-9]{6,12}") String dni,
        @Email @Size(max=120) String email, @Size(max=40) String planId, LocalDate expiry, @NotNull Boolean enabled) {}
    public record Renewal(@NotBlank @Size(max=40) String planId, @NotBlank @Pattern(regexp="EFECTIVO|TARJETA|TRANSFERENCIA") String method) {}
    public record Entry(@NotBlank @Pattern(regexp="[0-9]{6,12}") String dni) {}
    public record Staff(@NotBlank @Size(max=80) String name, @NotBlank @Email @Size(max=120) String email,
        @NotBlank @Size(min=8,max=72) String password, @NotNull Role role,
        @NotBlank @Pattern(regexp="EMP-[0-9]{3,8}") String employeeCode) {}
}
