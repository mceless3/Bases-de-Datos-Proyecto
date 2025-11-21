package com.example.crudjavafx;

import java.time.LocalDate;

public class RelacionDireccion {

    private final int idEntidad;
    private final int idDireccion;
    private final LocalDate fechaInicio;
    private LocalDate fechaFin;

    public RelacionDireccion(int idEntidad, int idDireccion, LocalDate fechaInicio, LocalDate fechaFin) {
        this.idEntidad = idEntidad;
        this.idDireccion = idDireccion;
        this.fechaInicio = fechaInicio;
        this.fechaFin = fechaFin;
    }

    public int getIdEntidad() { return idEntidad; }
    public int getIdDireccion() { return idDireccion; }
    public LocalDate getFechaInicio() { return fechaInicio; }
    public LocalDate getFechaFin() { return fechaFin; }
    public void setFechaFin(LocalDate fechaFin) { this.fechaFin = fechaFin; }

    @Override
    public String toString() {
        return "Entidad ID: " + idEntidad + ", Dirección ID: " + idDireccion + ", Inicio: " + fechaInicio;
    }
}