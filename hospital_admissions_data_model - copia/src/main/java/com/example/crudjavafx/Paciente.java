package com.example.crudjavafx;

import java.time.LocalDate;

public class Paciente {
    private final int id;
    private String nombre;
    private String apellido;
    private String hospitalNumber;
    private double peso;
    private LocalDate fechaNacimiento;
    private String genero;

    public Paciente(int id, String nombre, String apellido, String hospitalNumber, double peso, LocalDate fechaNacimiento, String genero) {
        this.id = id;
        this.nombre = nombre;
        this.apellido = apellido;
        this.hospitalNumber = hospitalNumber;
        this.peso = peso;
        this.fechaNacimiento = fechaNacimiento;
        this.genero = genero;
    }

    public int getId(){ return id; }
    public String getNombre(){ return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public String getApellido() { return apellido; }
    public void setApellido(String apellido) { this.apellido = apellido; }
    public String getHospitalNumber() { return hospitalNumber; }
    public void setHospitalNumber(String hospitalNumber) { this.hospitalNumber = hospitalNumber; }
    public double getPeso() { return peso; }
    public void setPeso(double peso) { this.peso = peso; }
    public LocalDate getFechaNacimiento() { return fechaNacimiento; }
    public void setFechaNacimiento(LocalDate fechaNacimiento) { this.fechaNacimiento = fechaNacimiento; }
    public String getGenero() { return genero; }
    public void setGenero(String genero) { this.genero = genero; }

    @Override
    public String toString(){
        return "ID: " + id + ", Nombre: " + nombre + " " + apellido + ", Nro. Hospital: " + hospitalNumber;
    }
}