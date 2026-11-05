package com.movilidad.controller;

import com.movilidad.model.Carro;
import com.movilidad.service.CarroService;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/carros")
public class CarroController {

    private final CarroService servicio;

    // CONSTRUCTOR
    public CarroController(CarroService servicio) {
        this.servicio = servicio;
    }

    // 1. MOSTRAR TODOS LOS CARROS
    @GetMapping
    public String listar(
            @RequestParam(required = false) String placa,
            Model modelo) {

        List<Carro> carros;

        if (placa != null && !placa.isBlank()) {

            Carro encontrado =
                    servicio.buscarPorPlaca(placa);

            carros = encontrado == null
                    ? List.of()
                    : List.of(encontrado);

        } else {
            carros = servicio.listarCarros();
        }

        modelo.addAttribute("carros", carros);
        modelo.addAttribute("placaBuscada",
                placa == null ? "" : placa);

        modelo.addAttribute("total",
                servicio.contarCarros());

        modelo.addAttribute("disponibles",
                servicio.contarDisponibles());

        return "carros/lista";
    }

    // 2. MOSTRAR FORMULARIO DE REGISTRO
    @GetMapping("/nuevo")
    public String nuevo(Model modelo) {

        modelo.addAttribute("carro", new Carro());
        modelo.addAttribute("titulo", "Registrar carro");

        return "carros/formulario";
    }

    // 3. MOSTRAR FORMULARIO DE EDICION
    @GetMapping("/editar/{id}")
    public String editar(
            @PathVariable Long id,
            Model modelo,
            RedirectAttributes mensajes) {

        try {
            Carro carro = servicio.buscarPorId(id);

            modelo.addAttribute("carro", carro);
            modelo.addAttribute("titulo", "Editar carro");

            return "carros/formulario";

        } catch (IllegalArgumentException e) {

            mensajes.addFlashAttribute(
                    "error", e.getMessage());

            return "redirect:/carros";
        }
    }

    // 4. GUARDAR O ACTUALIZAR UN CARRO
    @PostMapping("/guardar")
    public String guardar(
            @ModelAttribute Carro carro,
            Model modelo,
            RedirectAttributes mensajes) {

        boolean esNuevo = carro.getId() == null;

        try {
            if (esNuevo) {
                servicio.registrarCarro(carro);
            } else {
                servicio.actualizarCarro(
                        carro.getId(), carro);
            }

            mensajes.addFlashAttribute(
                    "mensaje",
                    esNuevo
                        ? "Carro registrado correctamente."
                        : "Carro actualizado correctamente.");

            return "redirect:/carros";

        } catch (IllegalArgumentException e) {

            modelo.addAttribute("carro", carro);

            modelo.addAttribute(
                    "titulo",
                    esNuevo ? "Registrar carro" : "Editar carro");

            modelo.addAttribute("error", e.getMessage());

            return "carros/formulario";
        }
    }

    // 5. ELIMINAR UN CARRO
    @PostMapping("/eliminar/{id}")
    public String eliminar(
            @PathVariable Long id,
            RedirectAttributes mensajes) {

        try {
            servicio.eliminarCarro(id);

            mensajes.addFlashAttribute(
                    "mensaje",
                    "Carro eliminado correctamente.");

        } catch (IllegalArgumentException e) {

            mensajes.addFlashAttribute(
                    "error", e.getMessage());
        }

        return "redirect:/carros";
    }

    // 6. CAMBIAR DISPONIBILIDAD
    @PostMapping("/disponibilidad/{id}")
    public String cambiarDisponibilidad(
            @PathVariable Long id,
            @RequestParam boolean disponible,
            RedirectAttributes mensajes) {

        try {
            servicio.cambiarDisponibilidad(id, disponible);

            mensajes.addFlashAttribute(
                    "mensaje",
                    "Disponibilidad actualizada correctamente.");

        } catch (IllegalArgumentException e) {

            mensajes.addFlashAttribute(
                    "error", e.getMessage());
        }

        return "redirect:/carros";
    }
}
