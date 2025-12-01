package com.example.crudjavafx;

import java.time.LocalDate;

public class FacturaPaciente {

    private final int idFactura;
    private int idPaciente;
    private LocalDate fechaPago;
    private double montoTotal;
    private String estadoPago;

    // Campos agregados para mostrar el nombre del cliente
    private String nombrePaciente;
    private String apellidoPaciente;

    // CONSTRUCTOR CORTO (para inserciones/actualizaciones POST-FORM)
    public FacturaPaciente(int idFactura, int idPaciente, LocalDate fechaPago, double montoTotal, String estadoPago) {
        this.idFactura = idFactura;
        this.idPaciente = idPaciente;
        this.fechaPago = fechaPago;
        this.montoTotal = montoTotal;
        this.estadoPago = estadoPago;
        // Los campos de nombre se inicializan a null/vacío.
        this.nombrePaciente = "";
        this.apellidoPaciente = "";
    }

    // CONSTRUCTOR LARGO (para cargar datos DESDE LA DB con el JOIN)
    public FacturaPaciente(int idFactura, int idPaciente, LocalDate fechaPago, double montoTotal, String estadoPago, String nombrePaciente, String apellidoPaciente) {
        this.idFactura = idFactura;
        this.idPaciente = idPaciente;
        this.fechaPago = fechaPago;
        this.montoTotal = montoTotal;
        this.estadoPago = estadoPago;
        this.nombrePaciente = nombrePaciente;
        this.apellidoPaciente = apellidoPaciente;
    }

    // ----------------------------------------------------
    // GETTERS Y SETTERS
    // ----------------------------------------------------

    public String getNombreCompletoPaciente() {
        return nombrePaciente + " " + apellidoPaciente;
    }

    public String getNombrePaciente() { return nombrePaciente; }
    public String getApellidoPaciente() { return apellidoPaciente; }

    public int getIdFactura() { return idFactura; }
    public int getIdPaciente() { return idPaciente; }
    public void setIdPaciente(int idPaciente) { this.idPaciente = idPaciente; }
    public LocalDate getFechaPago() { return fechaPago; }
    public void setFechaPago(LocalDate fechaPago) { this.fechaPago = fechaPago; }
    public double getMontoTotal() { return montoTotal; }
    public void setMontoTotal(double montoTotal) { this.montoTotal = montoTotal; }
    public String getEstadoPago() { return estadoPago; }
    public void setEstadoPago(String estadoPago) { this.estadoPago = estadoPago; }

    @Override
    public String toString() {
        return "Factura ID: " + idFactura + ", Paciente: " + getNombreCompletoPaciente() + " (" + idPaciente + "), Monto: " + montoTotal + ", Estado: " + estadoPago;
    }
}