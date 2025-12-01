package com.example.crudjavafx;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.stage.Stage;

import java.sql.Connection;

public class CRUDMetodoPagoController {

    @FXML private TextField idMetodoText;
    @FXML private TextField idPacienteText;
    @FXML private TextField codigoMetodoText;
    @FXML private TextArea detallesArea;

    @FXML private TableView<MetodoPago> tablaMetodos;
    @FXML private TableColumn<MetodoPago, Integer> columnaIdMetodo;
    @FXML private TableColumn<MetodoPago, Integer> columnaIdPaciente;
    @FXML private TableColumn<MetodoPago, String> columnaCodigoMetodo;
    @FXML private TableColumn<MetodoPago, String> columnaDetalles;

    private ManejadorMetodoPagoDB manejadorMetodoPagoDB;
    private ObservableList<MetodoPago> listaObservable;

    public void initData(String url, String user, String password) {
        manejadorMetodoPagoDB = new ManejadorMetodoPagoDB(url, user, password);
        listaObservable = FXCollections.observableArrayList(manejadorMetodoPagoDB.getMetodosPagoPS());
        tablaMetodos.setItems(listaObservable);

        tablaMetodos.getSelectionModel().selectedItemProperty().addListener((obs, oldSel, newSel)->{
            if (newSel != null) { cargarSeleccionado(newSel); }
        });

        limpiarForm();
    }

    @FXML
    public void initialize(){
        columnaIdMetodo.setCellValueFactory(new PropertyValueFactory<>("idMetodo"));
        columnaIdPaciente.setCellValueFactory(new PropertyValueFactory<>("idPaciente"));
        columnaCodigoMetodo.setCellValueFactory(new PropertyValueFactory<>("codigoMetodo"));
        columnaDetalles.setCellValueFactory(new PropertyValueFactory<>("detalles"));
    }

    @FXML
    private void guardarMetodo() {
        int idPaciente;
        try {
            idPaciente = Integer.parseInt(idPacienteText.getText());
            if (idPaciente <= 0) throw new NumberFormatException();
        } catch (NumberFormatException e) {
            mostrarAlerta("ID Paciente inválido o vacío", "Error", Alert.AlertType.ERROR); return;
        }

        String codigoMetodo = codigoMetodoText.getText();
        String detalles = detallesArea.getText();

        if (codigoMetodo.isEmpty()) {
            mostrarAlerta("El Código de Método de Pago es obligatorio", "Error", Alert.AlertType.ERROR); return;
        }

        int idMetodo;
        try {
            idMetodo = idMetodoText.getText().isEmpty() ? 0 : Integer.parseInt(idMetodoText.getText());
        } catch (NumberFormatException e) {
            mostrarAlerta("ID de Método inválido.", "Error", Alert.AlertType.ERROR); return;
        }

        MetodoPago metodo = new MetodoPago(idMetodo, idPaciente, codigoMetodo, detalles);
        int resultado;

        if (idMetodo == 0) {
            resultado = manejadorMetodoPagoDB.insertarPS(metodo);
            if (resultado > 0) {
                mostrarAlerta("Método de Pago agregado correctamente. ID: " + resultado, "Correcto", Alert.AlertType.INFORMATION);
            } else {
                mostrarAlerta("No se pudo agregar el método (Verifique si el ID Paciente existe)", "Error", Alert.AlertType.ERROR);
            }
        } else {
            resultado = manejadorMetodoPagoDB.actualizarPS(metodo);
            if (resultado > 0) {
                mostrarAlerta("Método de Pago actualizado correctamente", "Correcto", Alert.AlertType.INFORMATION);
            } else {
                mostrarAlerta("No se pudo actualizar el método", "Error", Alert.AlertType.ERROR);
            }
        }
        recargarDatos();
    }

    @FXML
    private void eliminarMetodo() {
        int idEliminar;
        try {
            idEliminar = Integer.parseInt(idMetodoText.getText());
            if (idEliminar <= 0) throw new NumberFormatException();
        } catch (NumberFormatException e) { mostrarAlerta("Seleccione un ID de Método válido para eliminar", "Aviso", Alert.AlertType.WARNING); return; }

        manejadorMetodoPagoDB.eliminarPS(idEliminar);
        mostrarAlerta("Método de Pago eliminado (si existía)", "Correcto", Alert.AlertType.INFORMATION);
        recargarDatos();
    }

    @FXML
    private void filtrarMetodos() {
        Integer idPacienteFiltro = null;
        try {
            idPacienteFiltro = idPacienteText.getText().isEmpty() ? null : Integer.parseInt(idPacienteText.getText());
        } catch (NumberFormatException e) {
            mostrarAlerta("ID Paciente debe ser un número entero", "Error", Alert.AlertType.ERROR); return;
        }

        listaObservable.setAll(manejadorMetodoPagoDB.getMetodosPorPacientePS(idPacienteFiltro));

        if (listaObservable.isEmpty()) { mostrarAlerta("No se encontraron métodos para el ID Paciente: " + idPacienteFiltro, "Aviso", Alert.AlertType.INFORMATION); }
        limpiarForm();
    }

    private void cargarSeleccionado(MetodoPago metodo) {
        idMetodoText.setText(String.valueOf(metodo.getIdMetodo()));
        idMetodoText.setDisable(true);

        idPacienteText.setText(String.valueOf(metodo.getIdPaciente()));
        codigoMetodoText.setText(metodo.getCodigoMetodo());
        detallesArea.setText(metodo.getDetalles());
    }

    @FXML
    private void recargarDatos() {
        listaObservable.setAll(manejadorMetodoPagoDB.getMetodosPagoPS());
        limpiarForm();
    }

    private void limpiarForm() {
        idMetodoText.setText("");
        idMetodoText.setDisable(false);
        idPacienteText.clear();
        codigoMetodoText.clear();
        detallesArea.clear();
        tablaMetodos.getSelectionModel().clearSelection();
    }

    private void mostrarAlerta(String mensaje, String titulo, Alert.AlertType tipo) {
        Alert alerta = new Alert(tipo);
        alerta.setTitle("Aviso");
        alerta.setHeaderText(null);
        alerta.setContentText(mensaje);
        Stage stage = (Stage) tablaMetodos.getScene().getWindow();
        if (stage != null) { alerta.initOwner(stage); }
        alerta.showAndWait();
    }
}