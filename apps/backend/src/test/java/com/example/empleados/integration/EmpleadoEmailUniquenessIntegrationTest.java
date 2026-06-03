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

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class EmpleadoEmailUniquenessIntegrationTest extends BaseIntegrationTest {

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
        empleado.setNombre("Base");
        empleado.setDireccion("Calle");
        empleado.setTelefono("555");
        empleado.setEmail("user@example.local");
        empleado.setPasswordHash(passwordHasher.hash("Passw0rd"));
        empleado.setActivo(true);
        empleado.setDepartamento(departamento);
        empleadoRepository.save(empleado);
    }

    @Test
    void shouldRejectCaseInsensitiveDuplicateEmail() throws Exception {
        mockMvc.perform(post("/api/empleados")
                .with(httpBasic("admin", "admin123"))
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                      \"nombre\": \"Otro\",
                      \"direccion\": \"Calle\",
                      \"telefono\": \"111\",
                      \"departamentoId\": \"D-001\",
                      \"email\": \" USER@example.local \",
                      \"password\": \"Passw0rd\"
                    }
                    """))
            .andExpect(status().isConflict());
    }
}
