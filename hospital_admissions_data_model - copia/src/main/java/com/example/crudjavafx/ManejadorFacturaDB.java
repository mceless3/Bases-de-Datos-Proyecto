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

    private FacturaPaciente crearFacturaDesdeRS(ResultSet rs) throws SQLException {
        String nombre = rs.getString("patient_first_name");
        String apellido = rs.getString("patient_last_name");

        return new FacturaPaciente(
                rs.getInt("patient_bill_id"),
                rs.getInt("patient_id"),
                rs.getDate("date_bill_paid") != null ? rs.getDate("date_bill_paid").toLocalDate() : null,
                rs.getDouble("total_amount_due"),
                rs.getString("payment_status"),
                nombre != null ? nombre : "N/D",
                apellido != null ? apellido : "N/D"
        );
    }

    // -------------------------------------------------------------------------
    // OPERACIONES DE PERSISTENCIA (CRUD)
    // -------------------------------------------------------------------------

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

    public int actualizarNombrePaciente(int idPaciente, String nombre, String apellido){
        String query = "UPDATE public.patients SET patient_first_name = ?, patient_last_name = ? WHERE patient_id = ?";

        try(Connection conn = abrirConexion(); PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setString(1, nombre);
            ps.setString(2, apellido);
            ps.setInt(3, idPaciente);
            return ps.executeUpdate();
        } catch (SQLException e){
            e.printStackTrace();
            return 0;
        }
    }

    public void eliminarPS(int idFactura){
        String query = "DELETE FROM public.patient_bills WHERE patient_bill_id = ?";
        try(Connection conn = abrirConexion(); PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setInt(1, idFactura);
            ps.executeUpdate();
        } catch (SQLException e){ e.printStackTrace(); }
    }

    // -------------------------------------------------------------------------
    // CONSULTAS (LECTURA)
    // -------------------------------------------------------------------------

    public ArrayList<FacturaPaciente> getFacturasPS(){
        ArrayList<FacturaPaciente> facturas = new ArrayList<>();
        String query = "SELECT pb.patient_bill_id, pb.patient_id, pb.date_bill_paid, pb.total_amount_due, pb.payment_status, p.patient_first_name, p.patient_last_name " +
                "FROM public.patient_bills pb " +
                "LEFT JOIN public.patients p ON pb.patient_id = p.patient_id " +
                "ORDER BY pb.patient_bill_id DESC";

        try(Connection conn = abrirConexion(); PreparedStatement ps = conn.prepareStatement(query); ResultSet rs = ps.executeQuery()) {
            while (rs.next()){
                facturas.add(crearFacturaDesdeRS(rs));
            }
        } catch (SQLException e){ e.printStackTrace(); }
        return facturas;
    }

    public ArrayList<FacturaPaciente> getFacturasPorFiltroPS(Integer idPacienteFiltro, String estadoFiltro, Double montoTotalFiltro, LocalDate fechaPagoFiltro){
        ArrayList<FacturaPaciente> facturas = new ArrayList<>();
        String query = "SELECT pb.patient_bill_id, pb.patient_id, pb.date_bill_paid, pb.total_amount_due, pb.payment_status, p.patient_first_name, p.patient_last_name " +
                "FROM public.patient_bills pb " +
                "LEFT JOIN public.patients p ON pb.patient_id = p.patient_id " +
                "WHERE 1=1";

        if (idPacienteFiltro != null && idPacienteFiltro > 0) { query += " AND pb.patient_id = ?"; }
        if (estadoFiltro != null && !estadoFiltro.isEmpty()) { query += " AND pb.payment_status = ?"; }
        if (montoTotalFiltro != null) { query += " AND pb.total_amount_due = ?"; }
        if (fechaPagoFiltro != null) {
            query += " AND pb.date_bill_paid >= ?";
            query += " AND pb.date_bill_paid < ?";
        }

        query += " ORDER BY pb.patient_bill_id DESC";

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
                facturas.add(crearFacturaDesdeRS(rs));
            }
            return facturas;
        } catch (SQLException e){ e.printStackTrace(); }
        return facturas;
    }

    public ArrayList<String> obtenerEstadosPago() {
        ArrayList<String> estados = new ArrayList<>();
        estados.add("Pendiente");
        estados.add("Pagado");
        estados.add("Cancelado");
        return estados;
    }

    public int insertarTransaccional(FacturaPaciente factura, ArrayList<ItemFactura> items){
        Connection conn = null;
        PreparedStatement psFactura = null;
        PreparedStatement psItem = null;
        int nuevaIdFactura = 0;

        String queryFactura = "INSERT INTO public.patient_bills (patient_id, date_bill_paid, total_amount_due, payment_status) VALUES (?, ?, ?, ?) RETURNING patient_bill_id";
        String queryItem = "INSERT INTO public.patient_bill_items (patient_bill_id, item_seq_nr, item_code, quantity, total_cost) VALUES (?, ?, ?, ?, ?)";

        try {
            conn = abrirConexion();
            if (conn == null) return 0;
            conn.setAutoCommit(false); // INICIO DE LA TRANSACCIÓN

            // 1. Insertar Factura
            psFactura = conn.prepareStatement(queryFactura);
            psFactura.setInt(1, factura.getIdPaciente());
            if (factura.getFechaPago() != null) { psFactura.setDate(2, Date.valueOf(factura.getFechaPago())); } else { psFactura.setNull(2, Types.DATE); }
            psFactura.setDouble(3, factura.getMontoTotal());
            psFactura.setString(4, factura.getEstadoPago());

            ResultSet rs = psFactura.executeQuery();
            if (rs.next()) {
                nuevaIdFactura = rs.getInt(1); // Obtener el ID generado
            } else {
                conn.rollback(); // Fallo al obtener ID, revertir
                return 0;
            }

            // 2. Insertar Ítems (usando la nueva ID de Factura)
            psItem = conn.prepareStatement(queryItem);
            int seqNr = 1;
            for (ItemFactura item : items) {
                psItem.setInt(1, nuevaIdFactura); // Usar la nueva ID
                psItem.setInt(2, seqNr++); // Usar el contador de secuencia
                psItem.setString(3, item.getCodigoItem());
                psItem.setInt(4, item.getCantidad());
                psItem.setDouble(5, item.getCostoTotal());
                psItem.addBatch(); // Agregar al lote de inserción
            }
            psItem.executeBatch(); // Ejecutar todas las inserciones de ítems

            conn.commit(); // FINALIZACIÓN EXITOSA
            return nuevaIdFactura;

        } catch (SQLException e){
            e.printStackTrace();
            if (conn != null) {
                try { conn.rollback(); } catch (SQLException ex) { ex.printStackTrace(); } // ROLLBACK EN CASO DE ERROR
            }
            return 0;
        } finally {
            try {
                if (psFactura != null) psFactura.close();
                if (psItem != null) psItem.close();
                if (conn != null) conn.setAutoCommit(true); // Restaurar
                cerrarConexion(conn);
            } catch (SQLException e) { e.printStackTrace(); }
        }
    }

}