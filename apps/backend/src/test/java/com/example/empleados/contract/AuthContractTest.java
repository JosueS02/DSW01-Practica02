package com.example.empleados.contract;

import com.example.empleados.controller.AuthController;
import com.example.empleados.dto.DepartamentoResumenResponse;
import com.example.empleados.dto.EmpleadoResponse;
import com.example.empleados.dto.LoginResponse;
import com.example.empleados.service.AuthService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AuthController.class)
class AuthContractTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AuthService authService;

    @Test
    void shouldLoginAndReturnSessionCookie() throws Exception {
        LoginResponse response = new LoginResponse();
        EmpleadoResponse empleado = new EmpleadoResponse();
        empleado.setClave("E-001");
        empleado.setNombre("Ana");
        empleado.setEmail("ana@example.local");
        empleado.setDepartamento(new DepartamentoResumenResponse());
        response.setEmpleado(empleado);
        response.setSessionExpiresAt(Instant.now().plusSeconds(3600));
        response.setAuthMode("ui-session");

        when(authService.login(any())).thenReturn(new AuthService.LoginResult(response, "session123"));

        mockMvc.perform(post("/auth/login")
            .with(httpBasic("admin", "admin123"))
            .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                      \"email\": \"ana@example.local\",
                      \"password\": \"Passw0rd\"
                    }
                    """))
            .andExpect(status().isOk())
            .andExpect(header().string("Set-Cookie", org.hamcrest.Matchers.containsString("SESSION=session123")))
            .andExpect(jsonPath("$.empleado.clave").value("E-001"));
    }

    @Test
    void shouldLogoutAndReturn204() throws Exception {
        doNothing().when(authService).logout(any());

        mockMvc.perform(post("/auth/logout")
            .with(httpBasic("admin", "admin123"))
            .with(csrf())
            .header("Cookie", "SESSION=session123"))
            .andExpect(status().isNoContent());
    }
}
