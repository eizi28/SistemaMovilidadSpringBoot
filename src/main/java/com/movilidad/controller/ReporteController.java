package com.movilidad.controller;

import com.movilidad.model.*;
import com.movilidad.repository.*;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.format.DateTimeFormatter;

@RestController
public class ReporteController {

    private final CarroRepository carros;
    private final MotorRepository motores;
    private final ChoferRepository choferes;
    private final PasajeroRepository pasajeros;
    private final ViajeRepository viajes;

    public ReporteController(
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

    // DESCARGAR EL REPORTE COMPLETO EN EXCEL
    @GetMapping("/reportes/excel")
    public ResponseEntity<byte[]> exportarExcel()
            throws IOException {

        try (
            Workbook libro = new XSSFWorkbook();
            ByteArrayOutputStream salida =
                    new ByteArrayOutputStream()
        ) {

            agregarCarros(libro);
            agregarMotores(libro);
            agregarChoferes(libro);
            agregarPasajeros(libro);
            agregarViajes(libro);

            libro.write(salida);

            return ResponseEntity.ok()
                    .header(
                        HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"reporte_movilidad.xlsx\""
                    )
                    .contentType(
                        MediaType.parseMediaType(
                            "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"
                        )
                    )
                    .body(salida.toByteArray());
        }
    }

    // HOJA 1: CARROS
    private void agregarCarros(Workbook libro) {

        Sheet hoja = crearHoja(
            libro, "Carros",
            "ID", "Placa", "Marca",
            "Modelo", "Color", "Disponible"
        );

        for (Carro carro : carros.findAll()) {
            agregarFila(
                hoja,
                carro.getId(),
                carro.getPlaca(),
                carro.getMarca(),
                carro.getModelo(),
                carro.getColor(),
                carro.isDisponible() ? "Sí" : "No"
            );
        }
    }

    // HOJA 2: MOTORES
    private void agregarMotores(Workbook libro) {

        Sheet hoja = crearHoja(
            libro, "Motores",
            "ID", "Número de serie", "Marca",
            "Tipo", "Potencia (HP)", "Disponible"
        );

        for (Motor motor : motores.findAll()) {
            agregarFila(
                hoja,
                motor.getId(),
                motor.getNumeroSerie(),
                motor.getMarca(),
                motor.getTipo(),
                motor.getPotencia(),
                motor.isDisponible() ? "Sí" : "No"
            );
        }
    }

    // HOJA 3: CHOFERES
    private void agregarChoferes(Workbook libro) {

        Sheet hoja = crearHoja(
            libro, "Choferes",
            "ID", "Nombre", "Apellido",
            "Documento", "Licencia",
            "Teléfono", "Disponible"
        );

        for (Chofer chofer : choferes.findAll()) {
            agregarFila(
                hoja,
                chofer.getId(),
                chofer.getNombre(),
                chofer.getApellido(),
                chofer.getDocumento(),
                chofer.getLicencia(),
                chofer.getTelefono(),
                chofer.isDisponible() ? "Sí" : "No"
            );
        }
    }

    // HOJA 4: PASAJEROS
    private void agregarPasajeros(Workbook libro) {

        Sheet hoja = crearHoja(
            libro, "Pasajeros",
            "ID", "Nombre", "Apellido",
            "Documento", "Teléfono", "Correo"
        );

        for (Pasajero pasajero : pasajeros.findAll()) {
            agregarFila(
                hoja,
                pasajero.getId(),
                pasajero.getNombre(),
                pasajero.getApellido(),
                pasajero.getDocumento(),
                pasajero.getTelefono(),
                pasajero.getCorreo()
            );
        }
    }

    // HOJA 5: VIAJES
    private void agregarViajes(Workbook libro) {

        Sheet hoja = crearHoja(
            libro, "Viajes",
            "ID", "Origen", "Destino",
            "Fecha y hora", "Carro", "Motor",
            "Chofer", "Pasajero", "Estado"
        );

        DateTimeFormatter formato =
                DateTimeFormatter.ofPattern(
                    "dd/MM/yyyy HH:mm"
                );

        for (Viaje viaje :
                viajes.findAllByOrderByFechaDesc()) {

            String nombreChofer =
                    viaje.getChofer().getNombre() + " " +
                    viaje.getChofer().getApellido();

            String nombrePasajero =
                    viaje.getPasajero().getNombre() + " " +
                    viaje.getPasajero().getApellido();

            String numeroMotor =
                    viaje.getMotor() == null
                        ? "Sin asignar"
                        : viaje.getMotor().getNumeroSerie();

            agregarFila(
                hoja,
                viaje.getId(),
                viaje.getOrigen(),
                viaje.getDestino(),
                viaje.getFecha().format(formato),
                viaje.getCarro().getPlaca(),
                numeroMotor,
                nombreChofer,
                nombrePasajero,
                viaje.getEstado()
            );
        }
    }

    // CREAR HOJA CON ENCABEZADOS
    private Sheet crearHoja(
            Workbook libro,
            String nombre,
            String... encabezados) {

        Sheet hoja = libro.createSheet(nombre);

        Row primeraFila = hoja.createRow(0);

        CellStyle estilo = libro.createCellStyle();
        Font fuente = libro.createFont();

        fuente.setBold(true);
        estilo.setFont(fuente);

        for (int i = 0; i < encabezados.length; i++) {

            Cell celda = primeraFila.createCell(i);
            celda.setCellValue(encabezados[i]);
            celda.setCellStyle(estilo);

            hoja.setColumnWidth(i, 5500);
        }

        hoja.createFreezePane(0, 1);

        return hoja;
    }

    // AGREGAR FILAS CON DATOS
    private void agregarFila(
            Sheet hoja,
            Object... valores) {

        int numeroFila = hoja.getLastRowNum() + 1;

        Row fila = hoja.createRow(numeroFila);

        for (int i = 0; i < valores.length; i++) {

            Cell celda = fila.createCell(i);

            String texto = valores[i] == null
                    ? ""
                    : String.valueOf(valores[i]);

            celda.setCellValue(texto);
        }
    }
}
