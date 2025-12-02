package com.example.crudjavafx;

import java.time.LocalDate;

public class Estancia {

    private final int idPaciente;
    private final String idHabitacion;
    private final LocalDate fechaInicio;
    private LocalDate fechaFin;
    private final String nombrePaciente;
    private final String apellidoPaciente;

    // 1. CONSTRUCTOR PRINCIPAL (6 argumentos) - Usado por ManejadorEstanciaDB para SELECT
    public Estancia(int idPaciente, String idHabitacion, LocalDate fechaInicio, LocalDate fechaFin,
                    String nombrePaciente, String apellidoPaciente) {
        this.idPaciente = idPaciente;
        this.idHabitacion = idHabitacion;
        this.fechaInicio = fechaInicio;
        this.fechaFin = fechaFin;
        this.nombrePaciente = nombrePaciente;
        this.apellidoPaciente = apellidoPaciente;
    }

    // 2. CONSTRUCTOR AUXILIAR (4 argumentos) - USADO POR EL CRUDEstanciaController.java (¡El que te falta!)
    public Estancia(int idPaciente, String idHabitacion, LocalDate fechaInicio, LocalDate fechaFin) {
        // Llama al constructor principal con nombre y apellido nulos.
        this(idPaciente, idHabitacion, fechaInicio, fechaFin, null, null);
    }

    // Getters y Setters
    public int getIdPaciente() { return idPaciente; }
    public String getIdHabitacion() { return idHabitacion; }
    public LocalDate getFechaInicio() { return fechaInicio; }
    public LocalDate getFechaFin() { return fechaFin; }
    public void setFechaFin(LocalDate fechaFin) { this.fechaFin = fechaFin; }
    public String getNombrePaciente() { return nombrePaciente; }
    public String getApellidoPaciente() { return apellidoPaciente; }

    @Override
    public String toString() {
        return "Paciente: " + (nombrePaciente != null ? nombrePaciente + " " + apellidoPaciente : "ID " + idPaciente) + ", Habitación: " + idHabitacion + ", Inicio: " + fechaInicio;
    }
}