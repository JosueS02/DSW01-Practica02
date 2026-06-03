package com.example.empleados.contract;

import com.example.empleados.controller.EmpleadoController;
import com.example.empleados.dto.DepartamentoResumenResponse;
import com.example.empleados.dto.EmpleadoResponse;
import com.example.empleados.dto.UpdateEmpleadoRequest;
import com.example.empleados.service.EmpleadoService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(EmpleadoController.class)
class EmpleadoUpdateWithCredentialsContractTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private EmpleadoService empleadoService;

    @Test
    void shouldUpdateEmployeeWithoutPassword() throws Exception {
        EmpleadoResponse response = new EmpleadoResponse();
        response.setClave("E-001");
        response.setNombre("Ana");
        response.setDireccion("Calle nueva");
        response.setTelefono("555");
        response.setEmail("ana@example.local");
        DepartamentoResumenResponse dep = new DepartamentoResumenResponse();
        dep.setId("D-001");
        dep.setNombre("General");
        response.setDepartamento(dep);

        when(empleadoService.update(eq("E-001"), any(UpdateEmpleadoRequest.class))).thenReturn(response);

        mockMvc.perform(put("/api/empleados/E-001")
                .with(httpBasic("admin", "admin123"))
          .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                      \"nombre\": \"Ana\",
                      \"direccion\": \"Calle nueva\",
                      \"telefono\": \"555\",
                      \"departamentoId\": \"D-001\",
                      \"email\": \"ana@example.local\"
                    }
                    """))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.email").value("ana@example.local"));
    }
}
