package com.example.empleados.integration;

import com.example.empleados.domain.DepartamentoEntity;
import com.example.empleados.domain.EmpleadoEntity;
import com.example.empleados.repository.DepartamentoRepository;
import com.example.empleados.repository.EmpleadoRepository;
import com.example.empleados.security.PasswordHasher;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class AuthSingleSessionIntegrationTest extends BaseIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private EmpleadoRepository empleadoRepository;

    @Autowired
    private DepartamentoRepository departamentoRepository;

    @Autowired
    private PasswordHasher passwordHasher;

    @BeforeEach
    void setup() {
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
    void shouldInvalidatePreviousSessionOnNewLogin() throws Exception {
        String oldCookie = login();
        String newCookie = login();

        mockMvc.perform(post("/auth/logout").header("Cookie", oldCookie))
            .andExpect(status().isUnauthorized());

        mockMvc.perform(post("/auth/logout").header("Cookie", newCookie))
            .andExpect(status().isNoContent());
    }

    private String login() throws Exception {
        return mockMvc.perform(post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                      \"email\": \"ana@example.local\",
                      \"password\": \"Passw0rd\"
                    }
                    """))
            .andExpect(status().isOk())
            .andExpect(header().string("Set-Cookie", containsString("SESSION=")))
            .andReturn()
            .getResponse()
            .getHeader("Set-Cookie");
    }
}
