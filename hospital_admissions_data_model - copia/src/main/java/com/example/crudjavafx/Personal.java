package com.example.crudjavafx;

import java.time.LocalDate;

public class Personal {
    private final int id;
    private String nombre;
    private String apellido;
    private String puesto;
    private String categoria;
    private String genero;
    private LocalDate fechaNacimiento;
    private boolean activo;

    public Personal(int id, String nombre, String apellido, String puesto, String categoria, String genero, LocalDate fechaNacimiento, boolean activo) {
        this.id = id;
        this.nombre = nombre;
        this.apellido = apellido;
        this.puesto = puesto;
        this.categoria = categoria;
        this.genero = genero;
        this.fechaNacimiento = fechaNacimiento;
        this.activo = activo;
    }

    public int getId() { return id; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public String getApellido() { return apellido; }
    public void setApellido(String apellido) { this.apellido = apellido; }
    public String getPuesto() { return puesto; }
    public void setPuesto(String puesto) { this.puesto = puesto; }
    public String getCategoria() { return categoria; }
    public void setCategoria(String categoria) { this.categoria = categoria; }
    public String getGenero() { return genero; }
    public void setGenero(String genero) { this.genero = genero; }
    public LocalDate getFechaNacimiento() { return fechaNacimiento; }
    public void setFechaNacimiento(LocalDate fechaNacimiento) { this.fechaNacimiento = fechaNacimiento; }
    public boolean isActivo() { return activo; }
    public void setActivo(boolean activo) { this.activo = activo; }

    @Override
    public String toString() {
        return "ID: " + id + ", Nombre: " + nombre + " " + apellido + ", Puesto: " + puesto;
    }
}