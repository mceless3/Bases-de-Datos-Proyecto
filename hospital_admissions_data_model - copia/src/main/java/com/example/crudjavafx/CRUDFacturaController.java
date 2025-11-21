package com.example.crudjavafx;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.stage.Stage;

import java.sql.Connection;
import java.time.LocalDate;

public class CRUDFacturaController {

    @FXML private TextField idFacturaText;
    @FXML private TextField idPacienteText;
    @FXML private TextField montoTotalText;
    @FXML private DatePicker fechaPagoPicker;
    @FXML private ComboBox<String> estadoPagoComboBox;

    @FXML private TableView<FacturaPaciente> tablaFacturas;
    @FXML private TableColumn<FacturaPaciente, Integer> columnaIdFactura;
    @FXML private TableColumn<FacturaPaciente, Integer> columnaIdPaciente;
    @FXML private TableColumn<FacturaPaciente, Double> columnaMontoTotal;
    @FXML private TableColumn<FacturaPaciente, LocalDate> columnaFechaPago;
    @FXML private TableColumn<FacturaPaciente, String> columnaEstadoPago;

    private ManejadorFacturaDB manejadorFacturaDB;
    private ObservableList<FacturaPaciente> listaObservable;

    public void initData(String url, String user, String password) {
        manejadorFacturaDB = new ManejadorFacturaDB(url, user, password);
        listaObservable = FXCollections.observableArrayList(manejadorFacturaDB.getFacturasPS());
        tablaFacturas.setItems(listaObservable);

        if (estadoPagoComboBox != null) {
            estadoPagoComboBox.setItems(FXCollections.observableArrayList(manejadorFacturaDB.obtenerEstadosPago()));
        }

        tablaFacturas.getSelectionModel().selectedItemProperty().addListener((obs, oldSel, newSel)->{
            if (newSel != null) { cargarSeleccionado(newSel); }
        });
        limpiarForm();
    }

    @FXML
    public void initialize(){
        columnaIdFactura.setCellValueFactory(new PropertyValueFactory<>("idFactura"));
        columnaIdPaciente.setCellValueFactory(new PropertyValueFactory<>("idPaciente"));
        columnaMontoTotal.setCellValueFactory(new PropertyValueFactory<>("montoTotal"));
        columnaFechaPago.setCellValueFactory(new PropertyValueFactory<>("fechaPago"));
        columnaEstadoPago.setCellValueFactory(new PropertyValueFactory<>("estadoPago"));
    }

    @FXML
    private void guardarFactura() {
        int idPaciente = 0; double montoTotal = 0.0;
        try {
            idPaciente = Integer.parseInt(idPacienteText.getText());
            montoTotal = Double.parseDouble(montoTotalText.getText());
            if (idPaciente <= 0 || montoTotal < 0) throw new NumberFormatException();
        } catch (NumberFormatException e) {
            mostrarAlerta("ID Paciente inválido o Monto Total debe ser un número >= 0", "Error", Alert.AlertType.ERROR); return;
        }

        LocalDate fechaPago = fechaPagoPicker.getValue();
        String estadoPago = estadoPagoComboBox.getValue();

        if (estadoPago == null || estadoPago.isEmpty()) { mostrarAlerta("Debe seleccionar un Estado de Pago", "Error", Alert.AlertType.ERROR); return; }

        int idFactura = Integer.parseInt(idFacturaText.getText());
        FacturaPaciente factura = new FacturaPaciente(idFactura, idPaciente, fechaPago, montoTotal, estadoPago);

        int resultado = (idFactura == 0) ? manejadorFacturaDB.insertarPS(factura) : manejadorFacturaDB.actualizarPS(factura);

        if (resultado > 0) {
            mostrarAlerta((idFactura == 0) ? "Factura agregada correctamente. ID: " + resultado : "Factura actualizada correctamente.", "Correcto", Alert.AlertType.INFORMATION);
        } else {
            mostrarAlerta("No se pudo guardar la factura (Verifique si el ID Paciente existe)", "Error", Alert.AlertType.ERROR);
        }
        recargarDatos();
    }

    @FXML
    private void eliminarFactura() {
        int idEliminar;
        try { idEliminar = Integer.parseInt(idFacturaText.getText()); }
        catch (NumberFormatException e) { mostrarAlerta("Seleccione un ID de Factura válido para eliminar", "Aviso", Alert.AlertType.WARNING); return; }

        if (idEliminar > 0) {
            manejadorFacturaDB.eliminarPS(idEliminar);
            mostrarAlerta("Factura eliminada (si existía)", "Correcto", Alert.AlertType.INFORMATION);
            recargarDatos();
        }
    }

    @FXML
    private void filtrarFacturas() {
        Integer idPacienteFiltro = null;
        try { idPacienteFiltro = idPacienteText.getText().isEmpty() ? null : Integer.parseInt(idPacienteText.getText()); }
        catch (NumberFormatException e) { mostrarAlerta("ID Paciente debe ser un número entero", "Error", Alert.AlertType.ERROR); return; }

        Double montoTotalFiltro = null;
        if (!montoTotalText.getText().isEmpty()) {
            try {
                montoTotalFiltro = Double.parseDouble(montoTotalText.getText());
            } catch (NumberFormatException e) {
                mostrarAlerta("Monto Total debe ser un número válido", "Error", Alert.AlertType.ERROR);
                return;
            }
        }

        LocalDate fechaPagoFiltro = fechaPagoPicker.getValue();

        String estadoFiltro = estadoPagoComboBox.getValue();

        listaObservable.setAll(manejadorFacturaDB.getFacturasPorFiltroPS(idPacienteFiltro, estadoFiltro, montoTotalFiltro, fechaPagoFiltro));

        if (listaObservable.isEmpty()) { mostrarAlerta("No se encontraron facturas con los filtros proporcionados", "Aviso", Alert.AlertType.INFORMATION); }
        limpiarForm();
    }

    private void cargarSeleccionado(FacturaPaciente factura) {
        idFacturaText.setText(String.valueOf(factura.getIdFactura()));
        idPacienteText.setText(String.valueOf(factura.getIdPaciente()));
        montoTotalText.setText(String.valueOf(factura.getMontoTotal()));
        fechaPagoPicker.setValue(factura.getFechaPago());
        estadoPagoComboBox.setValue(factura.getEstadoPago());
    }

    @FXML
    private void recargarDatos() {
        listaObservable.setAll(manejadorFacturaDB.getFacturasPS());
        limpiarForm();
    }

    @FXML
    private void limpiarForm() {
        idFacturaText.setText(String.valueOf(0));
        idPacienteText.clear();
        montoTotalText.clear();
        fechaPagoPicker.setValue(null);
        estadoPagoComboBox.setValue(null);
        tablaFacturas.getSelectionModel().clearSelection();
    }

    @FXML
    private void probarConexión() {
        Connection conn = manejadorFacturaDB.abrirConexion();
        if (conn != null) {
            mostrarAlerta("Conexión exitosa!!", "Éxito", Alert.AlertType.INFORMATION);
            manejadorFacturaDB.cerrarConexion(conn);
        } else { mostrarAlerta("No se pudo conectar.", "Error", Alert.AlertType.ERROR); }
    }

    private void mostrarAlerta(String mensaje, String titulo, Alert.AlertType tipo) {
        Alert alerta = new Alert(tipo);
        alerta.setTitle("Aviso");
        alerta.setHeaderText(null);
        alerta.setContentText(mensaje);
        Stage stage = (Stage) tablaFacturas.getScene().getWindow();
        if (stage != null) { alerta.initOwner(stage); }
        alerta.showAndWait();
    }
}