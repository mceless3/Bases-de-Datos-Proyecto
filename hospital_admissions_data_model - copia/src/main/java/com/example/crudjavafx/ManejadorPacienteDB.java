package com.example.crudjavafx;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;

public class ManejadorPacienteDB {

    private String url;
    private String user;
    private String password;

    public ManejadorPacienteDB(String url, String user, String password) {
        this.url = url;
        this.user = user;
        this.password = password;
    }

    public Connection abrirConexion() {
        try {
            DriverManager.setLoginTimeout(10);
            return DriverManager.getConnection(url, user, password);
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public void cerrarConexion(Connection conn) {
        try {
            if (conn != null) { conn.close(); }
        } catch (SQLException e) { e.printStackTrace(); }
    }

    public boolean probarConexion() {
        Connection conn = abrirConexion();
        if (conn != null) { cerrarConexion(conn); return true; } else { return false; }
    }

    public int insertarPS(Paciente paciente){
        String query = "INSERT INTO public.patients (" +
                "patient_first_name, patient_last_name, hospital_number, weight, " +
                "date_of_birth, gender, outpatient_yn) " +
                "VALUES (?, ?, ?, ?, ?, ?, 't') RETURNING patient_id";

        try(Connection conn = abrirConexion(); PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setString(1, paciente.getNombre());
            ps.setString(2, paciente.getApellido());
            ps.setString(3, paciente.getHospitalNumber());
            ps.setDouble(4, paciente.getPeso());
            ps.setDate(5, Date.valueOf(paciente.getFechaNacimiento()));
            ps.setString(6, paciente.getGenero());
            ResultSet rs = ps.executeQuery();
            if (rs.next()) { return rs.getInt(1); }
            return 0;
        } catch (SQLException e){ e.printStackTrace(); return 0; }
    }

    public int actualizarPS(Paciente paciente){
        String query = "UPDATE public.patients SET " +
                "patient_first_name = ?, patient_last_name = ?, hospital_number = ?, " +
                "weight = ?, date_of_birth = ?, gender = ? " +
                " WHERE patient_id = ?";

        try(Connection conn = abrirConexion(); PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setString(1, paciente.getNombre());
            ps.setString(2, paciente.getApellido());
            ps.setString(3, paciente.getHospitalNumber());
            ps.setDouble(4, paciente.getPeso());
            ps.setDate(5, Date.valueOf(paciente.getFechaNacimiento()));
            ps.setString(6, paciente.getGenero());
            ps.setInt(7, paciente.getId());
            return ps.executeUpdate();
        } catch (SQLException e){ e.printStackTrace(); return 0; }
    }

    public ArrayList<Paciente> getPacientesPS(){
        ArrayList<Paciente> pacientes = new ArrayList<>();
        String query = "SELECT patient_id, patient_first_name, patient_last_name, hospital_number, weight, date_of_birth, gender FROM public.patients ORDER BY patient_id";

        try(Connection conn = abrirConexion(); PreparedStatement ps = conn.prepareStatement(query); ResultSet rs = ps.executeQuery()) {
            while (rs.next()){
                pacientes.add(new Paciente(
                        rs.getInt("patient_id"), rs.getString("patient_first_name"), rs.getString("patient_last_name"),
                        rs.getString("hospital_number"), rs.getDouble("weight"), rs.getDate("date_of_birth").toLocalDate(),
                        rs.getString("gender")
                ));
            }
        } catch (SQLException e){ e.printStackTrace(); }
        return pacientes;
    }

    public void eliminarPS(int id){
        String query = "DELETE FROM public.patients WHERE patient_id = ?";
        try(Connection conn = abrirConexion(); PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (SQLException e){ e.printStackTrace(); }
    }

    public ArrayList<Paciente> getPacientesPorFiltroPS(
            String nombreFiltro, String apellidoFiltro, String hospitalNumberFiltro,
            Double pesoFiltro, LocalDate fechaNacFiltro, String generoFiltro
    ){
        ArrayList<Paciente> pacientes = new ArrayList<>();
        String query = "SELECT patient_id, patient_first_name, patient_last_name, hospital_number, weight, date_of_birth, gender " +
                "FROM public.patients WHERE 1=1";

        if (nombreFiltro!= null) { query += " AND patient_first_name ILIKE ?"; }
        if (apellidoFiltro!= null) { query += " AND patient_last_name ILIKE ?"; }
        if (hospitalNumberFiltro!= null) { query += " AND hospital_number ILIKE ?"; }
        if (pesoFiltro!= null) { query += " AND weight = ?"; }
        if (fechaNacFiltro!= null) {
            query += " AND date_of_birth >= ?";
            query += " AND date_of_birth < ?";
        }
        if (generoFiltro!= null) { query += " AND gender = ?"; }

        try(Connection conn = abrirConexion(); PreparedStatement ps = conn.prepareStatement(query)) {
            int index = 1;
            if (nombreFiltro != null) { ps.setString(index++, "%" + nombreFiltro + "%"); }
            if (apellidoFiltro != null) { ps.setString(index++, "%" + apellidoFiltro + "%"); }
            if (hospitalNumberFiltro != null) { ps.setString(index++, "%" + hospitalNumberFiltro + "%"); }
            if (pesoFiltro != null) { ps.setDouble(index++, pesoFiltro); }
            if (fechaNacFiltro != null) { ps.setDate(index++, Date.valueOf(fechaNacFiltro));
                LocalDate nextDay = fechaNacFiltro.plusDays(1);
                ps.setDate(index++, Date.valueOf(nextDay));
            }
            if (generoFiltro != null) { ps.setString(index++, generoFiltro); }

            ResultSet rs = ps.executeQuery();
            while (rs.next()){
                pacientes.add(new Paciente(
                        rs.getInt("patient_id"), rs.getString("patient_first_name"), rs.getString("patient_last_name"),
                        rs.getString("hospital_number"), rs.getDouble("weight"), rs.getDate("date_of_birth").toLocalDate(),
                        rs.getString("gender")
                ));
            }
        } catch (SQLException e){ e.printStackTrace(); }
        return pacientes;
    }
}