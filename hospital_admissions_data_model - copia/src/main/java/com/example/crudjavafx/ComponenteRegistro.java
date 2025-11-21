package com.example.crudjavafx;

public class ComponenteRegistro {

    private final String codigo;
    private String descripcion;

    public ComponenteRegistro(String codigo, String descripcion) {
        this.codigo = codigo;
        this.descripcion = descripcion;
    }

    public String getCodigo() { return codigo; }
    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    @Override
    public String toString() {
        return "Código: " + codigo + " - Descripción: " + descripcion;
    }
}