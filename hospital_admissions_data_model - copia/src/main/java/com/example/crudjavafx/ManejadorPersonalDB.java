package com.example.crudjavafx;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;

public class ManejadorPersonalDB {

    private String url;
    private String user;
    private String password;

    public ManejadorPersonalDB(String url, String user, String password) {
        this.url = url;
        this.user = user;
        this.password = password;
    }

    public Connection abrirConexion() {
        try {
            DriverManager.setLoginTimeout(10);
            return DriverManager.getConnection(url, user, password);
        } catch (SQLException e) { e.printStackTrace(); return null; }
    }
    public void cerrarConexion(Connection conn) {
        try { if (conn != null) { conn.close(); } } catch (SQLException e) { e.printStackTrace(); }
    }
    public boolean probarConexion() {
        Connection conn = abrirConexion();
        if (conn != null) { cerrarConexion(conn); return true; } else { return false; }
    }

    public int insertarPS(Personal personal){
        String query = "INSERT INTO public.staff (staff_first_name, staff_last_name, staff_job_title, staff_category_code, gender, staff_birth_date, is_active) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?) RETURNING staff_id";

        try(Connection conn = abrirConexion(); PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setString(1, personal.getNombre());
            ps.setString(2, personal.getApellido());
            ps.setString(3, personal.getPuesto());
            ps.setString(4, personal.getCategoria());
            ps.setString(5, personal.getGenero());
            ps.setDate(6, Date.valueOf(personal.getFechaNacimiento()));
            ps.setBoolean(7, personal.isActivo());
            ResultSet rs = ps.executeQuery();
            if (rs.next()) { return rs.getInt(1); }
            return 0;
        } catch (SQLException e){ e.printStackTrace(); return 0; }
    }

    public int actualizarPS(Personal personal){
        String query = "UPDATE public.staff SET staff_first_name = ?, staff_last_name = ?, staff_job_title = ?, staff_category_code = ?, gender = ?, staff_birth_date = ?, is_active = ? WHERE staff_id = ?";
        try(Connection conn = abrirConexion(); PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setString(1, personal.getNombre());
            ps.setString(2, personal.getApellido());
            ps.setString(3, personal.getPuesto());
            ps.setString(4, personal.getCategoria());
            ps.setString(5, personal.getGenero());
            ps.setDate(6, Date.valueOf(personal.getFechaNacimiento()));
            ps.setBoolean(7, personal.isActivo());
            ps.setInt(8, personal.getId());
            return ps.executeUpdate();
        } catch (SQLException e){ e.printStackTrace(); return 0; }
    }

    public ArrayList<Personal> getPersonalPS(){
        ArrayList<Personal> personalList = new ArrayList<>();
        String query = "SELECT staff_id, staff_first_name, staff_last_name, staff_job_title, staff_category_code, gender, staff_birth_date, is_active FROM public.staff ORDER BY staff_id";

        try(Connection conn = abrirConexion(); PreparedStatement ps = conn.prepareStatement(query); ResultSet rs = ps.executeQuery()) {
            while (rs.next()){
                personalList.add(new Personal(
                        rs.getInt("staff_id"), rs.getString("staff_first_name"), rs.getString("staff_last_name"),
                        rs.getString("staff_job_title"), rs.getString("staff_category_code"), rs.getString("gender"),
                        rs.getDate("staff_birth_date") != null ? rs.getDate("staff_birth_date").toLocalDate() : null,
                        rs.getBoolean("is_active")
                ));
            }
        } catch (SQLException e){ e.printStackTrace(); }
        return personalList;
    }

    public void eliminarPS(int id){
        String query = "DELETE FROM public.staff WHERE staff_id = ?";
        try(Connection conn = abrirConexion(); PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (SQLException e){ e.printStackTrace(); }
    }

    public ArrayList<String> obtenerCategorias() {
        ArrayList<String> categorias = new ArrayList<>();
        String query = "SELECT DISTINCT staff_category_code FROM public.staff WHERE staff_category_code IS NOT NULL ORDER BY staff_category_code ASC";

        try (Connection conn = abrirConexion(); PreparedStatement ps = conn.prepareStatement(query); ResultSet rs = ps.executeQuery()) {
            while (rs.next()) { categorias.add(rs.getString("staff_category_code")); }
        } catch (SQLException e) { e.printStackTrace(); }
        return categorias;
    }

    public ArrayList<Personal> getPersonalPorFiltroPS(
            String nombreFiltro,
            String apellidoFiltro,
            String puestoFiltro,
            String categoriaFiltro,
            LocalDate fechaNacFiltro,
            String generoFiltro,
            Boolean activoFiltro
    ){
        ArrayList<Personal> personalList = new ArrayList<>();
        String query = "SELECT staff_id, staff_first_name, staff_last_name, staff_job_title, staff_category_code, gender, staff_birth_date, is_active " +
                "FROM public.staff WHERE 1=1";

        if (nombreFiltro!= null) { query += " AND staff_first_name ILIKE ?"; }
        if (apellidoFiltro!= null) { query += " AND staff_last_name ILIKE ?"; }
        if (puestoFiltro!= null) { query += " AND staff_job_title ILIKE ?"; }
        if (categoriaFiltro!= null) { query += " AND staff_category_code = ?"; }
        if (generoFiltro!= null) { query += " AND gender = ?"; }
        if (activoFiltro!= null) { query += " AND is_active = ?"; }

        if (fechaNacFiltro!= null) {
            query += " AND staff_birth_date >= ?";
            query += " AND staff_birth_date < ?";
        }

        try(Connection conn = abrirConexion(); PreparedStatement ps = conn.prepareStatement(query)) {
            int index = 1;

            if (nombreFiltro != null) { ps.setString(index++, "%" + nombreFiltro + "%"); }
            if (apellidoFiltro != null) { ps.setString(index++, "%" + apellidoFiltro + "%"); }
            if (puestoFiltro != null) { ps.setString(index++, "%" + puestoFiltro + "%"); }
            if (categoriaFiltro != null) { ps.setString(index++, categoriaFiltro); }
            if (generoFiltro != null) { ps.setString(index++, generoFiltro); }
            if (activoFiltro != null) { ps.setBoolean(index++, activoFiltro); }

            if (fechaNacFiltro != null) {
                ps.setDate(index++, Date.valueOf(fechaNacFiltro));
                LocalDate nextDay = fechaNacFiltro.plusDays(1);
                ps.setDate(index++, Date.valueOf(nextDay));
            }

            ResultSet rs = ps.executeQuery();
            while (rs.next()){
                personalList.add(new Personal(
                        rs.getInt("staff_id"), rs.getString("staff_first_name"), rs.getString("staff_last_name"),
                        rs.getString("staff_job_title"), rs.getString("staff_category_code"), rs.getString("gender"),
                        rs.getDate("staff_birth_date") != null ? rs.getDate("staff_birth_date").toLocalDate() : null,
                        rs.getBoolean("is_active")
                ));
            }
            return personalList;
        } catch (SQLException e){
            e.printStackTrace();
        }
        return personalList;
    }
}