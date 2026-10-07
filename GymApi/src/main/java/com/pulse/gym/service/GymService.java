package com.pulse.gym.service;
import com.pulse.gym.dto.*;
import com.pulse.gym.model.*;
import com.pulse.gym.repository.*;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import java.math.BigDecimal;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.*;
@Service
public class GymService {
    private final MemberRepository members;private final PlanRepository plans;private final PaymentRepository payments;private final CheckInRepository entries;private final Clock clock;
    public GymService(MemberRepository members,PlanRepository plans,PaymentRepository payments,CheckInRepository entries,Clock clock){this.members=members;this.plans=plans;this.payments=payments;this.entries=entries;this.clock=clock;}
    public LocalDate today(){return LocalDate.now(clock);}
    public String status(GymMember m){return !m.enabled||m.expiry==null?"Inactivo":m.expiry.isBefore(today())?"Vencido":"Activo";}
    public Views.Member view(GymMember m){String name=m.planId==null?"Sin plan":plans.findById(m.planId).map(p->p.name).orElse("Sin plan");return new Views.Member(m.id,m.name,m.dni,m.email,m.planId,name,m.expiry,status(m),m.enabled);}
    public List<Views.Member> list(){return members.findAll().stream().sorted(Comparator.comparing((GymMember m)->m.id).reversed()).map(this::view).toList();}
    public GymMember find(Long id){return members.findById(id).orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND,"Socio no encontrado"));}
    private Plan plan(String id){return plans.findById(id).orElseThrow(()->new ResponseStatusException(HttpStatus.BAD_REQUEST,"Plan no disponible"));}
    @Transactional public Views.Member create(Requests.Member r){if(members.existsByDni(r.dni()))throw new ResponseStatusException(HttpStatus.CONFLICT,"Cédula ya registrada");GymMember m=new GymMember();apply(m,r);return view(members.save(m));}
    @Transactional public Views.Member update(Long id,Requests.Member r){GymMember m=find(id);members.findByDni(r.dni()).filter(other->!other.id.equals(id)).ifPresent(other->{throw new ResponseStatusException(HttpStatus.CONFLICT,"Cédula ya registrada");});apply(m,r);return view(members.save(m));}
    private void apply(GymMember m,Requests.Member r){if(r.planId()!=null&&!r.planId().isBlank())plan(r.planId());m.name=r.name().trim();m.dni=r.dni();m.email=r.email();m.planId=r.planId()==null||r.planId().isBlank()?null:r.planId();m.expiry=r.expiry();m.enabled=r.enabled();if(m.enabled&&(m.planId==null||m.expiry==null))throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Un socio habilitado requiere plan y fecha de vencimiento");}
    @Transactional public Payment renew(Long id,Requests.Renewal r,Long operator){GymMember m=members.lockById(id).orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND,"Socio no encontrado"));Plan p=plan(r.planId());LocalDate base=m.expiry!=null&&!m.expiry.isBefore(today())?m.expiry:today().minusDays(1);m.expiry=base.plusDays(p.days);m.planId=p.id;m.enabled=true;members.save(m);Payment payment=new Payment();payment.memberId=m.id;payment.memberName=m.name;payment.planName=p.name;payment.amount=p.price;payment.method=r.method();payment.operatorId=operator;payment.createdAt=clock.instant();payment.expiresOn=m.expiry;return payments.save(payment);}
    @Transactional public Views.Entry checkIn(String dni,Long operator){GymMember m=members.findByDni(dni).orElse(null);if(m==null)return new Views.Entry(false,"Socio no encontrado","Verifica la cédula o registra al socio.",null,null);String state=status(m);if(!state.equals("Activo"))return new Views.Entry(false,state.equals("Vencido")?"Membresía Vencida":"Socio Inactivo","Renueva o habilita la membresía para permitir el ingreso.",view(m),null);CheckIn c=new CheckIn();c.memberId=m.id;c.memberName=m.name;c.operatorId=operator;c.enteredAt=clock.instant();entries.save(c);return new Views.Entry(true,"Acceso Permitido","¡Buen entrenamiento, "+m.name+"!",view(m),c.enteredAt);}
    public Views.Dashboard dashboard(){LocalDate now=today();Instant start=now.atStartOfDay(clock.getZone()).toInstant();BigDecimal revenue=payments.findAll().stream().filter(p->!p.createdAt.isBefore(start)).map(p->p.amount).reduce(BigDecimal.ZERO,BigDecimal::add);List<GymMember> all=members.findAll();List<CheckIn> visits=entries.findByEnteredAtGreaterThanEqualOrderByEnteredAtDesc(now.minusDays(6).atStartOfDay(clock.getZone()).toInstant());List<Views.Bar> bars=new ArrayList<>();for(int i=6;i>=0;i--){LocalDate day=now.minusDays(i);long count=visits.stream().filter(c->c.enteredAt.atZone(clock.getZone()).toLocalDate().equals(day)).count();bars.add(new Views.Bar(day,day.format(DateTimeFormatter.ofPattern("EEE",new Locale("es","CO"))),count));}return new Views.Dashboard(all.stream().filter(m->status(m).equals("Activo")).count(),revenue,all.stream().filter(m->status(m).equals("Activo")&&!m.expiry.isAfter(now.plusDays(7))).count(),bars.get(6).visits(),bars);}
    public Views.Profile profile(GymUser u){GymMember m=u.memberId==null?null:find(u.memberId);return new Views.Profile(Views.User.of(u),m==null?null:view(m),m==null?List.of():payments.findByMemberIdOrderByCreatedAtDesc(m.id),m==null?List.of():entries.findTop20ByMemberIdOrderByEnteredAtDesc(m.id));}
}
