package com.example.empleados.controller;

import com.example.empleados.dto.CreateEmpleadoRequest;
import com.example.empleados.dto.EmpleadoResponse;
import com.example.empleados.dto.UpdateEmpleadoRequest;
import com.example.empleados.service.EmpleadoService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Pattern;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/empleados")
@Validated
public class EmpleadoController {

    private final EmpleadoService empleadoService;

    public EmpleadoController(EmpleadoService empleadoService) {
        this.empleadoService = empleadoService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public EmpleadoResponse create(@Valid @RequestBody CreateEmpleadoRequest request) {
        return empleadoService.create(request);
    }

    @GetMapping
    public List<EmpleadoResponse> listAll() {
        return empleadoService.listAll();
    }

    @GetMapping("/{clave}")
    public EmpleadoResponse getByClave(
        @PathVariable
        @Pattern(regexp = "^E-[0-9]{3,}$", message = "La clave debe tener formato E-001")
        String clave
    ) {
        return empleadoService.getByClave(clave);
    }

    @PutMapping("/{clave}")
    public EmpleadoResponse update(
        @PathVariable
        @Pattern(regexp = "^E-[0-9]{3,}$", message = "La clave debe tener formato E-001")
        String clave,
        @Valid @RequestBody UpdateEmpleadoRequest request
    ) {
        return empleadoService.update(clave, request);
    }

    @DeleteMapping("/{clave}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(
        @PathVariable
        @Pattern(regexp = "^E-[0-9]{3,}$", message = "La clave debe tener formato E-001")
        String clave
    ) {
        empleadoService.delete(clave);
    }
}
