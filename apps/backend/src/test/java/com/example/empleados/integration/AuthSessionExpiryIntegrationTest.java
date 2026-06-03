package com.example.empleados.integration;

import com.example.empleados.domain.DepartamentoEntity;
import com.example.empleados.domain.EmpleadoEntity;
import com.example.empleados.domain.SesionAutenticada;
import com.example.empleados.repository.DepartamentoRepository;
import com.example.empleados.repository.EmpleadoRepository;
import com.example.empleados.repository.SesionAutenticadaRepository;
import com.example.empleados.security.PasswordHasher;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class AuthSessionExpiryIntegrationTest extends BaseIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private EmpleadoRepository empleadoRepository;

    @Autowired
    private DepartamentoRepository departamentoRepository;

    @Autowired
    private SesionAutenticadaRepository sesionAutenticadaRepository;

    @Autowired
    private PasswordHasher passwordHasher;

    @BeforeEach
    void setup() {
        sesionAutenticadaRepository.deleteAll();
        empleadoRepository.deleteAll();
        departamentoRepository.deleteAll();

        DepartamentoEntity departamento = new DepartamentoEntity();
        departamento.setId("D-001");
        departamento.setNombre("General");
        departamento.setDescripcion("Base");
        departamentoRepository.save(departamento);

        EmpleadoEntity empleado = new EmpleadoEntity();
        empleado.setClave("E-001");
        empleado.setNombre("Ana");
        empleado.setDireccion("Calle");
        empleado.setTelefono("555");
        empleado.setEmail("ana@example.local");
        empleado.setPasswordHash(passwordHasher.hash("Passw0rd"));
        empleado.setActivo(true);
        empleado.setDepartamento(departamento);
        empleadoRepository.save(empleado);
    }

    @Test
    void shouldRejectExpiredSessionLogout() throws Exception {
        String cookie = mockMvc.perform(post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                      \"email\": \"ana@example.local\",
                      \"password\": \"Passw0rd\"
                    }
                    """))
            .andExpect(status().isOk())
            .andReturn()
            .getResponse()
            .getHeader("Set-Cookie");

        String sessionId = cookie.substring(cookie.indexOf("SESSION=") + 8, cookie.indexOf(';'));
        SesionAutenticada session = sesionAutenticadaRepository.findById(sessionId).orElseThrow();
        session.setExpiresAt(Instant.now().minusSeconds(30));
        sesionAutenticadaRepository.save(session);

        mockMvc.perform(post("/auth/logout").header("Cookie", "SESSION=" + sessionId))
            .andExpect(status().isUnauthorized());
    }
}
