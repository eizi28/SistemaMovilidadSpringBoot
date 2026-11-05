package com.movilidad.service;

import com.movilidad.model.Pasajero;
import com.movilidad.repository.PasajeroRepository;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PasajeroService {

    private final PasajeroRepository repositorio;

    public PasajeroService(PasajeroRepository repositorio) {
        this.repositorio = repositorio;
    }

    // LISTAR PASAJEROS
    public List<Pasajero> listarPasajeros() {
        return repositorio.findAll();
    }

    // BUSCAR POR ID
    public Pasajero buscarPorId(Long id) {
        return repositorio.findById(id)
                .orElseThrow(() ->
                    new IllegalArgumentException(
                        "No se encontro el pasajero."
                    )
                );
    }

    // BUSCAR POR DOCUMENTO
    public Pasajero buscarPorDocumento(String documento) {

        if (documento == null || documento.isBlank()) {
            return null;
        }

        return repositorio.findByDocumentoIgnoreCase(
                documento.trim()
        ).orElse(null);
    }

    // REGISTRAR PASAJERO
    public Pasajero registrarPasajero(Pasajero pasajero) {

        validarDatos(pasajero);

        String documento = pasajero.getDocumento().trim();

        if (repositorio.existsByDocumentoIgnoreCase(documento)) {
            throw new IllegalArgumentException(
                "Ya existe un pasajero con ese documento."
            );
        }

        pasajero.setId(null);
        prepararDatos(pasajero);

        return repositorio.save(pasajero);
    }

    // ACTUALIZAR PASAJERO
    public Pasajero actualizarPasajero(
            Long id, Pasajero datos) {

        Pasajero existente = buscarPorId(id);
        validarDatos(datos);

        String documento = datos.getDocumento().trim();

        Pasajero otro = repositorio
                .findByDocumentoIgnoreCase(documento)
                .orElse(null);

        if (otro != null && !otro.getId().equals(id)) {
            throw new IllegalArgumentException(
                "El documento pertenece a otro pasajero."
            );
        }

        existente.setNombre(datos.getNombre().trim());
        existente.setApellido(datos.getApellido().trim());
        existente.setDocumento(documento);

        existente.setTelefono(
                datos.getTelefono() == null
                    ? ""
                    : datos.getTelefono().trim()
        );

        existente.setCorreo(
                datos.getCorreo() == null
                    ? ""
                    : datos.getCorreo().trim()
        );

        return repositorio.save(existente);
    }

    // ELIMINAR PASAJERO
    public void eliminarPasajero(Long id) {
        Pasajero pasajero = buscarPorId(id);
        repositorio.delete(pasajero);
    }

    // CONTAR PASAJEROS
    public long contarPasajeros() {
        return repositorio.count();
    }

    // BUSCAR POR NOMBRE
    public List<Pasajero> buscarPorNombre(String nombre) {

        if (nombre == null || nombre.isBlank()) {
            return listarPasajeros();
        }

        return repositorio.findByNombreContainingIgnoreCase(
                nombre.trim()
        );
    }

    // VALIDAR INFORMACION
    private void validarDatos(Pasajero pasajero) {

        if (pasajero == null) {
            throw new IllegalArgumentException(
                "Los datos del pasajero son obligatorios."
            );
        }

        validarCampo(pasajero.getNombre(), "nombre", 50);
        validarCampo(pasajero.getApellido(), "apellido", 50);
        validarCampo(pasajero.getDocumento(), "documento", 20);

        if (pasajero.getTelefono() != null
                && pasajero.getTelefono().trim().length() > 20) {
            throw new IllegalArgumentException(
                "El telefono es demasiado largo."
            );
        }

        if (pasajero.getCorreo() != null
                && pasajero.getCorreo().trim().length() > 100) {
            throw new IllegalArgumentException(
                "El correo es demasiado largo."
            );
        }
    }

    private void validarCampo(
            String valor, String campo, int maximo) {

        if (valor == null || valor.isBlank()) {
            throw new IllegalArgumentException(
                "El campo " + campo + " es obligatorio."
            );
        }

        if (valor.trim().length() > maximo) {
            throw new IllegalArgumentException(
                "El campo " + campo + " es demasiado largo."
            );
        }
    }

    private void prepararDatos(Pasajero pasajero) {

        pasajero.setNombre(pasajero.getNombre().trim());
        pasajero.setApellido(pasajero.getApellido().trim());
        pasajero.setDocumento(pasajero.getDocumento().trim());

        pasajero.setTelefono(
                pasajero.getTelefono() == null
                    ? ""
                    : pasajero.getTelefono().trim()
        );

        pasajero.setCorreo(
                pasajero.getCorreo() == null
                    ? ""
                    : pasajero.getCorreo().trim()
        );
    }
}
