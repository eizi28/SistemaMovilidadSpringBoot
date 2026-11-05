package com.movilidad.model;

import jakarta.persistence.*;

@Entity
@Table(name = "motores")
public class Motor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 30)
    private String numeroSerie;

    @Column(nullable = false, length = 50)
    private String marca;

    @Column(nullable = false, length = 30)
    private String tipo;

    @Column(nullable = false)
    private int potencia;

    @Column(nullable = false)
    private boolean disponible = true;

    // CONSTRUCTOR VACIO PARA JPA
    public Motor() {
    }

    // CONSTRUCTOR CON DATOS
    public Motor(String numeroSerie, String marca,
                 String tipo, int potencia) {

        this.numeroSerie = numeroSerie;
        this.marca = marca;
        this.tipo = tipo;
        this.potencia = potencia;
        this.disponible = true;
    }

    // GETTERS Y SETTERS
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNumeroSerie() {
        return numeroSerie;
    }

    public void setNumeroSerie(String numeroSerie) {
        this.numeroSerie = numeroSerie;
    }

    public String getMarca() {
        return marca;
    }

    public void setMarca(String marca) {
        this.marca = marca;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public int getPotencia() {
        return potencia;
    }

    public void setPotencia(int potencia) {
        this.potencia = potencia;
    }

    public boolean isDisponible() {
        return disponible;
    }

    public void setDisponible(boolean disponible) {
        this.disponible = disponible;
    }

    @Override
    public String toString() {
        return "Motor{" +
                "id=" + id +
                ", numeroSerie='" + numeroSerie + '\'' +
                ", marca='" + marca + '\'' +
                ", tipo='" + tipo + '\'' +
                ", potencia=" + potencia +
                ", disponible=" + disponible +
                '}';
    }
}
