package com.movilidad.repository;

import com.movilidad.model.Chofer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ChoferRepository
        extends JpaRepository<Chofer, Long> {

    // Buscar chofer por documento
    Optional<Chofer> findByDocumento(String documento);

    // Comprobar si el documento ya existe
    boolean existsByDocumento(String documento);

    // Buscar choferes por nombre
    List<Chofer> findByNombreContainingIgnoreCase(String nombre);

    // Mostrar choferes disponibles
    List<Chofer> findByDisponibleTrue();

    // Contar choferes disponibles
    long countByDisponibleTrue();
}
