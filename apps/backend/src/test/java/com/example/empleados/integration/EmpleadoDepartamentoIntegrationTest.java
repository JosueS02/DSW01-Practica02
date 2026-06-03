package com.example.empleados.integration;

import com.example.empleados.domain.DepartamentoEntity;
import com.example.empleados.repository.DepartamentoRepository;
import com.example.empleados.repository.EmpleadoRepository;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class EmpleadoDepartamentoIntegrationTest extends BaseIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private EmpleadoRepository empleadoRepository;

    @Autowired
    private DepartamentoRepository departamentoRepository;

    private String departamentoId;

    @BeforeEach
    void setup() {
        empleadoRepository.deleteAll();
        departamentoRepository.deleteAll();

        DepartamentoEntity departamento = new DepartamentoEntity();
        departamento.setId("D-092");
        departamento.setNombre("General");
        departamento.setDescripcion("Base");
        departamentoId = departamentoRepository.save(departamento).getId();
    }

    @Test
    void shouldCreateAndUpdateEmpleadoWithDepartamento() throws Exception {
                String createPayload = """
            {
              \"nombre\": \"Ana\",
              \"direccion\": \"Calle 2\",
              \"telefono\": \"55555\",
                            \"departamentoId\": \"%s\",
                            \"email\": \"ana@example.local\",
                            \"password\": \"Passw0rd\"
            }
                        """.formatted(departamentoId);

        mockMvc.perform(post("/api/empleados")
                .with(httpBasic("admin", "admin123"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(createPayload))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.departamento.id").value(departamentoId));

                String updatePayload = """
            {
              \"nombre\": \"Ana Maria\",
              \"direccion\": \"Calle 3\",
              \"telefono\": \"77777\",
                            \"departamentoId\": \"%s\",
                            \"email\": \"ana@example.local\"
            }
                        """.formatted(departamentoId);

        mockMvc.perform(put("/api/empleados/E-001")
                .with(httpBasic("admin", "admin123"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(updatePayload))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.departamento.id").value(departamentoId));
    }
}
