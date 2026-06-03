package com.example.empleados.service;

import com.example.empleados.domain.EmpleadoEntity;
import com.example.empleados.domain.DepartamentoEntity;
import com.example.empleados.dto.CreateEmpleadoRequest;
import com.example.empleados.dto.DepartamentoResumenResponse;
import com.example.empleados.dto.EmpleadoResponse;
import com.example.empleados.dto.UpdateEmpleadoRequest;
import com.example.empleados.repository.DepartamentoRepository;
import com.example.empleados.repository.EmpleadoRepository;
import com.example.empleados.security.PasswordHasher;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;

@Service
public class EmpleadoService {

    private final EmpleadoRepository empleadoRepository;
    private final DepartamentoRepository departamentoRepository;
    private final ClaveEmpleadoGenerator claveEmpleadoGenerator;
    private final PasswordHasher passwordHasher;

    public EmpleadoService(
        EmpleadoRepository empleadoRepository,
        DepartamentoRepository departamentoRepository,
        ClaveEmpleadoGenerator claveEmpleadoGenerator,
        PasswordHasher passwordHasher
    ) {
        this.empleadoRepository = empleadoRepository;
        this.departamentoRepository = departamentoRepository;
        this.claveEmpleadoGenerator = claveEmpleadoGenerator;
        this.passwordHasher = passwordHasher;
    }

    @Transactional
    public EmpleadoResponse create(CreateEmpleadoRequest request) {
        EmpleadoEntity entity = new EmpleadoEntity();
        entity.setClave(claveEmpleadoGenerator.nextClave());
        entity.setNombre(request.getNombre().trim());
        entity.setDireccion(request.getDireccion().trim());
        entity.setTelefono(request.getTelefono().trim());
        entity.setEmail(normalizeEmail(request.getEmail()));
        entity.setPasswordHash(passwordHasher.hash(request.getPassword()));
        entity.setActivo(true);
        entity.setDepartamento(findDepartamento(request.getDepartamentoId()));

        if (empleadoRepository.existsByEmailIgnoreCase(entity.getEmail())) {
            throw new DataIntegrityViolationException("email duplicado");
        }

        try {
            EmpleadoEntity saved = empleadoRepository.save(entity);
            return toResponse(saved);
        } catch (DataIntegrityViolationException exception) {
            throw new DataIntegrityViolationException("No fue posible crear el empleado por conflicto de datos", exception);
        }
    }

    @Transactional(readOnly = true)
    public EmpleadoResponse getByClave(String clave) {
        EmpleadoEntity empleado = empleadoRepository.findById(clave)
            .orElseThrow(() -> new NoSuchElementException("Empleado no encontrado para clave: " + clave));
        return toResponse(empleado);
    }

    @Transactional(readOnly = true)
    public List<EmpleadoResponse> listAll() {
        return empleadoRepository.findAll().stream().map(this::toResponse).toList();
    }

    @Transactional
    public EmpleadoResponse update(String clave, UpdateEmpleadoRequest request) {
        EmpleadoEntity empleado = empleadoRepository.findById(clave)
            .orElseThrow(() -> new NoSuchElementException("Empleado no encontrado para clave: " + clave));

        empleado.setNombre(request.getNombre().trim());
        empleado.setDireccion(request.getDireccion().trim());
        empleado.setTelefono(request.getTelefono().trim());
        empleado.setEmail(normalizeEmail(request.getEmail()));
        empleado.setDepartamento(findDepartamento(request.getDepartamentoId()));

        if (empleadoRepository.existsByEmailIgnoreCaseAndClaveNot(empleado.getEmail(), empleado.getClave())) {
            throw new DataIntegrityViolationException("email duplicado");
        }

        if (request.getPassword() != null && !request.getPassword().isBlank()) {
            empleado.setPasswordHash(passwordHasher.hash(request.getPassword()));
        }

        EmpleadoEntity updated = empleadoRepository.save(empleado);
        return toResponse(updated);
    }

    @Transactional
    public void delete(String clave) {
        if (!empleadoRepository.existsById(clave)) {
            throw new NoSuchElementException("Empleado no encontrado para clave: " + clave);
        }
        empleadoRepository.deleteById(clave);
    }

    private EmpleadoResponse toResponse(EmpleadoEntity entity) {
        EmpleadoResponse response = new EmpleadoResponse();
        response.setClave(entity.getClave());
        response.setNombre(entity.getNombre());
        response.setDireccion(entity.getDireccion());
        response.setTelefono(entity.getTelefono());
        response.setEmail(entity.getEmail());
        DepartamentoResumenResponse departamento = new DepartamentoResumenResponse();
        departamento.setId(entity.getDepartamento().getId());
        departamento.setNombre(entity.getDepartamento().getNombre());
        response.setDepartamento(departamento);
        return response;
    }

    private String normalizeEmail(String email) {
        return email == null ? null : email.trim().toLowerCase();
    }

    private DepartamentoEntity findDepartamento(String departamentoId) {
        return departamentoRepository.findById(departamentoId)
            .orElseThrow(() -> new NoSuchElementException("Departamento no encontrado para id: " + departamentoId));
    }
}
