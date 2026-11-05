package com.movilidad.controller;

import com.movilidad.repository.CarroRepository;
import com.movilidad.repository.MotorRepository;
import com.movilidad.repository.ChoferRepository;
import com.movilidad.repository.PasajeroRepository;
import com.movilidad.repository.ViajeRepository;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class InicioController {

    private final CarroRepository carros;
    private final MotorRepository motores;
    private final ChoferRepository choferes;
    private final PasajeroRepository pasajeros;
    private final ViajeRepository viajes;

    // CONSTRUCTOR
    public InicioController(
            CarroRepository carros,
            MotorRepository motores,
            ChoferRepository choferes,
            PasajeroRepository pasajeros,
            ViajeRepository viajes) {

        this.carros = carros;
        this.motores = motores;
        this.choferes = choferes;
        this.pasajeros = pasajeros;
        this.viajes = viajes;
    }

    // MOSTRAR MENU PRINCIPAL Y ESTADISTICAS
    @GetMapping("/")
    public String mostrarInicio(Model modelo) {

        // TOTAL DE REGISTROS
        modelo.addAttribute(
                "totalCarros", carros.count());

        modelo.addAttribute(
                "totalMotores", motores.count());

        modelo.addAttribute(
                "totalChoferes", choferes.count());

        modelo.addAttribute(
                "totalPasajeros", pasajeros.count());

        modelo.addAttribute(
                "totalViajes", viajes.count());

        // ESTADOS DE LOS VIAJES
        modelo.addAttribute(
                "viajesProgramados",
                viajes.countByEstado("PROGRAMADO"));

        modelo.addAttribute(
                "viajesEnCurso",
                viajes.countByEstado("EN_CURSO"));

        modelo.addAttribute(
                "viajesFinalizados",
                viajes.countByEstado("FINALIZADO"));

        return "inicio";
    }
}
