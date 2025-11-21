package com.example.crudjavafx;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;

public class ManejadorFacturaDB {

    private String url;
    private String user;
    private String password;

    public ManejadorFacturaDB(String url, String user, String password) {
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

    public int insertarPS(FacturaPaciente factura){
        String query = "INSERT INTO public.patient_bills (patient_id, date_bill_paid, total_amount_due, payment_status) VALUES (?, ?, ?, ?) RETURNING patient_bill_id";

        try(Connection conn = abrirConexion(); PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setInt(1, factura.getIdPaciente());
            if (factura.getFechaPago() != null) { ps.setDate(2, Date.valueOf(factura.getFechaPago())); } else { ps.setNull(2, Types.DATE); }
            ps.setDouble(3, factura.getMontoTotal());
            ps.setString(4, factura.getEstadoPago());
            ResultSet rs = ps.executeQuery();
            if (rs.next()) { return rs.getInt(1); }
            return 0;
        } catch (SQLException e){ e.printStackTrace(); return 0; }
    }

    public int actualizarPS(FacturaPaciente factura){
        String query = "UPDATE public.patient_bills SET patient_id = ?, date_bill_paid = ?, total_amount_due = ?, payment_status = ? WHERE patient_bill_id = ?";
        try(Connection conn = abrirConexion(); PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setInt(1, factura.getIdPaciente());
            if (factura.getFechaPago() != null) { ps.setDate(2, Date.valueOf(factura.getFechaPago())); } else { ps.setNull(2, Types.DATE); }
            ps.setDouble(3, factura.getMontoTotal());
            ps.setString(4, factura.getEstadoPago());
            ps.setInt(5, factura.getIdFactura());
            return ps.executeUpdate();
        } catch (SQLException e){ e.printStackTrace(); return 0; }
    }

    public ArrayList<FacturaPaciente> getFacturasPS(){
        ArrayList<FacturaPaciente> facturas = new ArrayList<>();
        String query = "SELECT patient_bill_id, patient_id, date_bill_paid, total_amount_due, payment_status FROM public.patient_bills ORDER BY patient_bill_id DESC";

        try(Connection conn = abrirConexion(); PreparedStatement ps = conn.prepareStatement(query); ResultSet rs = ps.executeQuery()) {
            while (rs.next()){
                facturas.add(new FacturaPaciente(
                        rs.getInt("patient_bill_id"), rs.getInt("patient_id"),
                        rs.getDate("date_bill_paid") != null ? rs.getDate("date_bill_paid").toLocalDate() : null,
                        rs.getDouble("total_amount_due"), rs.getString("payment_status")
                ));
            }
        } catch (SQLException e){ e.printStackTrace(); }
        return facturas;
    }

    public void eliminarPS(int idFactura){
        String query = "DELETE FROM public.patient_bills WHERE patient_bill_id = ?";
        try(Connection conn = abrirConexion(); PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setInt(1, idFactura);
            ps.executeUpdate();
        } catch (SQLException e){ e.printStackTrace(); }
    }

    public ArrayList<String> obtenerEstadosPago() {
        ArrayList<String> estados = new ArrayList<>();
        estados.add("Pendiente");
        estados.add("Pagado");
        estados.add("Cancelado");
        return estados;
    }

    public ArrayList<FacturaPaciente> getFacturasPorFiltroPS(Integer idPacienteFiltro, String estadoFiltro, Double montoTotalFiltro, LocalDate fechaPagoFiltro){
        ArrayList<FacturaPaciente> facturas = new ArrayList<>();
        String query = "SELECT patient_bill_id, patient_id, date_bill_paid, total_amount_due, payment_status FROM public.patient_bills WHERE 1=1";

        if (idPacienteFiltro != null && idPacienteFiltro > 0) { query += " AND patient_id = ?"; }
        if (estadoFiltro != null && !estadoFiltro.isEmpty()) { query += " AND payment_status = ?"; }
        if (montoTotalFiltro != null) { query += " AND total_amount_due = ?"; }
        if (fechaPagoFiltro != null) {
            query += " AND date_bill_paid >= ?";
            query += " AND date_bill_paid < ?";
        }

        try(Connection conn = abrirConexion(); PreparedStatement ps = conn.prepareStatement(query)) {
            int index = 1;
            if (idPacienteFiltro != null && idPacienteFiltro > 0) { ps.setInt(index++, idPacienteFiltro); }
            if (estadoFiltro != null && !estadoFiltro.isEmpty()) { ps.setString(index++, estadoFiltro); }
            if (montoTotalFiltro != null) { ps.setDouble(index++, montoTotalFiltro); }
            if (fechaPagoFiltro != null) {
                ps.setDate(index++, Date.valueOf(fechaPagoFiltro));
                LocalDate nextDay = fechaPagoFiltro.plusDays(1);
                ps.setDate(index++, Date.valueOf(nextDay));
            }

            ResultSet rs = ps.executeQuery();
            while (rs.next()){
                facturas.add(new FacturaPaciente(
                        rs.getInt("patient_bill_id"), rs.getInt("patient_id"),
                        rs.getDate("date_bill_paid") != null ? rs.getDate("date_bill_paid").toLocalDate() : null,
                        rs.getDouble("total_amount_due"), rs.getString("payment_status")
                ));
            }
            return facturas;
        } catch (SQLException e){ e.printStackTrace(); }
        return facturas;
    }
}