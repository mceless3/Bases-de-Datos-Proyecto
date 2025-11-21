package com.example.crudjavafx;

public class ItemFactura {

    private final int idFactura;
    private final int numSecuencia;
    private String codigoItem;
    private int cantidad;
    private double costoTotal;

    public ItemFactura(int idFactura, int numSecuencia, String codigoItem, int cantidad, double costoTotal) {
        this.idFactura = idFactura;
        this.numSecuencia = numSecuencia;
        this.codigoItem = codigoItem;
        this.cantidad = cantidad;
        this.costoTotal = costoTotal;
    }

    public int getIdFactura() { return idFactura; }
    public int getNumSecuencia() { return numSecuencia; }
    public String getCodigoItem() { return codigoItem; }
    public void setCodigoItem(String codigoItem) { this.codigoItem = codigoItem; }
    public int getCantidad() { return cantidad; }
    public void setCantidad(int cantidad) { this.cantidad = cantidad; }
    public double getCostoTotal() { return costoTotal; }
    public void setCostoTotal(double costoTotal) { this.costoTotal = costoTotal; }

    @Override
    public String toString() {
        return "Factura ID: " + idFactura + ", Secuencia: " + numSecuencia + ", Ítem: " + codigoItem + ", Cantidad: " + cantidad;
    }
}