package com.example.crudjavafx;

public class MetodoPago {

    private final int idMetodo;
    private int idPaciente;
    private String codigoMetodo;
    private String detalles;

    public MetodoPago(int idMetodo, int idPaciente, String codigoMetodo, String detalles) {
        this.idMetodo = idMetodo;
        this.idPaciente = idPaciente;
        this.codigoMetodo = codigoMetodo;
        this.detalles = detalles;
    }

    public int getIdMetodo() { return idMetodo; }

    public int getIdPaciente() { return idPaciente; }
    public void setIdPaciente(int idPaciente) { this.idPaciente = idPaciente; }

    public String getCodigoMetodo() { return codigoMetodo; }
    public void setCodigoMetodo(String codigoMetodo) { this.codigoMetodo = codigoMetodo; }

    public String getDetalles() { return detalles; }
    public void setDetalles(String detalles) { this.detalles = detalles; }

    @Override
    public String toString() {
        return "Método ID: " + idMetodo + ", Paciente ID: " + idPaciente + ", Código: " + codigoMetodo;
    }
}