package com.example.crudjavafx;

public class Direccion {

    private final int id;
    private String linea1;
    private String linea2;
    private String ciudad;
    private String codigoPostal;
    private String pais;
    private boolean esPrincipal;

    public Direccion(int id, String linea1, String linea2, String ciudad, String codigoPostal, String pais, boolean esPrincipal) {
        this.id = id;
        this.linea1 = linea1;
        this.linea2 = linea2;
        this.ciudad = ciudad;
        this.codigoPostal = codigoPostal;
        this.pais = pais;
        this.esPrincipal = esPrincipal;
    }

    public int getId() { return id; }
    public String getLinea1() { return linea1; }
    public void setLinea1(String linea1) { this.linea1 = linea1; }
    public String getLinea2() { return linea2; }
    public void setLinea2(String linea2) { this.linea2 = linea2; }
    public String getCiudad() { return ciudad; }
    public void setCiudad(String ciudad) { this.ciudad = ciudad; }
    public String getCodigoPostal() { return codigoPostal; }
    public void setCodigoPostal(String codigoPostal) { this.codigoPostal = codigoPostal; }
    public String getPais() { return pais; }
    public void setPais(String pais) { this.pais = pais; }
    public boolean isEsPrincipal() { return esPrincipal; }
    public void setEsPrincipal(boolean esPrincipal) { this.esPrincipal = esPrincipal; }

    @Override
    public String toString() {
        return "ID: " + id + ", Dirección: " + linea1 + ", Ciudad: " + ciudad + ", País: " + pais;
    }
}