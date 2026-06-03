package com.example.empleados.service;

import com.example.empleados.domain.DepartamentoEntity;
import com.example.empleados.dto.CreateDepartamentoRequest;
import com.example.empleados.dto.DepartamentoResponse;
import com.example.empleados.dto.UpdateDepartamentoRequest;
import com.example.empleados.repository.DepartamentoRepository;
import com.example.empleados.repository.EmpleadoRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;

@Service
public class DepartamentoService {

    private final DepartamentoRepository departamentoRepository;
    private final EmpleadoRepository empleadoRepository;
    private final DepartamentoIdGenerator departamentoIdGenerator;

    public DepartamentoService(
        DepartamentoRepository departamentoRepository,
        EmpleadoRepository empleadoRepository,
        DepartamentoIdGenerator departamentoIdGenerator
    ) {
        this.departamentoRepository = departamentoRepository;
        this.empleadoRepository = empleadoRepository;
        this.departamentoIdGenerator = departamentoIdGenerator;
    }

    @Transactional
    public DepartamentoResponse create(CreateDepartamentoRequest request) {
        String nombre = request.getNombre().trim();
        validateNombreDuplicadoOnCreate(nombre);

        DepartamentoEntity entity = new DepartamentoEntity();
        entity.setId(departamentoIdGenerator.nextId());
        entity.setNombre(nombre);
        entity.setDescripcion(normalizeDescripcion(request.getDescripcion()));

        DepartamentoEntity saved = departamentoRepository.save(entity);
        return toResponse(saved);
    }

    @Transactional(readOnly = true)
    public List<DepartamentoResponse> listAll() {
        return departamentoRepository.findAll().stream().map(this::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public DepartamentoResponse getById(String id) {
        DepartamentoEntity entity = findById(id);
        return toResponse(entity);
    }

    @Transactional
    public DepartamentoResponse update(String id, UpdateDepartamentoRequest request) {
        DepartamentoEntity entity = findById(id);
        String nombre = request.getNombre().trim();
        validateNombreDuplicadoOnUpdate(nombre, id);

        entity.setNombre(nombre);
        entity.setDescripcion(normalizeDescripcion(request.getDescripcion()));

        DepartamentoEntity updated = departamentoRepository.save(entity);
        return toResponse(updated);
    }

    @Transactional
    public void delete(String id) {
        DepartamentoEntity entity = findById(id);
        if (empleadoRepository.existsByDepartamento_Id(id)) {
            throw new IllegalStateException("No se puede eliminar el departamento porque tiene empleados asociados");
        }
        departamentoRepository.delete(entity);
    }

    private DepartamentoEntity findById(String id) {
        return departamentoRepository.findById(id)
            .orElseThrow(() -> new NoSuchElementException("Departamento no encontrado para id: " + id));
    }

    private void validateNombreDuplicadoOnCreate(String nombre) {
        if (departamentoRepository.existsByNombreIgnoreCase(nombre)) {
            throw new DataIntegrityViolationException("Ya existe un departamento con el nombre: " + nombre);
        }
    }

    private void validateNombreDuplicadoOnUpdate(String nombre, String id) {
        if (departamentoRepository.existsByNombreIgnoreCaseAndIdNot(nombre, id)) {
            throw new DataIntegrityViolationException("Ya existe un departamento con el nombre: " + nombre);
        }
    }

    private String normalizeDescripcion(String descripcion) {
        if (descripcion == null) {
            return null;
        }
        String trimmed = descripcion.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }

    private DepartamentoResponse toResponse(DepartamentoEntity entity) {
        DepartamentoResponse response = new DepartamentoResponse();
        response.setId(entity.getId());
        response.setNombre(entity.getNombre());
        response.setDescripcion(entity.getDescripcion());
        return response;
    }
}