package com.example.crudjavafx;

import java.sql.*;
import java.util.ArrayList;

public class ManejadorDireccionDB {

    private String url;
    private String user;
    private String password;

    public ManejadorDireccionDB(String url, String user, String password) {
        this.url = url;
        this.user = user;
        this.password = password;
    }

    public Connection abrirConexion() {
        try { DriverManager.setLoginTimeout(10); return DriverManager.getConnection(url, user, password); } catch (SQLException e) { e.printStackTrace(); return null; }
    }
    public void cerrarConexion(Connection conn) {
        try { if (conn != null) { conn.close(); } } catch (SQLException e) { e.printStackTrace(); }
    }
    public boolean probarConexion() {
        Connection conn = abrirConexion();
        if (conn != null) { cerrarConexion(conn); return true; } else { return false; }
    }

    public int insertarPS(Direccion direccion){
        String query = "INSERT INTO public.addresses (line_1_number_building, line_2_number_street, city, zip_postcode, country, is_primary_address) " +
                "VALUES (?, ?, ?, ?, ?, ?) RETURNING address_id";
        try(Connection conn = abrirConexion(); PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setString(1, direccion.getLinea1());
            ps.setString(2, direccion.getLinea2());
            ps.setString(3, direccion.getCiudad());
            ps.setString(4, direccion.getCodigoPostal());
            ps.setString(5, direccion.getPais());
            ps.setBoolean(6, direccion.isEsPrincipal());
            ResultSet rs = ps.executeQuery();
            if (rs.next()) { return rs.getInt(1); }
            return 0;
        } catch (SQLException e){ e.printStackTrace(); return 0; }
    }

    public int actualizarPS(Direccion direccion){
        String query = "UPDATE public.addresses SET line_1_number_building = ?, line_2_number_street = ?, city = ?, zip_postcode = ?, country = ?, is_primary_address = ? WHERE address_id = ?";
        try(Connection conn = abrirConexion(); PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setString(1, direccion.getLinea1());
            ps.setString(2, direccion.getLinea2());
            ps.setString(3, direccion.getCiudad());
            ps.setString(4, direccion.getCodigoPostal());
            ps.setString(5, direccion.getPais());
            ps.setBoolean(6, direccion.isEsPrincipal());
            ps.setInt(7, direccion.getId());
            return ps.executeUpdate();
        } catch (SQLException e){ e.printStackTrace(); return 0; }
    }

    public ArrayList<Direccion> getDireccionesPS(){
        ArrayList<Direccion> direcciones = new ArrayList<>();
        String query = "SELECT address_id, line_1_number_building, line_2_number_street, city, zip_postcode, country, is_primary_address FROM public.addresses ORDER BY address_id";
        try(Connection conn = abrirConexion(); PreparedStatement ps = conn.prepareStatement(query); ResultSet rs = ps.executeQuery()) {
            while (rs.next()){
                direcciones.add(new Direccion(
                        rs.getInt("address_id"), rs.getString("line_1_number_building"), rs.getString("line_2_number_street"),
                        rs.getString("city"), rs.getString("zip_postcode"), rs.getString("country"), rs.getBoolean("is_primary_address")
                ));
            }
        } catch (SQLException e){ e.printStackTrace(); }
        return direcciones;
    }

    public void eliminarPS(int id){
        String query = "DELETE FROM public.addresses WHERE address_id = ?";
        try(Connection conn = abrirConexion(); PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (SQLException e){ e.printStackTrace(); }
    }

    public ArrayList<Direccion> getDireccionesPorFiltroPS(
            String linea1Filtro,
            String linea2Filtro,
            String ciudadFiltro,
            String codigoPostalFiltro,
            String paisFiltro){
        ArrayList<Direccion> direcciones = new ArrayList<>();
        String query = "SELECT address_id, line_1_number_building, line_2_number_street, city, zip_postcode, country, is_primary_address FROM public.addresses WHERE 1=1";
        if (linea1Filtro!= null) { query += " AND line_1_number_building ILIKE ?"; }
        if (linea2Filtro!= null) { query += " AND line_2_number_street ILIKE ?"; }
        if (ciudadFiltro!= null) { query += " AND city ILIKE ?"; }
        if (codigoPostalFiltro!= null) { query += " AND zip_postcode ILIKE ?"; }
        if (paisFiltro!= null) { query += " AND country ILIKE ?"; }

        try(Connection conn = abrirConexion();
            PreparedStatement ps = conn.prepareStatement(query)) {

            int index = 1;

            if (linea1Filtro != null) { ps.setString(index++, "%" + linea1Filtro + "%"); }
            if (linea2Filtro != null) { ps.setString(index++, "%" + linea2Filtro + "%"); }
            if (ciudadFiltro != null) { ps.setString(index++, "%" + ciudadFiltro + "%"); }
            if (codigoPostalFiltro != null) { ps.setString(index++, "%" + codigoPostalFiltro + "%"); }
            if (paisFiltro != null) { ps.setString(index++, "%" + paisFiltro + "%"); }

            ResultSet rs = ps.executeQuery();
            while (rs.next()){
                direcciones.add(new Direccion(
                        rs.getInt("address_id"), rs.getString("line_1_number_building"), rs.getString("line_2_number_street"),
                        rs.getString("city"), rs.getString("zip_postcode"), rs.getString("country"), rs.getBoolean("is_primary_address")
                ));
            }
        } catch (SQLException e){ e.printStackTrace(); }
        return direcciones;
    }
}