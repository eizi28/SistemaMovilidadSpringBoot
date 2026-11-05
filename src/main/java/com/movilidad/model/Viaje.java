
package com.movilidad.model;

import jakarta.persistence.*;
import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDateTime;

@Entity
@Table(name = "viajes")
public class Viaje {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String origen;

    @Column(nullable = false, length = 100)
    private String destino;

    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm")
    @Column(nullable = false)
    private LocalDateTime fecha;

    @Column(nullable = false, length = 20)
    private String estado = "PROGRAMADO";

    // RELACION CON CARRO
    @ManyToOne(optional = false)
    @JoinColumn(name = "carro_id", nullable = false)
    private Carro carro;

    // RELACION CON MOTOR
    // Opcional para conservar los viajes anteriores
    @ManyToOne
    @JoinColumn(name = "motor_id")
    private Motor motor;

    // RELACION CON CHOFER
    @ManyToOne(optional = false)
    @JoinColumn(name = "chofer_id", nullable = false)
    private Chofer chofer;

    // RELACION CON PASAJERO
    @ManyToOne(optional = false)
    @JoinColumn(name = "pasajero_id", nullable = false)
    private Pasajero pasajero;

    // CONSTRUCTOR VACIO
    public Viaje() {
    }

    // ID
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    // ORIGEN
    public String getOrigen() {
        return origen;
    }

    public void setOrigen(String origen) {
        this.origen = origen;
    }

    // DESTINO
    public String getDestino() {
        return destino;
    }

    public void setDestino(String destino) {
        this.destino = destino;
    }

    // FECHA
    public LocalDateTime getFecha() {
        return fecha;
    }

    public void setFecha(LocalDateTime fecha) {
        this.fecha = fecha;
    }

    // ESTADO
    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    // CARRO
    public Carro getCarro() {
        return carro;
    }

    public void setCarro(Carro carro) {
        this.carro = carro;
    }

    // MOTOR
    public Motor getMotor() {
        return motor;
    }

    public void setMotor(Motor motor) {
        this.motor = motor;
    }

    // CHOFER
    public Chofer getChofer() {
        return chofer;
    }

    public void setChofer(Chofer chofer) {
        this.chofer = chofer;
    }

    // PASAJERO
    public Pasajero getPasajero() {
        return pasajero;
    }

    public void setPasajero(Pasajero pasajero) {
        this.pasajero = pasajero;
    }
}
