package com.example.crudjavafx;

import java.sql.*;
import java.util.ArrayList;

public class ManejadorItemFacturaDB {

    private String url;
    private String user;
    private String password;

    public ManejadorItemFacturaDB(String url, String user, String password) {
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

    public int insertarPS(ItemFactura item){
        String query = "INSERT INTO public.patient_bill_items (patient_bill_id, item_seq_nr, item_code, quantity, total_cost) " +
                "VALUES (?, ?, ?, ?, ?)";

        try(Connection conn = abrirConexion(); PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setInt(1, item.getIdFactura());
            ps.setInt(2, item.getNumSecuencia());
            ps.setString(3, item.getCodigoItem());
            ps.setInt(4, item.getCantidad());
            ps.setDouble(5, item.getCostoTotal());
            return ps.executeUpdate();
        } catch (SQLException e){ e.printStackTrace(); return 0; }
    }

    public int actualizarPS(ItemFactura item){
        String query = "UPDATE public.patient_bill_items SET item_code = ?, quantity = ?, total_cost = ? WHERE patient_bill_id = ? AND item_seq_nr = ?";        try(Connection conn = abrirConexion(); PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setString(1, item.getCodigoItem());
            ps.setInt(2, item.getCantidad());
            ps.setDouble(3, item.getCostoTotal());
            ps.setInt(4, item.getIdFactura());
            ps.setInt(5, item.getNumSecuencia());
            return ps.executeUpdate();
        } catch (SQLException e){ e.printStackTrace(); return 0; }
    }

    public ArrayList<ItemFactura> getItemsFacturaPS(){
        ArrayList<ItemFactura> items = new ArrayList<>();
        String query = "SELECT patient_bill_id, item_seq_nr, item_code, quantity, total_cost FROM public.patient_bill_items ORDER BY patient_bill_id, item_seq_nr";
        try(Connection conn = abrirConexion(); PreparedStatement ps = conn.prepareStatement(query); ResultSet rs = ps.executeQuery()) {
            while (rs.next()){
                items.add(new ItemFactura(
                        rs.getInt("patient_bill_id"), rs.getInt("item_seq_nr"), rs.getString("item_code"),
                        rs.getInt("quantity"), rs.getDouble("total_cost")
                ));
            }
        } catch (SQLException e){ e.printStackTrace(); }
        return items;
    }

    public void eliminarPS(int idFactura, int numSecuencia){
        String query = "DELETE FROM public.patient_bill_items WHERE patient_bill_id = ? AND item_seq_nr = ?";
        try(Connection conn = abrirConexion(); PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setInt(1, idFactura);
            ps.setInt(2, numSecuencia);
            ps.executeUpdate();
        } catch (SQLException e){ e.printStackTrace(); }
    }

    public ArrayList<ItemFactura> getItemsPorFacturaPS(int idFacturaFiltro){
        ArrayList<ItemFactura> items = new ArrayList<>();
        String query = "SELECT patient_bill_id, item_seq_nr, item_code, quantity, total_cost FROM public.patient_bill_items WHERE patient_bill_id = ?";

        try(Connection conn = abrirConexion(); PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setInt(1, idFacturaFiltro);
            ResultSet rs = ps.executeQuery();
            while (rs.next()){
                items.add(new ItemFactura(
                        rs.getInt("patient_bill_id"), rs.getInt("item_seq_nr"), rs.getString("item_code"),
                        rs.getInt("quantity"), rs.getDouble("total_cost")
                ));
            }
        } catch (SQLException e){ e.printStackTrace(); }
        return items;
    }
}