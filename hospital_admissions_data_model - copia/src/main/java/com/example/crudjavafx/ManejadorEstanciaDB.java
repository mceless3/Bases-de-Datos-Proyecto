package com.example.crudjavafx;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;

public class ManejadorEstanciaDB {

    private String url;
    private String user;
    private String password;

    public ManejadorEstanciaDB(String url, String user, String password) {
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

    public int insertarPS(Estancia estancia){
        String query = "INSERT INTO public.patient_rooms (patient_id, room_id, date_stay_from, date_stay_to) VALUES (?, ?, ?, ?)";
        try(Connection conn = abrirConexion(); PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setInt(1, estancia.getIdPaciente());
            ps.setString(2, estancia.getIdHabitacion());
            ps.setDate(3, Date.valueOf(estancia.getFechaInicio()));
            if (estancia.getFechaFin() != null) { ps.setDate(4, Date.valueOf(estancia.getFechaFin())); } else { ps.setNull(4, Types.DATE); }
            return ps.executeUpdate();
        } catch (SQLException e){ e.printStackTrace(); return 0; }
    }

    public int actualizarPS(Estancia estancia){
        String query = "UPDATE public.patient_rooms SET date_stay_to = ? WHERE patient_id = ? AND room_id = ? AND date_stay_from = ?";
        try(Connection conn = abrirConexion(); PreparedStatement ps = conn.prepareStatement(query)) {
            if (estancia.getFechaFin() != null) { ps.setDate(1, Date.valueOf(estancia.getFechaFin())); } else { ps.setNull(1, Types.DATE); }
            ps.setInt(2, estancia.getIdPaciente());
            ps.setString(3, estancia.getIdHabitacion());
            ps.setDate(4, Date.valueOf(estancia.getFechaInicio()));
            return ps.executeUpdate();
        } catch (SQLException e){ e.printStackTrace(); return 0; }
    }

    public ArrayList<Estancia> getEstanciasPS(){
        ArrayList<Estancia> estancias = new ArrayList<>();
        String query = "SELECT patient_id, room_id, date_stay_from, date_stay_to FROM public.patient_rooms ORDER BY patient_id ASC, date_stay_from DESC";

        try(Connection conn = abrirConexion(); PreparedStatement ps = conn.prepareStatement(query); ResultSet rs = ps.executeQuery()) {
            while (rs.next()){
                estancias.add(new Estancia(
                        rs.getInt("patient_id"), rs.getString("room_id"), rs.getDate("date_stay_from").toLocalDate(),
                        rs.getDate("date_stay_to") != null ? rs.getDate("date_stay_to").toLocalDate() : null
                ));
            }
        } catch (SQLException e){ e.printStackTrace(); }
        return estancias;
    }

    public void eliminarPS(int idPaciente, String idHabitacion, LocalDate fechaInicio){
        String query = "DELETE FROM public.patient_rooms WHERE patient_id = ? AND room_id = ? AND date_stay_from = ?";
        try(Connection conn = abrirConexion(); PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setInt(1, idPaciente);
            ps.setString(2, idHabitacion);
            ps.setDate(3, Date.valueOf(fechaInicio));
            ps.executeUpdate();
        } catch (SQLException e){ e.printStackTrace(); }
    }

    public ArrayList<Estancia> getEstanciasPorPacientePS(int idPacienteFiltro){
        ArrayList<Estancia> estancias = new ArrayList<>();
        String query = "SELECT patient_id, room_id, date_stay_from, date_stay_to FROM public.patient_rooms WHERE patient_id = ? ORDER BY date_stay_from DESC";

        try(Connection conn = abrirConexion(); PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setInt(1, idPacienteFiltro);
            ResultSet rs = ps.executeQuery();
            while (rs.next()){
                estancias.add(new Estancia(
                        rs.getInt("patient_id"), rs.getString("room_id"), rs.getDate("date_stay_from").toLocalDate(),
                        rs.getDate("date_stay_to") != null ? rs.getDate("date_stay_to").toLocalDate() : null
                ));
            }
        } catch (SQLException e){ e.printStackTrace(); }
        return estancias;
    }
}