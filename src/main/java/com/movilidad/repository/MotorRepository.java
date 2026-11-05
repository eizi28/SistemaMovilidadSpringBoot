package com.movilidad.repository;

import com.movilidad.model.Motor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MotorRepository
        extends JpaRepository<Motor, Long> {

    Optional<Motor> findByNumeroSerieIgnoreCase(String numeroSerie);

    boolean existsByNumeroSerieIgnoreCase(String numeroSerie);

    List<Motor> findByDisponibleTrue();

    long countByDisponibleTrue();

    List<Motor> findByMarcaContainingIgnoreCase(String marca);
}