package com.example.crudjavafx;

import java.sql.*;
import java.util.ArrayList;

public class ManejadorComponenteDB {

    private String url;
    private String user;
    private String password;

    public ManejadorComponenteDB(String url, String user, String password) {
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

    public int insertarPS(ComponenteRegistro componente){
        String query = "INSERT INTO public.record_components (component_code, component_description) VALUES (?, ?)";
        try(Connection conn = abrirConexion(); PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setString(1, componente.getCodigo());
            ps.setString(2, componente.getDescripcion());
            return ps.executeUpdate();
        } catch (SQLException e){ e.printStackTrace(); return 0; }
    }

    public int actualizarPS(ComponenteRegistro componente){
        String query = "UPDATE public.record_components SET component_description = ? WHERE component_code = ?";
        try(Connection conn = abrirConexion(); PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setString(1, componente.getDescripcion());
            ps.setString(2, componente.getCodigo());
            return ps.executeUpdate();
        } catch (SQLException e){ e.printStackTrace(); return 0; }
    }

    public ArrayList<ComponenteRegistro> getComponentesPS(){
        ArrayList<ComponenteRegistro> componentes = new ArrayList<>();
        String query = "SELECT component_code, component_description FROM public.record_components ORDER BY component_code";
        try(Connection conn = abrirConexion(); PreparedStatement ps = conn.prepareStatement(query); ResultSet rs = ps.executeQuery()) {
            while (rs.next()){
                componentes.add(new ComponenteRegistro(rs.getString("component_code"), rs.getString("component_description")));
            }
        } catch (SQLException e){ e.printStackTrace(); }
        return componentes;
    }

    public void eliminarPS(String codigo){
        String query = "DELETE FROM public.record_components WHERE component_code = ?";
        try(Connection conn = abrirConexion(); PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setString(1, codigo);
            ps.executeUpdate();
        } catch (SQLException e){ e.printStackTrace(); }
    }

    public ArrayList<ComponenteRegistro> getComponentesPorFiltroPS(String filtro){
        ArrayList<ComponenteRegistro> componentes = new ArrayList<>();
        String query = "SELECT component_code, component_description FROM public.record_components WHERE 1=1";
        if (filtro != null && !filtro.isEmpty()) {
            query += " AND (component_code ILIKE ? OR component_description ILIKE ?)";
        }

        try(Connection conn = abrirConexion();
            PreparedStatement ps = conn.prepareStatement(query)) {

            if (filtro != null && !filtro.isEmpty()) {
                ps.setString(1, "%" + filtro + "%");
                ps.setString(2, "%" + filtro + "%");
            }

            ResultSet rs = ps.executeQuery();
            while (rs.next()){
                componentes.add(new ComponenteRegistro(rs.getString("component_code"), rs.getString("component_description")));
            }
        } catch (SQLException e){ e.printStackTrace(); }
        return componentes;
    }
}