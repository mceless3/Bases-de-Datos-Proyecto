package com.example.crudjavafx;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.stage.Stage;

public class CRUDItemFacturaController {

    @FXML private TextField idFacturaText;
    @FXML private TextField numSecuenciaText;
    @FXML private TextField codigoItemText;
    @FXML private TextField cantidadText;
    @FXML private TextField costoTotalText;

    @FXML private TableView<ItemFactura> tablaItems;
    @FXML private TableColumn<ItemFactura, Integer> columnaIdFactura;
    @FXML private TableColumn<ItemFactura, Integer> columnaNumSecuencia;
    @FXML private TableColumn<ItemFactura, String> columnaCodigoItem;
    @FXML private TableColumn<ItemFactura, Integer> columnaCantidad;
    @FXML private TableColumn<ItemFactura, Double> columnaCostoTotal;

    // CORRECCIÓN AQUÍ: Usamos ManejadorItemFacturaDB, NO ManejadorFacturaDB
    private ManejadorItemFacturaDB manejadorItemFacturaDB;
    private ObservableList<ItemFactura> listaObservable;

    public void initData(String url, String user, String password) {
        // CORRECCIÓN AQUÍ: Instanciamos el manejador de Ítems correcto
        manejadorItemFacturaDB = new ManejadorItemFacturaDB(url, user, password);

        listaObservable = FXCollections.observableArrayList(manejadorItemFacturaDB.getItemsFacturaPS());
        tablaItems.setItems(listaObservable);

        tablaItems.getSelectionModel().selectedItemProperty().addListener((obs, oldSel, newSel)->{
            if (newSel != null) { cargarSeleccionado(newSel); }
        });

        limpiarForm();
    }

    @FXML
    public void initialize(){
        columnaIdFactura.setCellValueFactory(new PropertyValueFactory<>("idFactura"));
        columnaNumSecuencia.setCellValueFactory(new PropertyValueFactory<>("numSecuencia"));
        columnaCodigoItem.setCellValueFactory(new PropertyValueFactory<>("codigoItem"));
        columnaCantidad.setCellValueFactory(new PropertyValueFactory<>("cantidad"));
        columnaCostoTotal.setCellValueFactory(new PropertyValueFactory<>("costoTotal"));
    }

    @FXML
    private void guardarItem() {
        int idFactura, numSecuencia, cantidad; double costoTotal;
        String codigoItem = codigoItemText.getText();

        try {
            idFactura = Integer.parseInt(idFacturaText.getText());
            numSecuencia = Integer.parseInt(numSecuenciaText.getText());
            cantidad = Integer.parseInt(cantidadText.getText());
            costoTotal = Double.parseDouble(costoTotalText.getText());

            if (idFactura <= 0 || numSecuencia <= 0 || codigoItem.isEmpty() || cantidad <= 0 || costoTotal < 0) {
                throw new NumberFormatException("Valores inválidos o faltantes.");
            }
        } catch (NumberFormatException e) {
            mostrarAlerta("Verifique ID Factura, Secuencia, Código, Cantidad (>0) y Costo (>=0).", "Error", Alert.AlertType.ERROR); return;
        }

        ItemFactura item = new ItemFactura(idFactura, numSecuencia, codigoItem, cantidad, costoTotal);
        int resultado;

        // Métodos originales del manejador de items
        resultado = manejadorItemFacturaDB.actualizarPS(item);

        if (resultado > 0) {
            mostrarAlerta("Ítem actualizado correctamente", "Correcto", Alert.AlertType.INFORMATION);
        } else {
            resultado = manejadorItemFacturaDB.insertarPS(item);

            if (resultado > 0) {
                mostrarAlerta("Ítem agregado correctamente", "Correcto", Alert.AlertType.INFORMATION);
            } else {
                mostrarAlerta("No se pudo guardar el ítem. (Clave duplicada o ID Factura no existe)", "Error", Alert.AlertType.ERROR);
            }
        }
        recargarDatos();
    }

    @FXML
    private void eliminarItem() {
        int idFactura, numSecuencia;
        try {
            idFactura = Integer.parseInt(idFacturaText.getText());
            numSecuencia = Integer.parseInt(numSecuenciaText.getText());
        } catch (NumberFormatException e) { mostrarAlerta("Seleccione un Ítem válido para eliminar", "Aviso", Alert.AlertType.WARNING); return; }

        if (idFactura > 0 && numSecuencia > 0) {
            manejadorItemFacturaDB.eliminarPS(idFactura, numSecuencia);
            mostrarAlerta("Ítem eliminado (si existía)", "Correcto", Alert.AlertType.INFORMATION);
            recargarDatos();
        }
    }

    @FXML
    private void filtrarItems() {
        int idFacturaFiltro;
        try { idFacturaFiltro = Integer.parseInt(idFacturaText.getText()); }
        catch (NumberFormatException e) { mostrarAlerta("Ingrese un ID de Factura válido para filtrar", "Error", Alert.AlertType.ERROR); return; }

        listaObservable.setAll(manejadorItemFacturaDB.getItemsPorFacturaPS(idFacturaFiltro));

        if (listaObservable.isEmpty()) { mostrarAlerta("No se encontraron ítems para la Factura ID " + idFacturaFiltro, "Aviso", Alert.AlertType.INFORMATION); }
        limpiarForm();
    }

    private void cargarSeleccionado(ItemFactura item) {
        idFacturaText.setText(String.valueOf(item.getIdFactura()));
        idFacturaText.setDisable(true);
        numSecuenciaText.setText(String.valueOf(item.getNumSecuencia()));
        numSecuenciaText.setDisable(true);

        codigoItemText.setText(item.getCodigoItem());
        cantidadText.setText(String.valueOf(item.getCantidad()));
        costoTotalText.setText(String.valueOf(item.getCostoTotal()));
    }

    @FXML
    private void recargarDatos() {
        if (manejadorItemFacturaDB != null) {
            listaObservable.setAll(manejadorItemFacturaDB.getItemsFacturaPS());
            tablaItems.setItems(listaObservable);
        }
        limpiarForm();
    }

    @FXML
    private void limpiarForm() {
        idFacturaText.setText("0");
        idFacturaText.setDisable(false);
        numSecuenciaText.setText("0");
        numSecuenciaText.setDisable(false);
        codigoItemText.clear();
        cantidadText.clear();
        costoTotalText.clear();
        tablaItems.getSelectionModel().clearSelection();
    }

    private void mostrarAlerta(String mensaje, String titulo, Alert.AlertType tipo) {
        Alert alerta = new Alert(tipo);
        alerta.setTitle("Aviso");
        alerta.setHeaderText(null);
        alerta.setContentText(mensaje);
        Stage stage = (Stage) tablaItems.getScene().getWindow();
        if (stage != null) { alerta.initOwner(stage); }
        alerta.showAndWait();
    }
}