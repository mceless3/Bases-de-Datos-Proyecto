package com.example.crudjavafx;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.stage.Stage;

import java.time.LocalDate;
import java.util.ArrayList;

public class CRUDFacturaController {

    // NUEVO CAMPO: Para el nombre completo editable
    @FXML private TextField nombreCompletoText;

    // Campos FXML existentes
    @FXML private TextField idPacienteText;
    @FXML private TextField montoTotalText;
    @FXML private DatePicker fechaPagoPicker;
    @FXML private ComboBox<String> estadoPagoComboBox;

    @FXML private TableView<FacturaPaciente> tablaFacturas;

    @FXML private TableColumn<FacturaPaciente, String> columnaNombrePaciente;
    @FXML private TableColumn<FacturaPaciente, Integer> columnaIdFactura;
    @FXML private TableColumn<FacturaPaciente, Integer> columnaIdPaciente;
    @FXML private TableColumn<FacturaPaciente, Double> columnaMontoTotal;
    @FXML private TableColumn<FacturaPaciente, LocalDate> columnaFechaPago;
    @FXML private TableColumn<FacturaPaciente, String> columnaEstadoPago;

    // DECLARACIÓN CORRECTA DE LA INSTANCIA (Usamos la clase original)
    private ManejadorFacturaDB manejadorFacturaDB;
    private ObservableList<FacturaPaciente> listaObservable;

    // Inicialización de la conexión
    public void initData(String url, String user, String password) {
        manejadorFacturaDB = new ManejadorFacturaDB(url, user, password);

        // Carga inicial de datos
        recargarDatos();

        // Cargar combo de estados
        if (estadoPagoComboBox != null) {
            estadoPagoComboBox.setItems(FXCollections.observableArrayList(manejadorFacturaDB.obtenerEstadosPago()));
        }

        // Listener para la selección de la tabla
        tablaFacturas.getSelectionModel().selectedItemProperty().addListener((obs, oldSel, newSel)->{
            if (newSel != null) {
                cargarSeleccionado(newSel);
            } else {
                limpiarForm();
            }
        });
        limpiarForm();
    }

    @FXML
    public void initialize(){
        columnaIdFactura.setCellValueFactory(new PropertyValueFactory<>("idFactura"));
        columnaNombrePaciente.setCellValueFactory(new PropertyValueFactory<>("nombreCompletoPaciente"));
        columnaIdPaciente.setCellValueFactory(new PropertyValueFactory<>("idPaciente"));
        columnaMontoTotal.setCellValueFactory(new PropertyValueFactory<>("montoTotal"));
        columnaFechaPago.setCellValueFactory(new PropertyValueFactory<>("fechaPago"));
        columnaEstadoPago.setCellValueFactory(new PropertyValueFactory<>("estadoPago"));
    }

    @FXML
    private void guardarFactura() {
        int idPaciente;
        double montoTotal;
        String nombreCompleto = nombreCompletoText.getText().trim();

        try {
            idPaciente = Integer.parseInt(idPacienteText.getText());
            montoTotal = Double.parseDouble(montoTotalText.getText());
            if (idPaciente <= 0 || montoTotal < 0) throw new NumberFormatException();
        } catch (NumberFormatException e) {
            mostrarAlerta("ID Paciente inválido o Monto Total debe ser un número >= 0", "Error", Alert.AlertType.ERROR);
            return;
        }

        LocalDate fechaPago = fechaPagoPicker.getValue();
        String estadoPago = estadoPagoComboBox.getValue();

        if (estadoPago == null || estadoPago.isEmpty() || nombreCompleto.isEmpty()) {
            mostrarAlerta("Debe completar todos los campos obligatorios.", "Error", Alert.AlertType.ERROR);
            return;
        }

        FacturaPaciente seleccionado = tablaFacturas.getSelectionModel().getSelectedItem();
        int idFactura = (seleccionado != null) ? seleccionado.getIdFactura() : 0;

        FacturaPaciente factura = new FacturaPaciente(idFactura, idPaciente, fechaPago, montoTotal, estadoPago);

        int resultadoFactura;

        // LÓGICA DE GUARDADO (Usando métodos originales PS)
        if (idFactura == 0) { // Insertar (USANDO NUEVO MÉTODO TRANSACCIONAL)
            // Se asume que en un caso real, obtendrías los ítems de la interfaz
            // Por ahora, pasamos una lista vacía para demostrar la lógica transaccional
            // y evitar errores de compilación con una lista de ítems no definidos.
            ArrayList<ItemFactura> itemsASociar = new ArrayList<>(); // << AQUI DEBERÍAS OBTENER LOS ÍTEMS >>

            resultadoFactura = manejadorFacturaDB.insertarTransaccional(factura, itemsASociar);

        } else { // Actualizar
            resultadoFactura = manejadorFacturaDB.actualizarPS(factura);

            // Actualizar nombre del paciente
            String[] partesNombre = nombreCompleto.split(" ", 2);
            String nombre = partesNombre[0];
            String apellido = (partesNombre.length > 1) ? partesNombre[1] : "";

            manejadorFacturaDB.actualizarNombrePaciente(idPaciente, nombre, apellido);
        }

        if (resultadoFactura > 0) {
            mostrarAlerta((idFactura == 0) ? "Factura agregada correctamente. ID: " + resultadoFactura : "Factura actualizada.", "Correcto", Alert.AlertType.INFORMATION);
        } else {
            mostrarAlerta("No se pudo guardar la factura (Verifique ID Paciente y si la transacción fue exitosa)", "Error", Alert.AlertType.ERROR);
        }
        recargarDatos();
    }


