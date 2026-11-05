package com.movilidad.repository;

import com.movilidad.model.Carro;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CarroRepository
        extends JpaRepository<Carro, Long> {

    // 1. BUSCAR UN CARRO POR SU PLACA
    Optional<Carro> findByPlacaIgnoreCase(String placa);

    // 2. COMPROBAR SI UNA PLACA YA EXISTE
    boolean existsByPlacaIgnoreCase(String placa);

    // 3. BUSCAR CARROS DISPONIBLES
    java.util.List<Carro> findByDisponibleTrue();

    // 4. CONTAR CARROS DISPONIBLES
    long countByDisponibleTrue();
}
