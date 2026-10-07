package com.pulse.gym.config;
import com.nimbusds.jose.jwk.source.ImmutableSecret;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.*;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.security.oauth2.server.resource.authentication.*;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.*;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.security.SecureRandom;
import java.time.*;
import java.util.*;
@Configuration
public class SecurityConfig {
    @Bean Clock clock(@Value("${app.zone}") String zone) { return Clock.system(ZoneId.of(zone)); }
    @Bean PasswordEncoder passwordEncoder() { return new BCryptPasswordEncoder(); }
    @Bean SecretKey jwtKey(@Value("${app.jwt-secret}") String configured) {
        byte[] bytes;
        if (configured.isBlank()) { bytes=new byte[32]; new SecureRandom().nextBytes(bytes); }
        else { bytes=Base64.getDecoder().decode(configured); if(bytes.length<32) throw new IllegalArgumentException("JWT_SECRET debe tener al menos 32 bytes en Base64"); }
        return new SecretKeySpec(bytes,"HmacSHA256");
    }
    @Bean JwtEncoder jwtEncoder(SecretKey key) { return new NimbusJwtEncoder(new ImmutableSecret<>(key)); }
    @Bean JwtDecoder jwtDecoder(SecretKey key, @Value("${app.jwt-issuer}") String issuer) {
        NimbusJwtDecoder decoder=NimbusJwtDecoder.withSecretKey(key).macAlgorithm(MacAlgorithm.HS256).build();
        decoder.setJwtValidator(JwtValidators.createDefaultWithIssuer(issuer)); return decoder;
    }
    @Bean SecurityFilterChain filterChain(HttpSecurity http, @Value("${app.allowed-origins}") String origins) throws Exception {
        JwtGrantedAuthoritiesConverter authorities=new JwtGrantedAuthoritiesConverter();
        authorities.setAuthoritiesClaimName("role"); authorities.setAuthorityPrefix("ROLE_");
        JwtAuthenticationConverter converter=new JwtAuthenticationConverter(); converter.setJwtGrantedAuthoritiesConverter(authorities);
        CorsConfiguration cors=new CorsConfiguration(); cors.setAllowedOrigins(Arrays.asList(origins.split(",")));
        cors.setAllowedMethods(List.of("GET","POST","PUT","OPTIONS")); cors.setAllowedHeaders(List.of("Authorization","Content-Type"));
        UrlBasedCorsConfigurationSource source=new UrlBasedCorsConfigurationSource(); source.registerCorsConfiguration("/api/**",cors);
        return http.cors(c->c.configurationSource(source)).csrf(c->c.disable())
            .sessionManagement(s->s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(a->a
                .requestMatchers(HttpMethod.GET,"/api/health").permitAll()
                .requestMatchers(HttpMethod.POST,"/api/auth/login","/api/auth/register").permitAll()
                .requestMatchers("/api/admin/**","/api/dashboard","/api/reports/**").hasRole("ADMINISTRADOR")
                .requestMatchers("/api/members/**","/api/check-ins/**","/api/payments/**").hasAnyRole("ADMINISTRADOR","RECEPCIONISTA")
                .requestMatchers("/api/me/**","/api/auth/me","/api/plans").authenticated()
                .anyRequest().denyAll())
            .oauth2ResourceServer(o->o.jwt(j->j.jwtAuthenticationConverter(converter))
                .authenticationEntryPoint((req,res,e)->{res.setStatus(401);res.setContentType("application/json;charset=UTF-8");res.getWriter().write("{\"message\":\"Sesión inválida o vencida. Inicia sesión nuevamente.\"}");}))
            .exceptionHandling(e->e.accessDeniedHandler((req,res,err)->{res.setStatus(403);res.setContentType("application/json;charset=UTF-8");res.getWriter().write("{\"message\":\"No tienes permisos para esta operación.\"}");}))
            .build();
    }
}
