package com.example.crudjavafx;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;

public class ManejadorRelacionDireccionDB {

    private String url;
    private String user;
    private String password;

    public ManejadorRelacionDireccionDB(String url, String user, String password) {
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

    public int insertarRelacionPaciente(RelacionDireccion relacion){
        String query = "INSERT INTO public.patient_addresses (patient_id, address_id, date_address_from, date_address_to) VALUES (?, ?, ?, ?)";
        try(Connection conn = abrirConexion(); PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setInt(1, relacion.getIdEntidad());
            ps.setInt(2, relacion.getIdDireccion());
            ps.setDate(3, Date.valueOf(relacion.getFechaInicio()));
            if (relacion.getFechaFin() != null) { ps.setDate(4, Date.valueOf(relacion.getFechaFin())); } else { ps.setNull(4, Types.DATE); }
            return ps.executeUpdate();
        } catch (SQLException e){ e.printStackTrace(); return 0; }
    }

    public int actualizarRelacionPaciente(RelacionDireccion relacion){
        String query = "UPDATE public.patient_addresses SET date_address_to = ? WHERE patient_id = ? AND address_id = ? AND date_address_from = ?";
        try(Connection conn = abrirConexion(); PreparedStatement ps = conn.prepareStatement(query)) {
            if (relacion.getFechaFin() != null) { ps.setDate(1, Date.valueOf(relacion.getFechaFin())); } else { ps.setNull(1, Types.DATE); }
            ps.setInt(2, relacion.getIdEntidad());
            ps.setInt(3, relacion.getIdDireccion());
            ps.setDate(4, Date.valueOf(relacion.getFechaInicio()));
            return ps.executeUpdate();
        } catch (SQLException e){ e.printStackTrace(); return 0; }
    }

    public ArrayList<RelacionDireccion> getRelacionesPaciente(int idPacienteFiltro){
        ArrayList<RelacionDireccion> relaciones = new ArrayList<>();
        String query = "SELECT patient_id, address_id, date_address_from, date_address_to FROM public.patient_addresses WHERE patient_id = ? ORDER BY date_address_from DESC";

        try(Connection conn = abrirConexion(); PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setInt(1, idPacienteFiltro);
            ResultSet rs = ps.executeQuery();
            while (rs.next()){
                relaciones.add(new RelacionDireccion(
                        rs.getInt("patient_id"), rs.getInt("address_id"), rs.getDate("date_address_from").toLocalDate(),
                        rs.getDate("date_address_to") != null ? rs.getDate("date_address_to").toLocalDate() : null
                ));
            }
        } catch (SQLException e){ e.printStackTrace(); }
        return relaciones;
    }

    public void eliminarRelacionPaciente(int idPaciente, int idDireccion, LocalDate fechaInicio){
        String query = "DELETE FROM public.patient_addresses WHERE patient_id = ? AND address_id = ? AND date_address_from = ?";
        try(Connection conn = abrirConexion(); PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setInt(1, idPaciente);
            ps.setInt(2, idDireccion);
            ps.setDate(3, Date.valueOf(fechaInicio));
            ps.executeUpdate();
        } catch (SQLException e){ e.printStackTrace(); }
    }

    public int insertarRelacionStaff(RelacionDireccion relacion){
        String query = "INSERT INTO public.staff_addresses (staff_id, address_id, date_address_from, date_address_to) VALUES (?, ?, ?, ?)";
        try(Connection conn = abrirConexion(); PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setInt(1, relacion.getIdEntidad());
            ps.setInt(2, relacion.getIdDireccion());
            ps.setDate(3, Date.valueOf(relacion.getFechaInicio()));
            if (relacion.getFechaFin() != null) { ps.setDate(4, Date.valueOf(relacion.getFechaFin())); } else { ps.setNull(4, Types.DATE); }
            return ps.executeUpdate();
        } catch (SQLException e){ e.printStackTrace(); return 0; }
    }

    public ArrayList<RelacionDireccion> getRelacionesStaff(int idStaffFiltro){
        ArrayList<RelacionDireccion> relaciones = new ArrayList<>();
        String query = "SELECT staff_id, address_id, date_address_from, date_address_to FROM public.staff_addresses WHERE staff_id = ? ORDER BY date_address_from DESC";

        try(Connection conn = abrirConexion(); PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setInt(1, idStaffFiltro);
            ResultSet rs = ps.executeQuery();
            while (rs.next()){
                relaciones.add(new RelacionDireccion(
                        rs.getInt("staff_id"), rs.getInt("address_id"), rs.getDate("date_address_from").toLocalDate(),
                        rs.getDate("date_address_to") != null ? rs.getDate("date_address_to").toLocalDate() : null
                ));
            }
        } catch (SQLException e){ e.printStackTrace(); }
        return relaciones;
    }

    // Metodo: R - Read (SELECT) TODAS las Direcciones Históricas de Pacientes
    public ArrayList<RelacionDireccion> getTodasRelacionesPaciente(){
        ArrayList<RelacionDireccion> relaciones = new ArrayList<>();
        String query = "SELECT patient_id, address_id, date_address_from, date_address_to FROM public.patient_addresses ORDER BY patient_id ASC, date_address_from DESC";

        try(Connection conn = abrirConexion();
            PreparedStatement ps = conn.prepareStatement(query)) {

            ResultSet rs = ps.executeQuery();

            while (rs.next()){
                int idEntidad = rs.getInt("patient_id");
                int idDireccion = rs.getInt("address_id");
                LocalDate fechaInicio = rs.getDate("date_address_from").toLocalDate();
                LocalDate fechaFin = rs.getDate("date_address_to") != null ? rs.getDate("date_address_to").toLocalDate() : null;

                relaciones.add(new RelacionDireccion(idEntidad, idDireccion, fechaInicio, fechaFin));
            }

        } catch (SQLException e){
            e.printStackTrace();
        }

        return relaciones;
    }

}