package com.movilidad.controller;

import com.movilidad.model.Pasajero;
import com.movilidad.service.PasajeroService;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/pasajeros")
public class PasajeroController {

    private final PasajeroService servicio;

    public PasajeroController(PasajeroService servicio) {
        this.servicio = servicio;
    }

    // MOSTRAR Y BUSCAR PASAJEROS
    @GetMapping
    public String listar(
            @RequestParam(required = false) String documento,
            Model modelo) {

        List<Pasajero> pasajeros;

        if (documento != null && !documento.isBlank()) {

            Pasajero encontrado =
                    servicio.buscarPorDocumento(documento);

            pasajeros = encontrado == null
                    ? List.of()
                    : List.of(encontrado);

        } else {
            pasajeros = servicio.listarPasajeros();
        }

        modelo.addAttribute("pasajeros", pasajeros);

        modelo.addAttribute(
                "documentoBuscado",
                documento == null ? "" : documento
        );

        modelo.addAttribute(
                "total", servicio.contarPasajeros()
        );

        return "pasajeros/lista";
    }

    // MOSTRAR FORMULARIO NUEVO
    @GetMapping("/nuevo")
    public String nuevo(Model modelo) {

        modelo.addAttribute("pasajero", new Pasajero());
        modelo.addAttribute("titulo", "Registrar pasajero");

        return "pasajeros/formulario";
    }

    // MOSTRAR FORMULARIO EDITAR
    @GetMapping("/editar/{id}")
    public String editar(
            @PathVariable Long id,
            Model modelo,
            RedirectAttributes mensajes) {

        try {

            modelo.addAttribute(
                    "pasajero", servicio.buscarPorId(id)
            );

            modelo.addAttribute(
                    "titulo", "Editar pasajero"
            );

            return "pasajeros/formulario";

        } catch (IllegalArgumentException e) {

            mensajes.addFlashAttribute(
                    "error", e.getMessage()
            );

            return "redirect:/pasajeros";
        }
    }

    // GUARDAR O ACTUALIZAR
    @PostMapping("/guardar")
    public String guardar(
            @ModelAttribute Pasajero pasajero,
            Model modelo,
            RedirectAttributes mensajes) {

        boolean nuevo = pasajero.getId() == null;

        try {

            if (nuevo) {
                servicio.registrarPasajero(pasajero);
            } else {
                servicio.actualizarPasajero(
                        pasajero.getId(), pasajero
                );
            }

            mensajes.addFlashAttribute(
                    "mensaje",
                    nuevo
                        ? "Pasajero registrado correctamente."
                        : "Pasajero actualizado correctamente."
            );

            return "redirect:/pasajeros";

        } catch (IllegalArgumentException e) {

            modelo.addAttribute("pasajero", pasajero);

            modelo.addAttribute(
                    "titulo",
                    nuevo
                        ? "Registrar pasajero"
                        : "Editar pasajero"
            );

            modelo.addAttribute(
                    "error", e.getMessage()
            );

            return "pasajeros/formulario";
        }
    }

    // ELIMINAR PASAJERO
    @PostMapping("/eliminar/{id}")
    public String eliminar(
            @PathVariable Long id,
            RedirectAttributes mensajes) {

        try {

            servicio.eliminarPasajero(id);

            mensajes.addFlashAttribute(
                    "mensaje",
                    "Pasajero eliminado correctamente."
            );

        } catch (IllegalArgumentException e) {

            mensajes.addFlashAttribute(
                    "error", e.getMessage()
            );
        }

        return "redirect:/pasajeros";
    }
}
