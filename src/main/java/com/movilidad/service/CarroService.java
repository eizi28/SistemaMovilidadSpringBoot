package com.movilidad.service;

import com.movilidad.model.Carro;
import com.movilidad.repository.CarroRepository;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Locale;

@Service
public class CarroService {

    private final CarroRepository repositorio;

    // CONSTRUCTOR
    public CarroService(CarroRepository repositorio) {
        this.repositorio = repositorio;
    }

    // 1. LISTAR TODOS LOS CARROS
    public List<Carro> listarCarros() {
        return repositorio.findAll();
    }

    // 2. BUSCAR CARRO POR ID
    public Carro buscarPorId(Long id) {
        return repositorio.findById(id)
                .orElseThrow(() ->
                    new IllegalArgumentException(
                        "No se encontro el carro solicitado."
                    )
                );
    }

    // 3. BUSCAR CARRO POR PLACA
    public Carro buscarPorPlaca(String placa) {

        if (placa == null || placa.isBlank()) {
            throw new IllegalArgumentException(
                "Debes ingresar una placa."
            );
        }

        return repositorio.findByPlacaIgnoreCase(
            placa.trim()
        ).orElse(null);
    }

    // 4. REGISTRAR NUEVO CARRO
    public Carro registrarCarro(Carro carro) {

        validarDatos(carro);

        String placa = normalizarPlaca(carro.getPlaca());

        if (repositorio.existsByPlacaIgnoreCase(placa)) {
            throw new IllegalArgumentException(
                "Ya existe un carro con esa placa."
            );
        }

        carro.setId(null);
        carro.setPlaca(placa);

        return repositorio.save(carro);
    }

    // 5. ACTUALIZAR CARRO
    public Carro actualizarCarro(Long id, Carro datos) {

        Carro existente = buscarPorId(id);

        validarDatos(datos);

        String placaNueva =
                normalizarPlaca(datos.getPlaca());

        Carro carroConPlaca =
            repositorio.findByPlacaIgnoreCase(
                placaNueva
            ).orElse(null);

        if (carroConPlaca != null
                && !carroConPlaca.getId().equals(id)) {

            throw new IllegalArgumentException(
                "La placa pertenece a otro carro."
            );
        }

        existente.setPlaca(placaNueva);
        existente.setMarca(datos.getMarca().trim());
        existente.setModelo(datos.getModelo().trim());
        existente.setColor(datos.getColor().trim());
        existente.setDisponible(datos.isDisponible());

        return repositorio.save(existente);
    }

    // 6. ELIMINAR CARRO
    public void eliminarCarro(Long id) {

        Carro carro = buscarPorId(id);

        repositorio.delete(carro);
    }

    // 7. LISTAR CARROS DISPONIBLES
    public List<Carro> listarDisponibles() {
        return repositorio.findByDisponibleTrue();
    }

    // 8. CONTAR TODOS LOS CARROS
    public long contarCarros() {
        return repositorio.count();
    }

    // 9. CONTAR CARROS DISPONIBLES
    public long contarDisponibles() {
        return repositorio.countByDisponibleTrue();
    }

    // 10. CAMBIAR DISPONIBILIDAD
    public Carro cambiarDisponibilidad(
            Long id, boolean disponible) {

        Carro carro = buscarPorId(id);

        carro.setDisponible(disponible);

        return repositorio.save(carro);
    }

    // 11. VALIDAR LOS DATOS DEL CARRO
    private void validarDatos(Carro carro) {

        if (carro == null) {
            throw new IllegalArgumentException(
                "Debes ingresar los datos del carro."
            );
        }

        validarCampo(carro.getPlaca(), "placa", 15);
        validarCampo(carro.getMarca(), "marca", 60);
        validarCampo(carro.getModelo(), "modelo", 60);
        validarCampo(carro.getColor(), "color", 40);
    }

    // 12. VALIDAR CAMPOS OBLIGATORIOS
    private void validarCampo(
            String valor, String campo, int maximo) {

        if (valor == null || valor.isBlank()) {
            throw new IllegalArgumentException(
                "El campo " + campo + " es obligatorio."
            );
        }

        if (valor.trim().length() > maximo) {
            throw new IllegalArgumentException(
                "El campo " + campo
                + " supera los " + maximo
                + " caracteres permitidos."
            );
        }
    }

    // 13. NORMALIZAR LA PLACA
    private String normalizarPlaca(String placa) {
        return placa.trim().toUpperCase(Locale.ROOT);
    }
}
