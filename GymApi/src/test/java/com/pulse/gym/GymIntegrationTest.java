package com.pulse.gym;
import com.fasterxml.jackson.databind.*;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.*;
import org.springframework.transaction.annotation.Transactional;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
@SpringBootTest(properties={"spring.datasource.url=jdbc:h2:mem:gym-test;DB_CLOSE_DELAY=-1","spring.jpa.hibernate.ddl-auto=create-drop"})
@AutoConfigureMockMvc
@Transactional
class GymIntegrationTest {
    @Autowired MockMvc mvc; @Autowired ObjectMapper json;
    String token(String email) throws Exception {String body=mvc.perform(post("/api/auth/login").contentType(MediaType.APPLICATION_JSON).content(json.writeValueAsString(Map.of("email",email,"password","Password123")))).andExpect(status().isOk()).andReturn().getResponse().getContentAsString();return json.readTree(body).get("token").asText();}
    @Test void authAndRoleBoundariesAreEnforcedByApi() throws Exception {
        mvc.perform(get("/api/dashboard")).andExpect(status().isUnauthorized());
        mvc.perform(post("/api/auth/login").contentType(MediaType.APPLICATION_JSON).content("{\"email\":\"admin@correo.com\",\"password\":\"wrong\"}")).andExpect(status().isUnauthorized());
        String admin=token("admin@correo.com"),staff=token("recepcion@correo.com"),member=token("socio@correo.com");
        mvc.perform(get("/api/dashboard").header("Authorization","Bearer "+admin)).andExpect(status().isOk()).andExpect(jsonPath("$.activeMembers").value(5));
        mvc.perform(get("/api/admin/security").header("Authorization","Bearer "+admin)).andExpect(status().isOk()).andExpect(jsonPath("$.issuer").value("pulse-gym")).andExpect(jsonPath("$.algorithm").value("HS256"));
        mvc.perform(get("/api/members").header("Authorization","Bearer "+staff)).andExpect(status().isOk());
        mvc.perform(get("/api/dashboard").header("Authorization","Bearer "+staff)).andExpect(status().isForbidden());
        mvc.perform(get("/api/members").header("Authorization","Bearer "+member)).andExpect(status().isForbidden());
        mvc.perform(post("/api/check-ins").header("Authorization","Bearer "+member).contentType(MediaType.APPLICATION_JSON).content("{\"dni\":\"1023456789\"}")).andExpect(status().isForbidden());
        mvc.perform(get("/api/admin/security").header("Authorization","Bearer "+member)).andExpect(status().isForbidden());
        mvc.perform(get("/api/me/profile").header("Authorization","Bearer "+member)).andExpect(status().isOk()).andExpect(jsonPath("$.member.dni").value("1023456789")).andExpect(jsonPath("$.user.passwordHash").doesNotExist());
        mvc.perform(get("/api/dashboard").header("Authorization","Bearer "+admin.substring(0,admin.length()-5)+"xxxxx")).andExpect(status().isUnauthorized());
    }
    @Test void checkInRejectsInvalidMembershipsAndOnlyRecordsAllowedEntries() throws Exception {
        String staff=token("recepcion@correo.com");
        for(String[] pair:new String[][]{{"1023456789","true"},{"1045678901","false"},{"1067890123","false"},{"9999999999","false"}})
            mvc.perform(post("/api/check-ins").header("Authorization","Bearer "+staff).contentType(MediaType.APPLICATION_JSON).content(json.writeValueAsString(Map.of("dni",pair[0])))).andExpect(status().isOk()).andExpect(jsonPath("$.allowed").value(Boolean.parseBoolean(pair[1])));
        mvc.perform(post("/api/check-ins").header("Authorization","Bearer "+staff).contentType(MediaType.APPLICATION_JSON).content("{\"dni\":\"abc\"}")).andExpect(status().isBadRequest());
    }
    @Test void renewalUsesServerPriceAndCreatesReceipt() throws Exception {
        String staff=token("recepcion@correo.com");
        JsonNode all=json.readTree(mvc.perform(get("/api/members").header("Authorization","Bearer "+staff)).andReturn().getResponse().getContentAsString());
        long id=0;for(JsonNode m:all)if(m.get("dni").asText().equals("1090123456"))id=m.get("id").asLong();assertTrue(id>0);
        mvc.perform(post("/api/members/"+id+"/renew").header("Authorization","Bearer "+staff).contentType(MediaType.APPLICATION_JSON).content("{\"planId\":\"full\",\"method\":\"EFECTIVO\",\"amount\":1}")).andExpect(status().isOk()).andExpect(jsonPath("$.amount").value(139000)).andExpect(jsonPath("$.id").isNumber());
        mvc.perform(post("/api/check-ins").header("Authorization","Bearer "+staff).contentType(MediaType.APPLICATION_JSON).content("{\"dni\":\"1090123456\"}")).andExpect(jsonPath("$.allowed").value(true));
    }
    @Test void publicRegistrationCannotCreateStaffAndRejectsDuplicates() throws Exception {
        Map<String,String> data=Map.of("name","Nuevo Socio","email","nuevo@test.com","password","Password123","dni","9988776655","role","ADMINISTRADOR");
        JsonNode response=json.readTree(mvc.perform(post("/api/auth/register").contentType(MediaType.APPLICATION_JSON).content(json.writeValueAsString(data))).andExpect(status().isCreated()).andExpect(jsonPath("$.user.role").value("SOCIO")).andReturn().getResponse().getContentAsString());
        mvc.perform(get("/api/admin/staff").header("Authorization","Bearer "+response.get("token").asText())).andExpect(status().isForbidden());
        mvc.perform(post("/api/auth/register").contentType(MediaType.APPLICATION_JSON).content(json.writeValueAsString(data))).andExpect(status().isConflict());
    }
}
