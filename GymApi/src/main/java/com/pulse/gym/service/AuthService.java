package com.pulse.gym.service;
import com.pulse.gym.dto.*;
import com.pulse.gym.model.*;
import com.pulse.gym.repository.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import java.time.*;
import java.util.Locale;
@Service
public class AuthService {
    private final UserRepository users; private final MemberRepository members; private final PasswordEncoder passwords;
    private final JwtEncoder encoder; private final Clock clock; private final String issuer;
    public AuthService(UserRepository users,MemberRepository members,PasswordEncoder passwords,JwtEncoder encoder,Clock clock,@Value("${app.jwt-issuer}") String issuer) {
        this.users=users;this.members=members;this.passwords=passwords;this.encoder=encoder;this.clock=clock;this.issuer=issuer;
    }
    public Views.Auth login(Requests.Login request) {
        GymUser user=users.findByEmail(request.email().trim().toLowerCase(Locale.ROOT)).orElseThrow(()->new ResponseStatusException(HttpStatus.UNAUTHORIZED,"Correo o contraseña incorrectos"));
        if(!passwords.matches(request.password(),user.passwordHash))throw new ResponseStatusException(HttpStatus.UNAUTHORIZED,"Correo o contraseña incorrectos");
        return token(user);
    }
    @Transactional public Views.Auth register(Requests.Register request) {
        String email=request.email().trim().toLowerCase(Locale.ROOT);
        if(users.existsByEmail(email)||members.existsByDni(request.dni()))throw new ResponseStatusException(HttpStatus.CONFLICT,"El correo o la cédula ya están registrados");
        GymMember member=new GymMember();member.name=request.name().trim();member.email=email;member.dni=request.dni();member.enabled=false;members.save(member);
        GymUser user=new GymUser();user.name=member.name;user.email=email;user.passwordHash=passwords.encode(request.password());user.role=Role.SOCIO;user.memberId=member.id;users.save(user);
        return token(user);
    }
    public GymUser current(Jwt jwt) { return users.findById(Long.valueOf(jwt.getSubject())).orElseThrow(()->new ResponseStatusException(HttpStatus.UNAUTHORIZED,"Usuario no disponible")); }
    private Views.Auth token(GymUser user) {
        Instant now=clock.instant(),expiry=now.plusSeconds(7200);
        JwtClaimsSet claims=JwtClaimsSet.builder().issuer(issuer).subject(user.id.toString()).issuedAt(now).expiresAt(expiry).claim("role",user.role.name()).build();
        String token=encoder.encode(JwtEncoderParameters.from(JwsHeader.with(MacAlgorithm.HS256).build(),claims)).getTokenValue();
        return new Views.Auth(token,expiry,Views.User.of(user));
    }
    @Transactional public Views.User createStaff(Requests.Staff request) {
        if(request.role()==Role.SOCIO)throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Usa el registro de socios para este rol");
        String email=request.email().trim().toLowerCase(Locale.ROOT);
        if(users.existsByEmail(email)||users.existsByEmployeeCode(request.employeeCode()))throw new ResponseStatusException(HttpStatus.CONFLICT,"Correo o código de empleado ya registrado");
        GymUser u=new GymUser();u.name=request.name().trim();u.email=email;u.passwordHash=passwords.encode(request.password());u.role=request.role();u.employeeCode=request.employeeCode();return Views.User.of(users.save(u));
    }
}
