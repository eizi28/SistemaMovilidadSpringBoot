package com.movilidad.repository;

import com.movilidad.model.Viaje;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ViajeRepository
        extends JpaRepository<Viaje, Long> {

    // LISTAR VIAJES POR FECHA
    List<Viaje> findAllByOrderByFechaDesc();

    // BUSCAR VIAJES POR ESTADO
    List<Viaje> findByEstadoOrderByFechaDesc(String estado);

    // BUSCAR VIAJES DE UN PASAJERO
    List<Viaje> findByPasajero_IdOrderByFechaDesc(
            Long pasajeroId);

    // CONTAR VIAJES POR ESTADO
    long countByEstado(String estado);
}
