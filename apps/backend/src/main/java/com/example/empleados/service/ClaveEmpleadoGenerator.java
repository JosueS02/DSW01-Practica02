package com.example.empleados.service;

import jakarta.persistence.EntityManager;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ClaveEmpleadoGenerator {

    private final EntityManager entityManager;

    public ClaveEmpleadoGenerator(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Transactional
    public String nextClave() {
        Number nextVal = (Number) entityManager.createNativeQuery("SELECT nextval('empleados_clave_seq')").getSingleResult();
        return format(nextVal.longValue());
    }

    public String format(long sequenceValue) {
        return "E-" + String.format("%03d", sequenceValue);
    }
}
