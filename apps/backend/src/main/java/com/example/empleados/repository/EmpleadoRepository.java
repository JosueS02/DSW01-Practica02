package com.example.empleados.repository;

import com.example.empleados.domain.EmpleadoEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface EmpleadoRepository extends JpaRepository<EmpleadoEntity, String> {

	boolean existsByDepartamento_Id(String departamentoId);

	Optional<EmpleadoEntity> findByEmailIgnoreCase(String email);

	boolean existsByEmailIgnoreCaseAndClaveNot(String email, String clave);

	boolean existsByEmailIgnoreCase(String email);
}
