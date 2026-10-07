package com.pulse.gym.config;
import com.pulse.gym.model.*;
import com.pulse.gym.repository.*;
import org.slf4j.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.time.*;
@Component @ConditionalOnProperty(name="app.seed-demo",havingValue="true")
public class DataInitializer implements CommandLineRunner {
    private static final Logger log=LoggerFactory.getLogger(DataInitializer.class);
    private final UserRepository users;private final MemberRepository members;private final PlanRepository plans;private final PaymentRepository payments;private final CheckInRepository entries;private final PasswordEncoder passwords;private final Clock clock;
    public DataInitializer(UserRepository users,MemberRepository members,PlanRepository plans,PaymentRepository payments,CheckInRepository entries,PasswordEncoder passwords,Clock clock){this.users=users;this.members=members;this.plans=plans;this.payments=payments;this.entries=entries;this.passwords=passwords;this.clock=clock;}
    @Override @Transactional public void run(String... args){
        plan("basic","Plan Básico",89000,"Tu primer paso hacia una vida activa.","Zona de musculación|Horario de 6:00 a 16:00|Evaluación inicial");
        plan("full","Full Access",139000,"Sin límites. Entrena a tu propio ritmo.","Acceso a todas las zonas|Horario completo|Clases grupales");
        plan("premium","Plan Premium",199000,"Una experiencia a la altura de tus metas.","Beneficios Full Access|Entrenamiento personalizado|Asesoría nutricional");
        if(users.count()>0){log.info("[DataInitializer] Datos existentes conservados; no se borran registros.");return;}
        LocalDate now=LocalDate.now(clock);
        String[] names={"Santiago Rodríguez","Valentina Martínez","Andrés López","Camila Torres","Daniel Herrera","Laura Gómez","Mateo Ramírez","Isabella Rojas"};
        String[] ids={"1023456789","1034567890","1045678901","1056789012","1067890123","1078901234","1089012345","1090123456"};
        String[] planIds={"full","premium","basic","full","basic","premium","full","basic"};
        int[] offset={30,2,-5,40,-20,4,50,-8};GymMember first=null;
        for(int i=0;i<names.length;i++){GymMember m=members.findByDni(ids[i]).orElse(null);if(m==null){m=new GymMember();m.name=names[i];m.dni=ids[i];m.email=i==0?"socio@correo.com":"socio"+(i+1)+"@correo.com";m.planId=planIds[i];m.expiry=now.plusDays(offset[i]);m.enabled=i!=4;members.save(m);}if(i==0)first=m;}
        GymUser admin=user("Alex Castro","admin@correo.com",Role.ADMINISTRADOR,"EMP-001",null);
        user("Andrea Mora","recepcion@correo.com",Role.RECEPCIONISTA,"EMP-002",null);
        user(first.name,"socio@correo.com",Role.SOCIO,null,first.id);
        if(entries.count()==0)for(int day=6;day>=0;day--)for(int j=0;j<new int[]{9,12,8,16,11,7,14}[day];j++){CheckIn c=new CheckIn();c.memberId=first.id;c.memberName=first.name;c.operatorId=admin.id;c.enteredAt=now.minusDays(day).atStartOfDay(clock.getZone()).plusMinutes(j).toInstant();entries.save(c);}
        if(payments.count()==0){Payment p=new Payment();p.memberId=first.id;p.memberName=first.name;p.planName="Full Access";p.amount=new BigDecimal("139000");p.method="TRANSFERENCIA";p.operatorId=admin.id;p.createdAt=now.atStartOfDay(clock.getZone()).toInstant();p.expiresOn=first.expiry;payments.save(p);}
        log.info("[DataInitializer] Cuentas y operaciones de demostración listas. Solo se siembran en la primera ejecución.");
    }
    private void plan(String id,String name,int price,String description,String benefits){if(plans.existsById(id))return;Plan p=new Plan();p.id=id;p.name=name;p.price=BigDecimal.valueOf(price);p.days=30;p.description=description;p.benefits=benefits;plans.save(p);}
    private GymUser user(String name,String email,Role role,String code,Long memberId){GymUser u=new GymUser();u.name=name;u.email=email;u.role=role;u.employeeCode=code;u.memberId=memberId;u.passwordHash=passwords.encode("Password123");return users.save(u);}
}
