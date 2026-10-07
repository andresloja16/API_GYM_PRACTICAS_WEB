package com.pulse.gym.controller;
import com.pulse.gym.dto.*;
import com.pulse.gym.model.*;
import com.pulse.gym.repository.*;
import com.pulse.gym.service.*;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;
import java.time.Clock;
import java.util.*;
@RestController @RequestMapping("/api")
public class ApiController {
    private final AuthService auth;private final GymService gym;private final PlanRepository plans;private final UserRepository users;private final PaymentRepository payments;private final CheckInRepository entries;private final Clock clock;private final boolean demo;
    public ApiController(AuthService auth,GymService gym,PlanRepository plans,UserRepository users,PaymentRepository payments,CheckInRepository entries,Clock clock,@Value("${app.seed-demo}") boolean demo){this.auth=auth;this.gym=gym;this.plans=plans;this.users=users;this.payments=payments;this.entries=entries;this.clock=clock;this.demo=demo;}
    @GetMapping("/health") public Map<String,Object> health(){return Map.of("status","ONLINE","application","PULSE Gym API","demo",demo,"date",gym.today(),"zone",clock.getZone().toString());}
    @PostMapping("/auth/login") public Views.Auth login(@Valid @RequestBody Requests.Login r){return auth.login(r);}
    @PostMapping("/auth/register") @ResponseStatus(org.springframework.http.HttpStatus.CREATED) public Views.Auth register(@Valid @RequestBody Requests.Register r){return auth.register(r);}
    @GetMapping("/auth/me") public Views.User me(@AuthenticationPrincipal Jwt jwt){return Views.User.of(auth.current(jwt));}
    @GetMapping("/plans") public List<Plan> plans(){return plans.findAll();}
    @GetMapping("/members") public List<Views.Member> members(){return gym.list();}
    @PostMapping("/members") @ResponseStatus(org.springframework.http.HttpStatus.CREATED) public Views.Member create(@Valid @RequestBody Requests.Member r){return gym.create(r);}
    @PutMapping("/members/{id}") public Views.Member update(@PathVariable Long id,@Valid @RequestBody Requests.Member r){return gym.update(id,r);}
    @PostMapping("/members/{id}/renew") public Payment renew(@PathVariable Long id,@Valid @RequestBody Requests.Renewal r,@AuthenticationPrincipal Jwt jwt){return gym.renew(id,r,auth.current(jwt).id);}
    @PostMapping("/check-ins") public Views.Entry checkIn(@Valid @RequestBody Requests.Entry r,@AuthenticationPrincipal Jwt jwt){return gym.checkIn(r.dni(),auth.current(jwt).id);}
    @GetMapping("/check-ins/recent") public List<CheckIn> recent(){return entries.findTop10ByOrderByEnteredAtDesc();}
    @GetMapping("/dashboard") public Views.Dashboard dashboard(){return gym.dashboard();}
    @GetMapping("/payments") public List<Payment> payments(){return payments.findAllByOrderByCreatedAtDesc();}
    @GetMapping("/me/profile") public Views.Profile profile(@AuthenticationPrincipal Jwt jwt){return gym.profile(auth.current(jwt));}
    @GetMapping("/admin/staff") public List<Views.User> staff(){return users.findAll().stream().filter(u->u.role!=Role.SOCIO).map(Views.User::of).toList();}
    @PostMapping("/admin/staff") @ResponseStatus(org.springframework.http.HttpStatus.CREATED) public Views.User staff(@Valid @RequestBody Requests.Staff r){return auth.createStaff(r);}
    @GetMapping("/admin/security") public Map<String,Object> security(@AuthenticationPrincipal Jwt jwt){return Map.of("issuer",jwt.getClaimAsString("iss"),"algorithm","HS256","expiresAt",jwt.getExpiresAt(),"issuedAt",jwt.getIssuedAt(),"subject",jwt.getSubject(),"role",jwt.getClaimAsString("role"),"permissions",Map.of("ADMINISTRADOR",List.of("dashboard","socios","membresías","check-in","reportes","personal","seguridad"),"RECEPCIONISTA",List.of("socios","membresías","check-in","pagos"),"SOCIO",List.of("mi perfil","mis pagos","mis visitas")));}
}
