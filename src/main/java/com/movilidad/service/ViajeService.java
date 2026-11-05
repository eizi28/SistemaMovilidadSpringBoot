package com.movilidad.service;

import com.movilidad.model.*;
import com.movilidad.repository.*;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;

@Service
@Transactional
public class ViajeService {

    private final ViajeRepository viajeRepositorio;
    private final CarroRepository carroRepositorio;
    private final ChoferRepository choferRepositorio;
    private final PasajeroRepository pasajeroRepositorio;

    private static final Set<String> ESTADOS =
            Set.of(
                "PROGRAMADO",
                "EN_CURSO",
                "FINALIZADO",
                "CANCELADO"
            );

    public ViajeService(
            ViajeRepository viajeRepositorio,
            CarroRepository carroRepositorio,
            ChoferRepository choferRepositorio,
            PasajeroRepository pasajeroRepositorio) {

        this.viajeRepositorio = viajeRepositorio;
        this.carroRepositorio = carroRepositorio;
        this.choferRepositorio = choferRepositorio;
        this.pasajeroRepositorio = pasajeroRepositorio;
    }

    // LISTAR VIAJES
    public List<Viaje> listarViajes() {
        return viajeRepositorio.findAllByOrderByFechaDesc();
    }

    // BUSCAR POR ID
    public Viaje buscarPorId(Long id) {
        return viajeRepositorio.findById(id)
                .orElseThrow(() ->
                    new IllegalArgumentException(
                        "No se encontro el viaje solicitado."
                    )
                );
    }

    // LISTAR VIAJES POR ESTADO
    public List<Viaje> buscarPorEstado(String estado) {

        if (estado == null || estado.isBlank()) {
            return listarViajes();
        }

        return viajeRepositorio
                .findByEstadoOrderByFechaDesc(
                    estado.trim().toUpperCase()
                );
    }

    // REGISTRAR VIAJE
    public Viaje registrarViaje(
            Viaje viaje,
            Long carroId,
            Long choferId,
            Long pasajeroId) {

        validarDatos(viaje);

        viaje.setId(null);
        viaje.setOrigen(viaje.getOrigen().trim());
        viaje.setDestino(viaje.getDestino().trim());
        viaje.setEstado("PROGRAMADO");

        asignarParticipantes(
                viaje, carroId, choferId, pasajeroId
        );

        return viajeRepositorio.save(viaje);
    }

    // ACTUALIZAR VIAJE
    public Viaje actualizarViaje(
            Long id,
            Viaje datos,
            Long carroId,
            Long choferId,
            Long pasajeroId) {

        Viaje existente = buscarPorId(id);

        validarDatos(datos);

        existente.setOrigen(datos.getOrigen().trim());
        existente.setDestino(datos.getDestino().trim());
        existente.setFecha(datos.getFecha());

        asignarParticipantes(
                existente, carroId, choferId, pasajeroId
        );

        return viajeRepositorio.save(existente);
    }

    // ASIGNAR CARRO, CHOFER Y PASAJERO
    private void asignarParticipantes(
            Viaje viaje,
            Long carroId,
            Long choferId,
            Long pasajeroId) {

        Carro carro = carroRepositorio.findById(carroId)
                .orElseThrow(() ->
                    new IllegalArgumentException(
                        "El carro seleccionado no existe."
                    )
                );

        Chofer chofer = choferRepositorio.findById(choferId)
                .orElseThrow(() ->
                    new IllegalArgumentException(
                        "El chofer seleccionado no existe."
                    )
                );

        Pasajero pasajero = pasajeroRepositorio
                .findById(pasajeroId)
                .orElseThrow(() ->
                    new IllegalArgumentException(
                        "El pasajero seleccionado no existe."
                    )
                );

        viaje.setCarro(carro);
        viaje.setChofer(chofer);
        viaje.setPasajero(pasajero);
    }

    // CAMBIAR ESTADO DE VIAJE
    public Viaje cambiarEstado(Long id, String estado) {

        Viaje viaje = buscarPorId(id);

        if (estado == null
                || !ESTADOS.contains(estado)) {
            throw new IllegalArgumentException(
                "El estado del viaje no es valido."
            );
        }

        viaje.setEstado(estado);

        return viajeRepositorio.save(viaje);
    }

    // ELIMINAR VIAJE
    public void eliminarViaje(Long id) {
        Viaje viaje = buscarPorId(id);
        viajeRepositorio.delete(viaje);
    }

    // CONTAR TODOS LOS VIAJES
    public long contarViajes() {
        return viajeRepositorio.count();
    }

    // CONTAR VIAJES FINALIZADOS
    public long contarFinalizados() {
        return viajeRepositorio.countByEstado("FINALIZADO");
    }

    // CONTAR VIAJES PROGRAMADOS
    public long contarProgramados() {
        return viajeRepositorio.countByEstado("PROGRAMADO");
    }

    // VALIDAR DATOS
    private void validarDatos(Viaje viaje) {

        if (viaje == null) {
            throw new IllegalArgumentException(
                "Los datos del viaje son obligatorios."
            );
        }

        if (viaje.getOrigen() == null
                || viaje.getOrigen().isBlank()) {
            throw new IllegalArgumentException(
                "Debes ingresar el origen."
            );
        }

        if (viaje.getDestino() == null
                || viaje.getDestino().isBlank()) {
            throw new IllegalArgumentException(
                "Debes ingresar el destino."
            );
        }

        if (viaje.getOrigen().trim().length() > 100
                || viaje.getDestino().trim().length() > 100) {
            throw new IllegalArgumentException(
                "El origen o destino es demasiado largo."
            );
        }

        if (viaje.getOrigen().trim().equalsIgnoreCase(
                viaje.getDestino().trim())) {
            throw new IllegalArgumentException(
                "El origen y el destino deben ser diferentes."
            );
        }

        if (viaje.getFecha() == null) {
            throw new IllegalArgumentException(
                "Debes seleccionar una fecha y hora."
            );
        }
    }
}
