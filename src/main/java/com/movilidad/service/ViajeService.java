
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
    private final MotorRepository motorRepositorio;
    private final ChoferRepository choferRepositorio;
    private final PasajeroRepository pasajeroRepositorio;

    private static final Set<String> ESTADOS =
            Set.of(
                "PROGRAMADO",
                "EN_CURSO",
                "FINALIZADO",
                "CANCELADO"
            );

    // CONSTRUCTOR
    public ViajeService(
            ViajeRepository viajeRepositorio,
            CarroRepository carroRepositorio,
            MotorRepository motorRepositorio,
            ChoferRepository choferRepositorio,
            PasajeroRepository pasajeroRepositorio) {

        this.viajeRepositorio = viajeRepositorio;
        this.carroRepositorio = carroRepositorio;
        this.motorRepositorio = motorRepositorio;
        this.choferRepositorio = choferRepositorio;
        this.pasajeroRepositorio = pasajeroRepositorio;
    }

    // 1. LISTAR TODOS LOS VIAJES
    public List<Viaje> listarViajes() {
        return viajeRepositorio.findAllByOrderByFechaDesc();
    }

    // 2. BUSCAR VIAJE POR ID
    public Viaje buscarPorId(Long id) {
        return viajeRepositorio.findById(id)
                .orElseThrow(() ->
                    new IllegalArgumentException(
                        "No se encontro el viaje solicitado."
                    )
                );
    }

    // 3. BUSCAR VIAJES POR ESTADO
    public List<Viaje> buscarPorEstado(String estado) {

        if (estado == null || estado.isBlank()) {
            return listarViajes();
        }

        return viajeRepositorio
                .findByEstadoOrderByFechaDesc(
                    estado.trim().toUpperCase()
                );
    }

    // 4. REGISTRAR VIAJE CON MOTOR
    public Viaje registrarViaje(
            Viaje viaje,
            Long carroId,
            Long motorId,
            Long choferId,
            Long pasajeroId) {

        validarDatos(viaje);

        if (motorId == null) {
            throw new IllegalArgumentException(
                "Debes seleccionar un motor."
            );
        }

        viaje.setId(null);
        viaje.setOrigen(viaje.getOrigen().trim());
        viaje.setDestino(viaje.getDestino().trim());
        viaje.setEstado("PROGRAMADO");

        asignarParticipantes(
                viaje,
                carroId,
                motorId,
                choferId,
                pasajeroId
        );

        return viajeRepositorio.save(viaje);
    }

    // COMPATIBILIDAD TEMPORAL CON EL CONTROLADOR ANTERIOR
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
                viaje,
                carroId,
                null,
                choferId,
                pasajeroId
        );

        return viajeRepositorio.save(viaje);
    }

    // 5. ACTUALIZAR VIAJE CON MOTOR
    public Viaje actualizarViaje(
            Long id,
            Viaje datos,
            Long carroId,
            Long motorId,
            Long choferId,
            Long pasajeroId) {

        Viaje existente = buscarPorId(id);

        validarDatos(datos);

        if (motorId == null) {
            throw new IllegalArgumentException(
                "Debes seleccionar un motor."
            );
        }

        existente.setOrigen(datos.getOrigen().trim());
        existente.setDestino(datos.getDestino().trim());
        existente.setFecha(datos.getFecha());

        asignarParticipantes(
                existente,
                carroId,
                motorId,
                choferId,
                pasajeroId
        );

        return viajeRepositorio.save(existente);
    }

    // COMPATIBILIDAD TEMPORAL CON EL CONTROLADOR ANTERIOR
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
                existente,
                carroId,
                existente.getMotor() == null
                    ? null
                    : existente.getMotor().getId(),
                choferId,
                pasajeroId
        );

        return viajeRepositorio.save(existente);
    }

    // 6. ASIGNAR CARRO, MOTOR, CHOFER Y PASAJERO
    private void asignarParticipantes(
            Viaje viaje,
            Long carroId,
            Long motorId,
            Long choferId,
            Long pasajeroId) {

        if (carroId == null ||
                choferId == null ||
                pasajeroId == null) {
            throw new IllegalArgumentException(
                "Debes seleccionar carro, chofer y pasajero."
            );
        }

        Carro carro = carroRepositorio.findById(carroId)
                .orElseThrow(() ->
                    new IllegalArgumentException(
                        "El carro seleccionado no existe."
                    )
                );

        Motor motor = null;

        if (motorId != null) {
            motor = motorRepositorio.findById(motorId)
                    .orElseThrow(() ->
                        new IllegalArgumentException(
                            "El motor seleccionado no existe."
                        )
                    );
        }

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
        viaje.setMotor(motor);
        viaje.setChofer(chofer);
        viaje.setPasajero(pasajero);
    }

    // 7. CAMBIAR ESTADO DEL VIAJE
    public Viaje cambiarEstado(Long id, String estado) {

        Viaje viaje = buscarPorId(id);

        if (estado == null || !ESTADOS.contains(estado)) {
            throw new IllegalArgumentException(
                "El estado del viaje no es valido."
            );
        }

        viaje.setEstado(estado);

        return viajeRepositorio.save(viaje);
    }

    // 8. ELIMINAR VIAJE
    public void eliminarViaje(Long id) {
        Viaje viaje = buscarPorId(id);
        viajeRepositorio.delete(viaje);
    }

    // 9. CONTAR TODOS LOS VIAJES
    public long contarViajes() {
        return viajeRepositorio.count();
    }

    // 10. CONTAR VIAJES FINALIZADOS
    public long contarFinalizados() {
        return viajeRepositorio.countByEstado("FINALIZADO");
    }

    // 11. CONTAR VIAJES PROGRAMADOS
    public long contarProgramados() {
        return viajeRepositorio.countByEstado("PROGRAMADO");
    }

    // 12. VALIDAR DATOS DEL VIAJE
    private void validarDatos(Viaje viaje) {

        if (viaje == null) {
            throw new IllegalArgumentException(
                "Los datos del viaje son obligatorios."
            );
        }

        if (viaje.getOrigen() == null ||
                viaje.getOrigen().isBlank()) {
            throw new IllegalArgumentException(
                "Debes ingresar el origen."
            );
        }

        if (viaje.getDestino() == null ||
                viaje.getDestino().isBlank()) {
            throw new IllegalArgumentException(
                "Debes ingresar el destino."
            );
        }

        if (viaje.getOrigen().trim().length() > 100 ||
                viaje.getDestino().trim().length() > 100) {
            throw new IllegalArgumentException(
                "El origen o destino es demasiado largo."
            );
        }

        if (viaje.getOrigen().trim().equalsIgnoreCase(
                viaje.getDestino().trim())) {
            throw new IllegalArgumentException(
                "El origen y destino deben ser diferentes."
            );
        }

        if (viaje.getFecha() == null) {
            throw new IllegalArgumentException(
                "Debes seleccionar una fecha y hora."
            );
        }
    }
}
