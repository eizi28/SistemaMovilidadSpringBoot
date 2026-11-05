package com.movilidad.service;

import com.movilidad.model.Motor;
import com.movilidad.repository.MotorRepository;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Locale;

@Service
public class MotorService {

    private final MotorRepository repositorio;

    // CONSTRUCTOR
    public MotorService(MotorRepository repositorio) {
        this.repositorio = repositorio;
    }

    // 1. LISTAR TODOS LOS MOTORES
    public List<Motor> listarMotores() {
        return repositorio.findAll();
    }

    // 2. BUSCAR MOTOR POR ID
    public Motor buscarPorId(Long id) {
        return repositorio.findById(id)
                .orElseThrow(() ->
                    new IllegalArgumentException(
                        "No se encontro el motor solicitado."
                    )
                );
    }

    // 3. BUSCAR MOTOR POR NUMERO DE SERIE
    public Motor buscarPorNumeroSerie(String numeroSerie) {

        if (numeroSerie == null || numeroSerie.isBlank()) {
            throw new IllegalArgumentException(
                "Debes ingresar un numero de serie."
            );
        }

        return repositorio.findByNumeroSerieIgnoreCase(
            numeroSerie.trim()
        ).orElse(null);
    }

    // 4. REGISTRAR MOTOR
    public Motor registrarMotor(Motor motor) {

        validarDatos(motor);

        String serie = normalizarSerie(
            motor.getNumeroSerie()
        );

        if (repositorio.existsByNumeroSerieIgnoreCase(serie)) {
            throw new IllegalArgumentException(
                "Ya existe un motor con ese numero de serie."
            );
        }

        motor.setId(null);
        motor.setNumeroSerie(serie);
        motor.setMarca(motor.getMarca().trim());
        motor.setTipo(motor.getTipo().trim());

        return repositorio.save(motor);
    }

    // 5. ACTUALIZAR MOTOR
    public Motor actualizarMotor(Long id, Motor datos) {

        Motor existente = buscarPorId(id);

        validarDatos(datos);

        String serieNueva = normalizarSerie(
            datos.getNumeroSerie()
        );

        Motor motorConSerie = repositorio
            .findByNumeroSerieIgnoreCase(serieNueva)
            .orElse(null);

        if (motorConSerie != null
                && !motorConSerie.getId().equals(id)) {
            throw new IllegalArgumentException(
                "El numero de serie pertenece a otro motor."
            );
        }

        existente.setNumeroSerie(serieNueva);
        existente.setMarca(datos.getMarca().trim());
        existente.setTipo(datos.getTipo().trim());
        existente.setPotencia(datos.getPotencia());
        existente.setDisponible(datos.isDisponible());

        return repositorio.save(existente);
    }

    // 6. ELIMINAR MOTOR
    public void eliminarMotor(Long id) {
        Motor motor = buscarPorId(id);
        repositorio.delete(motor);
    }

    // 7. LISTAR MOTORES DISPONIBLES
    public List<Motor> listarDisponibles() {
        return repositorio.findByDisponibleTrue();
    }

    // 8. CONTAR TODOS LOS MOTORES
    public long contarMotores() {
        return repositorio.count();
    }

    // 9. CONTAR MOTORES DISPONIBLES
    public long contarDisponibles() {
        return repositorio.countByDisponibleTrue();
    }

    // 10. CAMBIAR DISPONIBILIDAD
    public Motor cambiarDisponibilidad(
            Long id, boolean disponible) {

        Motor motor = buscarPorId(id);
        motor.setDisponible(disponible);

        return repositorio.save(motor);
    }

    // 11. BUSCAR POR MARCA
    public List<Motor> buscarPorMarca(String marca) {

        if (marca == null || marca.isBlank()) {
            return listarMotores();
        }

        return repositorio.findByMarcaContainingIgnoreCase(
            marca.trim()
        );
    }

    // 12. VALIDAR DATOS
    private void validarDatos(Motor motor) {

        if (motor == null) {
            throw new IllegalArgumentException(
                "Los datos del motor son obligatorios."
            );
        }

        validarCampo(
            motor.getNumeroSerie(), "numero de serie", 30
        );

        validarCampo(motor.getMarca(), "marca", 50);
        validarCampo(motor.getTipo(), "tipo", 30);

        if (motor.getPotencia() <= 0) {
            throw new IllegalArgumentException(
                "La potencia debe ser mayor que cero."
            );
        }
    }

    // 13. VALIDAR CAMPOS DE TEXTO
    private void validarCampo(
            String valor, String campo, int maximo) {

        if (valor == null || valor.isBlank()) {
            throw new IllegalArgumentException(
                "El campo " + campo + " es obligatorio."
            );
        }

        if (valor.trim().length() > maximo) {
            throw new IllegalArgumentException(
                "El campo " + campo +
                " supera los " + maximo + " caracteres."
            );
        }
    }

    // 14. NORMALIZAR NUMERO DE SERIE
    private String normalizarSerie(String serie) {
        return serie.trim().toUpperCase(Locale.ROOT);
    }
}
