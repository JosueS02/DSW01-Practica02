package com.example.empleados.controller;

import com.example.empleados.dto.CreateDepartamentoRequest;
import com.example.empleados.dto.DepartamentoResponse;
import com.example.empleados.dto.UpdateDepartamentoRequest;
import com.example.empleados.service.DepartamentoService;
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
@RequestMapping("/api/departamentos")
@Validated
public class DepartamentoController {

    private final DepartamentoService departamentoService;

    public DepartamentoController(DepartamentoService departamentoService) {
        this.departamentoService = departamentoService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public DepartamentoResponse create(@Valid @RequestBody CreateDepartamentoRequest request) {
        return departamentoService.create(request);
    }

    @GetMapping
    public List<DepartamentoResponse> listAll() {
        return departamentoService.listAll();
    }

    @GetMapping("/{id}")
    public DepartamentoResponse getById(
        @PathVariable
        @Pattern(regexp = "^D-[0-9]{3,}$", message = "id debe tener formato D-001")
        String id
    ) {
        return departamentoService.getById(id);
    }

    @PutMapping("/{id}")
    public DepartamentoResponse update(
        @PathVariable
        @Pattern(regexp = "^D-[0-9]{3,}$", message = "id debe tener formato D-001")
        String id,
        @Valid @RequestBody UpdateDepartamentoRequest request
    ) {
        return departamentoService.update(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(
        @PathVariable
        @Pattern(regexp = "^D-[0-9]{3,}$", message = "id debe tener formato D-001")
        String id
    ) {
        departamentoService.delete(id);
    }
}