    @FXML
    private void eliminarFactura() {
        FacturaPaciente seleccionado = tablaFacturas.getSelectionModel().getSelectedItem();

        if (seleccionado == null) {
            mostrarAlerta("Seleccione una factura de la tabla para eliminar", "Aviso", Alert.AlertType.WARNING);
            return;
        }

        int idEliminar = seleccionado.getIdFactura();

        if (idEliminar > 0) {
            manejadorFacturaDB.eliminarPS(idEliminar);
            mostrarAlerta("Factura eliminada (ID: " + idEliminar + ")", "Correcto", Alert.AlertType.INFORMATION);
            recargarDatos();
        }
    }

    @FXML
    private void filtrarFacturas() {
        Integer idPacienteFiltro = null;
        try {
            if (!idPacienteText.getText().isEmpty()) {
                idPacienteFiltro = Integer.parseInt(idPacienteText.getText());
            }
        } catch (NumberFormatException e) {
            mostrarAlerta("ID Paciente debe ser un número entero", "Error", Alert.AlertType.ERROR);
            return;
        }

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

        // Usamos el método original getFacturasPorFiltroPS
        listaObservable.setAll(manejadorFacturaDB.getFacturasPorFiltroPS(idPacienteFiltro, estadoFiltro, montoTotalFiltro, fechaPagoFiltro));

        if (listaObservable.isEmpty()) {
            mostrarAlerta("No se encontraron facturas con los filtros proporcionados", "Aviso", Alert.AlertType.INFORMATION);
        }
        limpiarForm();
    }

    private void cargarSeleccionado(FacturaPaciente factura) {
        nombreCompletoText.setText(factura.getNombreCompletoPaciente());
        idPacienteText.setText(String.valueOf(factura.getIdPaciente()));
        montoTotalText.setText(String.valueOf(factura.getMontoTotal()));
        fechaPagoPicker.setValue(factura.getFechaPago());
        estadoPagoComboBox.setValue(factura.getEstadoPago());
    }

    @FXML
    private void recargarDatos() {
        if (manejadorFacturaDB != null) {
            listaObservable = FXCollections.observableArrayList(manejadorFacturaDB.getFacturasPS());
            tablaFacturas.setItems(listaObservable);
        }
        limpiarForm();
    }

    @FXML
    private void limpiarForm() {
        nombreCompletoText.clear();
        idPacienteText.clear();
        montoTotalText.clear();
        fechaPagoPicker.setValue(null);
        estadoPagoComboBox.setValue(null);
        tablaFacturas.getSelectionModel().clearSelection();
    }

    private void mostrarAlerta(String mensaje, String titulo, Alert.AlertType tipo) {
        Alert alerta = new Alert(tipo);
        alerta.setTitle(titulo);
        alerta.setHeaderText(null);
        alerta.setContentText(mensaje);
        Stage stage = (Stage) tablaFacturas.getScene().getWindow();
        if (stage != null) { alerta.initOwner(stage); }
        alerta.showAndWait();
    }
}