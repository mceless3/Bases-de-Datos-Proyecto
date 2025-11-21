package com.example.crudjavafx;

import java.time.LocalDate;

public class Estancia {

    private final int idPaciente;
    private final String idHabitacion;
    private final LocalDate fechaInicio;
    private LocalDate fechaFin;

    public Estancia(int idPaciente, String idHabitacion, LocalDate fechaInicio, LocalDate fechaFin) {
        this.idPaciente = idPaciente;
        this.idHabitacion = idHabitacion;
        this.fechaInicio = fechaInicio;
        this.fechaFin = fechaFin;
    }

    public int getIdPaciente() { return idPaciente; }
    public String getIdHabitacion() { return idHabitacion; }
    public LocalDate getFechaInicio() { return fechaInicio; }
    public LocalDate getFechaFin() { return fechaFin; }
    public void setFechaFin(LocalDate fechaFin) { this.fechaFin = fechaFin; }

    @Override
    public String toString() {
        return "Paciente ID: " + idPaciente + ", Habitación: " + idHabitacion + ", Inicio: " + fechaInicio;
    }
}