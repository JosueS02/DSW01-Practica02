package com.example.empleados.contract;

import com.example.empleados.controller.EmpleadoController;
import com.example.empleados.dto.CreateEmpleadoRequest;
import com.example.empleados.dto.DepartamentoResumenResponse;
import com.example.empleados.dto.EmpleadoResponse;
import com.example.empleados.service.EmpleadoService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(EmpleadoController.class)
class EmpleadoCreateWithCredentialsContractTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private EmpleadoService empleadoService;

    @Test
    void shouldCreateEmployeeWithEmailAndPassword() throws Exception {
        EmpleadoResponse response = new EmpleadoResponse();
        response.setClave("E-001");
        response.setNombre("Ana");
        response.setDireccion("Calle");
        response.setTelefono("555");
        response.setEmail("ana@example.local");
        DepartamentoResumenResponse dep = new DepartamentoResumenResponse();
        dep.setId("D-001");
        dep.setNombre("General");
        response.setDepartamento(dep);

        when(empleadoService.create(any(CreateEmpleadoRequest.class))).thenReturn(response);

        mockMvc.perform(post("/api/empleados")
                .with(httpBasic("admin", "admin123"))
          .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                      \"nombre\": \"Ana\",
                      \"direccion\": \"Calle\",
                      \"telefono\": \"555\",
                      \"departamentoId\": \"D-001\",
                      \"email\": \"ana@example.local\",
                      \"password\": \"Passw0rd\"
                    }
                    """))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.email").value("ana@example.local"));
    }
}
