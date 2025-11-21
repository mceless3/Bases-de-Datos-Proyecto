package com.example.crudjavafx;

import java.sql.*;
import java.util.ArrayList;

public class ManejadorMetodoPagoDB {

    private String url;
    private String user;
    private String password;

    public ManejadorMetodoPagoDB(String url, String user, String password) {
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

    public int insertarPS(MetodoPago metodo){
        String query = "INSERT INTO public.patient_payment_methods (patient_id, payment_method_code, payment_method_details) VALUES (?, ?, ?) RETURNING patient_method_id";
        try(Connection conn = abrirConexion(); PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setInt(1, metodo.getIdPaciente());
            ps.setString(2, metodo.getCodigoMetodo());
            ps.setString(3, metodo.getDetalles());
            ResultSet rs = ps.executeQuery();
            if (rs.next()) { return rs.getInt(1); }
            return 0;
        } catch (SQLException e){ e.printStackTrace(); return 0; }
    }

    public int actualizarPS(MetodoPago metodo){
        String query = "UPDATE public.patient_payment_methods SET patient_id = ?, payment_method_code = ?, payment_method_details = ? WHERE patient_method_id = ?";
        try(Connection conn = abrirConexion(); PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setInt(1, metodo.getIdPaciente());
            ps.setString(2, metodo.getCodigoMetodo());
            ps.setString(3, metodo.getDetalles());
            ps.setInt(4, metodo.getIdMetodo());
            return ps.executeUpdate();
        } catch (SQLException e){ e.printStackTrace(); return 0; }
    }

    public ArrayList<MetodoPago> getMetodosPagoPS(){
        ArrayList<MetodoPago> metodos = new ArrayList<>();
        String query = "SELECT patient_method_id, patient_id, payment_method_code, payment_method_details FROM public.patient_payment_methods ORDER BY patient_id, patient_method_id";
        try(Connection conn = abrirConexion(); PreparedStatement ps = conn.prepareStatement(query); ResultSet rs = ps.executeQuery()) {
            while (rs.next()){
                metodos.add(new MetodoPago(
                        rs.getInt("patient_method_id"), rs.getInt("patient_id"), rs.getString("payment_method_code"), rs.getString("payment_method_details")
                ));
            }
        } catch (SQLException e){ e.printStackTrace(); }
        return metodos;
    }

    public void eliminarPS(int idMetodo){
        String query = "DELETE FROM public.patient_payment_methods WHERE patient_method_id = ?";
        try(Connection conn = abrirConexion(); PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setInt(1, idMetodo);
            ps.executeUpdate();
        } catch (SQLException e){ e.printStackTrace(); }
    }

    public ArrayList<MetodoPago> getMetodosPorPacientePS(Integer idPacienteFiltro){
        ArrayList<MetodoPago> metodos = new ArrayList<>();
        String query = "SELECT patient_method_id, patient_id, payment_method_code, payment_method_details FROM public.patient_payment_methods WHERE 1=1";

        if (idPacienteFiltro != null && idPacienteFiltro > 0) { query += " AND patient_id = ?"; }

        try(Connection conn = abrirConexion(); PreparedStatement ps = conn.prepareStatement(query)) {
            if (idPacienteFiltro != null && idPacienteFiltro > 0) { ps.setInt(1, idPacienteFiltro); }
            ResultSet rs = ps.executeQuery();
            while (rs.next()){
                metodos.add(new MetodoPago(
                        rs.getInt("patient_method_id"), rs.getInt("patient_id"), rs.getString("payment_method_code"), rs.getString("payment_method_details")
                ));
            }
        } catch (SQLException e){ e.printStackTrace(); }
        return metodos;
    }
}