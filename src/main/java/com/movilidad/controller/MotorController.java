package com.movilidad.controller;

import com.movilidad.model.Motor;
import com.movilidad.service.MotorService;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/motores")
public class MotorController {

    private final MotorService servicio;

    // CONSTRUCTOR
    public MotorController(MotorService servicio) {
        this.servicio = servicio;
    }

    // 1. MOSTRAR TODOS LOS MOTORES
    @GetMapping
    public String listar(
            @RequestParam(required = false) String serie,
            Model modelo) {

        List<Motor> motores;

        if (serie != null && !serie.isBlank()) {

            Motor encontrado =
                    servicio.buscarPorNumeroSerie(serie);

            motores = encontrado == null
                    ? List.of()
                    : List.of(encontrado);

        } else {
            motores = servicio.listarMotores();
        }

        modelo.addAttribute("motores", motores);
        modelo.addAttribute("serieBuscada",
                serie == null ? "" : serie);

        modelo.addAttribute("total",
                servicio.contarMotores());

        modelo.addAttribute("disponibles",
                servicio.contarDisponibles());

        return "motores/lista";
    }

    // 2. FORMULARIO PARA REGISTRAR
    @GetMapping("/nuevo")
    public String nuevo(Model modelo) {

        modelo.addAttribute("motor", new Motor());
        modelo.addAttribute("titulo", "Registrar motor");

        return "motores/formulario";
    }

    // 3. FORMULARIO PARA EDITAR
    @GetMapping("/editar/{id}")
    public String editar(
            @PathVariable Long id,
            Model modelo,
            RedirectAttributes mensajes) {

        try {
            Motor motor = servicio.buscarPorId(id);

            modelo.addAttribute("motor", motor);
            modelo.addAttribute("titulo", "Editar motor");

            return "motores/formulario";

        } catch (IllegalArgumentException e) {

            mensajes.addFlashAttribute(
                    "error", e.getMessage());

            return "redirect:/motores";
        }
    }

    // 4. GUARDAR O ACTUALIZAR
    @PostMapping("/guardar")
    public String guardar(
            @ModelAttribute Motor motor,
            Model modelo,
            RedirectAttributes mensajes) {

        boolean esNuevo = motor.getId() == null;

        try {

            if (esNuevo) {
                servicio.registrarMotor(motor);
            } else {
                servicio.actualizarMotor(
                        motor.getId(), motor);
            }

            mensajes.addFlashAttribute(
                    "mensaje",
                    esNuevo
                        ? "Motor registrado correctamente."
                        : "Motor actualizado correctamente.");

            return "redirect:/motores";

        } catch (IllegalArgumentException e) {

            modelo.addAttribute("motor", motor);

            modelo.addAttribute(
                    "titulo",
                    esNuevo ? "Registrar motor" : "Editar motor");

            modelo.addAttribute("error", e.getMessage());

            return "motores/formulario";
        }
    }

    // 5. ELIMINAR MOTOR
    @PostMapping("/eliminar/{id}")
    public String eliminar(
            @PathVariable Long id,
            RedirectAttributes mensajes) {

        try {

            servicio.eliminarMotor(id);

            mensajes.addFlashAttribute(
                    "mensaje",
                    "Motor eliminado correctamente.");

        } catch (IllegalArgumentException e) {

            mensajes.addFlashAttribute(
                    "error", e.getMessage());
        }

        return "redirect:/motores";
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

        return "redirect:/motores";
    }
}
