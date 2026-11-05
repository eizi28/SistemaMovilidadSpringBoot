
package com.movilidad.controller;

import com.movilidad.model.Viaje;
import com.movilidad.service.ViajeService;
import com.movilidad.repository.*;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@Controller
@RequestMapping("/viajes")
public class ViajeController {

    private final ViajeService servicio;
    private final CarroRepository carros;
    private final MotorRepository motores;
    private final ChoferRepository choferes;
    private final PasajeroRepository pasajeros;

    // CONSTRUCTOR
    public ViajeController(
            ViajeService servicio,
            CarroRepository carros,
            MotorRepository motores,
            ChoferRepository choferes,
            PasajeroRepository pasajeros) {

        this.servicio = servicio;
        this.carros = carros;
        this.motores = motores;
        this.choferes = choferes;
        this.pasajeros = pasajeros;
    }

    // 1. LISTAR Y FILTRAR VIAJES
    @GetMapping
    public String listar(
            @RequestParam(required = false) String estado,
            Model modelo) {

        modelo.addAttribute(
                "viajes",
                servicio.buscarPorEstado(estado)
        );

        modelo.addAttribute(
                "total",
                servicio.contarViajes()
        );

        modelo.addAttribute(
                "programados",
                servicio.contarProgramados()
        );

        modelo.addAttribute(
                "finalizados",
                servicio.contarFinalizados()
        );

        modelo.addAttribute(
                "estadoBuscado",
                estado == null ? "" : estado
        );

        return "viajes/lista";
    }

    // 2. MOSTRAR FORMULARIO PARA REGISTRAR
    @GetMapping("/nuevo")
    public String nuevo(Model modelo) {

        Viaje viaje = new Viaje();

        viaje.setFecha(
                LocalDateTime.now()
                        .plusHours(1)
                        .truncatedTo(ChronoUnit.MINUTES)
        );

        modelo.addAttribute("viaje", viaje);
        modelo.addAttribute("titulo", "Registrar viaje");

        cargarOpciones(modelo);

        return "viajes/formulario";
    }

    // 3. MOSTRAR FORMULARIO PARA EDITAR
    @GetMapping("/editar/{id}")
    public String editar(
            @PathVariable Long id,
            Model modelo,
            RedirectAttributes mensajes) {

        try {

            Viaje viaje = servicio.buscarPorId(id);

            modelo.addAttribute("viaje", viaje);
            modelo.addAttribute("titulo", "Editar viaje");

            modelo.addAttribute(
                    "carroSeleccionado",
                    viaje.getCarro().getId()
            );

            // COMPATIBLE CON VIAJES ANTERIORES SIN MOTOR
            modelo.addAttribute(
                    "motorSeleccionado",
                    viaje.getMotor() == null
                            ? null
                            : viaje.getMotor().getId()
            );

            modelo.addAttribute(
                    "choferSeleccionado",
                    viaje.getChofer().getId()
            );

            modelo.addAttribute(
                    "pasajeroSeleccionado",
                    viaje.getPasajero().getId()
            );

            cargarOpciones(modelo);

            return "viajes/formulario";

        } catch (IllegalArgumentException e) {

            mensajes.addFlashAttribute(
                    "error",
                    e.getMessage()
            );

            return "redirect:/viajes";
        }
    }

    // 4. GUARDAR O ACTUALIZAR VIAJE
    @PostMapping("/guardar")
    public String guardar(
            @ModelAttribute Viaje viaje,
            @RequestParam Long carroId,
            @RequestParam(required = false) Long motorId,
            @RequestParam Long choferId,
            @RequestParam Long pasajeroId,
            Model modelo,
            RedirectAttributes mensajes) {

        boolean nuevo = viaje.getId() == null;

        try {

            if (nuevo) {

                if (motorId == null) {
                    // COMPATIBILIDAD CON FORMULARIO ANTERIOR
                    servicio.registrarViaje(
                            viaje,
                            carroId,
                            choferId,
                            pasajeroId
                    );
                } else {
                    servicio.registrarViaje(
                            viaje,
                            carroId,
                            motorId,
                            choferId,
                            pasajeroId
                    );
                }

            } else {

                if (motorId == null) {
                    // CONSERVAR COMPATIBILIDAD ANTERIOR
                    servicio.actualizarViaje(
                            viaje.getId(),
                            viaje,
                            carroId,
                            choferId,
                            pasajeroId
                    );
                } else {
                    servicio.actualizarViaje(
                            viaje.getId(),
                            viaje,
                            carroId,
                            motorId,
                            choferId,
                            pasajeroId
                    );
                }
            }

            mensajes.addFlashAttribute(
                    "mensaje",
                    nuevo
                            ? "Viaje registrado correctamente."
                            : "Viaje actualizado correctamente."
            );

            return "redirect:/viajes";

        } catch (IllegalArgumentException e) {

            modelo.addAttribute("viaje", viaje);

            modelo.addAttribute(
                    "titulo",
                    nuevo
                            ? "Registrar viaje"
                            : "Editar viaje"
            );

            modelo.addAttribute(
                    "error",
                    e.getMessage()
            );

            modelo.addAttribute(
                    "carroSeleccionado",
                    carroId
            );

            modelo.addAttribute(
                    "motorSeleccionado",
                    motorId
            );

            modelo.addAttribute(
                    "choferSeleccionado",
                    choferId
            );

            modelo.addAttribute(
                    "pasajeroSeleccionado",
                    pasajeroId
            );

            cargarOpciones(modelo);

            return "viajes/formulario";
        }
    }

    // 5. CAMBIAR ESTADO DE UN VIAJE
    @PostMapping("/estado/{id}")
    public String cambiarEstado(
            @PathVariable Long id,
            @RequestParam String estado,
            RedirectAttributes mensajes) {

        try {

            servicio.cambiarEstado(id, estado);

            mensajes.addFlashAttribute(
                    "mensaje",
                    "Estado del viaje actualizado."
            );

        } catch (IllegalArgumentException e) {

            mensajes.addFlashAttribute(
                    "error",
                    e.getMessage()
            );
        }

        return "redirect:/viajes";
    }

    // 6. ELIMINAR UN VIAJE
    @PostMapping("/eliminar/{id}")
    public String eliminar(
            @PathVariable Long id,
            RedirectAttributes mensajes) {

        try {

            servicio.eliminarViaje(id);

            mensajes.addFlashAttribute(
                    "mensaje",
                    "Viaje eliminado correctamente."
            );

        } catch (IllegalArgumentException e) {

            mensajes.addFlashAttribute(
                    "error",
                    e.getMessage()
            );
        }

        return "redirect:/viajes";
    }

    // 7. CARGAR CARROS, MOTORES, CHOFERES Y PASAJEROS
    private void cargarOpciones(Model modelo) {

        modelo.addAttribute(
                "carros",
                carros.findAll()
        );

        modelo.addAttribute(
                "motores",
                motores.findAll()
        );

        modelo.addAttribute(
                "choferes",
                choferes.findAll()
        );

        modelo.addAttribute(
                "pasajeros",
                pasajeros.findAll()
        );
    }
}
