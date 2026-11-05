package com.movilidad.service;

import com.movilidad.model.Chofer;
import com.movilidad.repository.ChoferRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ChoferService {

    private final ChoferRepository repositorio;

    public ChoferService(ChoferRepository repositorio) {
        this.repositorio = repositorio;
    }

    // LISTAR CHOFERES
    public List<Chofer> listarChoferes() {
        return repositorio.findAll();
    }

    // BUSCAR POR ID
    public Chofer buscarPorId(Long id) {
        return repositorio.findById(id)
                .orElseThrow(() ->
                    new IllegalArgumentException(
                        "No se encontro el chofer."
                    )
                );
    }

    // BUSCAR POR DOCUMENTO
    public Chofer buscarPorDocumento(String documento) {
        if (documento == null || documento.isBlank()) {
            return null;
        }

        return repositorio.findByDocumento(documento.trim())
                .orElse(null);
    }

    // REGISTRAR CHOFER
    public Chofer registrarChofer(Chofer chofer) {
        validarDatos(chofer);

        String documento = chofer.getDocumento().trim();

        if (repositorio.existsByDocumento(documento)) {
            throw new IllegalArgumentException(
                "Ya existe un chofer con ese documento."
            );
        }

        chofer.setId(null);
        prepararDatos(chofer);

        return repositorio.save(chofer);
    }

    // ACTUALIZAR CHOFER
    public Chofer actualizarChofer(Long id, Chofer datos) {
        Chofer existente = buscarPorId(id);
        validarDatos(datos);

        String documento = datos.getDocumento().trim();

        Chofer otro = repositorio.findByDocumento(documento)
                .orElse(null);

        if (otro != null && !otro.getId().equals(id)) {
            throw new IllegalArgumentException(
                "El documento pertenece a otro chofer."
            );
        }

        existente.setNombre(datos.getNombre().trim());
        existente.setApellido(datos.getApellido().trim());
        existente.setDocumento(documento);
        existente.setLicencia(datos.getLicencia().trim());
        existente.setTelefono(
                datos.getTelefono() == null
                    ? ""
                    : datos.getTelefono().trim()
        );
        existente.setDisponible(datos.isDisponible());

        return repositorio.save(existente);
    }

    // ELIMINAR CHOFER
    public void eliminarChofer(Long id) {
        Chofer chofer = buscarPorId(id);
        repositorio.delete(chofer);
    }

    // CAMBIAR DISPONIBILIDAD
    public Chofer cambiarDisponibilidad(
            Long id, boolean disponible) {

        Chofer chofer = buscarPorId(id);
        chofer.setDisponible(disponible);

        return repositorio.save(chofer);
    }

    // CONTAR CHOFERES
    public long contarChoferes() {
        return repositorio.count();
    }

    // CONTAR DISPONIBLES
    public long contarDisponibles() {
        return repositorio.countByDisponibleTrue();
    }

    // LISTAR DISPONIBLES
    public List<Chofer> listarDisponibles() {
        return repositorio.findByDisponibleTrue();
    }

    // BUSCAR POR NOMBRE
    public List<Chofer> buscarPorNombre(String nombre) {
        if (nombre == null || nombre.isBlank()) {
            return listarChoferes();
        }

        return repositorio.findByNombreContainingIgnoreCase(
                nombre.trim()
        );
    }

    // VALIDAR DATOS
    private void validarDatos(Chofer chofer) {
        if (chofer == null) {
            throw new IllegalArgumentException(
                "Los datos del chofer son obligatorios."
            );
        }

        validarCampo(chofer.getNombre(), "nombre", 50);
        validarCampo(chofer.getApellido(), "apellido", 50);
        validarCampo(chofer.getDocumento(), "documento", 20);
        validarCampo(chofer.getLicencia(), "licencia", 30);

        if (chofer.getTelefono() != null
                && chofer.getTelefono().trim().length() > 20) {
            throw new IllegalArgumentException(
                "El telefono no puede superar 20 caracteres."
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

    private void prepararDatos(Chofer chofer) {
        chofer.setNombre(chofer.getNombre().trim());
        chofer.setApellido(chofer.getApellido().trim());
        chofer.setDocumento(chofer.getDocumento().trim());
        chofer.setLicencia(chofer.getLicencia().trim());
        chofer.setTelefono(
                chofer.getTelefono() == null
                    ? ""
                    : chofer.getTelefono().trim()
        );
    }
}
