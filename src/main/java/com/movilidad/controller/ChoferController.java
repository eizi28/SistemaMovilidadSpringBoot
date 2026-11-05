package com.movilidad.controller;

import com.movilidad.model.Chofer;
import com.movilidad.service.ChoferService;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/choferes")
public class ChoferController {

    private final ChoferService servicio;

    public ChoferController(ChoferService servicio) {
        this.servicio = servicio;
    }

    // MOSTRAR CHOFERES Y BUSCAR
    @GetMapping
    public String listar(
            @RequestParam(required = false) String documento,
            Model modelo) {

        List<Chofer> choferes;

        if (documento != null && !documento.isBlank()) {
            Chofer encontrado =
                    servicio.buscarPorDocumento(documento);

            choferes = encontrado == null
                    ? List.of()
                    : List.of(encontrado);
        } else {
            choferes = servicio.listarChoferes();
        }

        modelo.addAttribute("choferes", choferes);
        modelo.addAttribute("documentoBuscado",
                documento == null ? "" : documento);
        modelo.addAttribute("total",
                servicio.contarChoferes());
        modelo.addAttribute("disponibles",
                servicio.contarDisponibles());

        return "choferes/lista";
    }

    // FORMULARIO NUEVO
    @GetMapping("/nuevo")
    public String nuevo(Model modelo) {
        modelo.addAttribute("chofer", new Chofer());
        modelo.addAttribute("titulo", "Registrar chofer");

        return "choferes/formulario";
    }

    // FORMULARIO EDITAR
    @GetMapping("/editar/{id}")
    public String editar(
            @PathVariable Long id,
            Model modelo,
            RedirectAttributes mensajes) {

        try {
            modelo.addAttribute(
                    "chofer", servicio.buscarPorId(id));

            modelo.addAttribute(
                    "titulo", "Editar chofer");

            return "choferes/formulario";

        } catch (IllegalArgumentException e) {
            mensajes.addFlashAttribute(
                    "error", e.getMessage());

            return "redirect:/choferes";
        }
    }

    // GUARDAR O ACTUALIZAR
    @PostMapping("/guardar")
    public String guardar(
            @ModelAttribute Chofer chofer,
            Model modelo,
            RedirectAttributes mensajes) {

        boolean nuevo = chofer.getId() == null;

        try {
            if (nuevo) {
                servicio.registrarChofer(chofer);
            } else {
                servicio.actualizarChofer(
                        chofer.getId(), chofer);
            }

            mensajes.addFlashAttribute(
                    "mensaje",
                    nuevo
                        ? "Chofer registrado correctamente."
                        : "Chofer actualizado correctamente.");

            return "redirect:/choferes";

        } catch (IllegalArgumentException e) {
            modelo.addAttribute("chofer", chofer);
            modelo.addAttribute(
                    "titulo",
                    nuevo ? "Registrar chofer" : "Editar chofer");

            modelo.addAttribute("error", e.getMessage());

            return "choferes/formulario";
        }
    }

    // ELIMINAR CHOFER
    @PostMapping("/eliminar/{id}")
    public String eliminar(
            @PathVariable Long id,
            RedirectAttributes mensajes) {

        try {
            servicio.eliminarChofer(id);

            mensajes.addFlashAttribute(
                    "mensaje", "Chofer eliminado correctamente.");

        } catch (IllegalArgumentException e) {
            mensajes.addFlashAttribute(
                    "error", e.getMessage());
        }

        return "redirect:/choferes";
    }

    // CAMBIAR DISPONIBILIDAD
    @PostMapping("/disponibilidad/{id}")
    public String cambiarDisponibilidad(
            @PathVariable Long id,
            @RequestParam boolean disponible,
            RedirectAttributes mensajes) {

        try {
            servicio.cambiarDisponibilidad(id, disponible);

            mensajes.addFlashAttribute(
                    "mensaje", "Disponibilidad actualizada.");

        } catch (IllegalArgumentException e) {
            mensajes.addFlashAttribute(
                    "error", e.getMessage());
        }

        return "redirect:/choferes";
    }
}
