package com.example.crudjavafx;

import java.time.LocalDate;

public class FacturaPaciente {

    private final int idFactura;
    private int idPaciente;
    private LocalDate fechaPago;
    private double montoTotal;
    private String estadoPago;

    public FacturaPaciente(int idFactura, int idPaciente, LocalDate fechaPago, double montoTotal, String estadoPago) {
        this.idFactura = idFactura;
        this.idPaciente = idPaciente;
        this.fechaPago = fechaPago;
        this.montoTotal = montoTotal;
        this.estadoPago = estadoPago;
    }

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
        return "Factura ID: " + idFactura + ", Paciente ID: " + idPaciente + ", Monto: " + montoTotal + ", Estado: " + estadoPago;
    }
}