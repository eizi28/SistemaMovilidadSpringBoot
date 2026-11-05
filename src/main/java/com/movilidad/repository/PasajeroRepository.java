package com.movilidad.repository;

import com.movilidad.model.Pasajero;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PasajeroRepository
        extends JpaRepository<Pasajero, Long> {

    Optional<Pasajero> findByDocumentoIgnoreCase(
            String documento);

    boolean existsByDocumentoIgnoreCase(
            String documento);

    List<Pasajero> findByNombreContainingIgnoreCase(
            String nombre);
}
