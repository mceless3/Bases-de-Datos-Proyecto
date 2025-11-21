package com.example.crudjavafx;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.stage.Stage;
import java.sql.Connection;

public class CRUDComponenteController {

    @FXML private TextField codigoText;
    @FXML private TextArea descripcionArea;

    @FXML private TableView<ComponenteRegistro> tablaComponentes;
    @FXML private TableColumn<ComponenteRegistro, String> columnaCodigo;
    @FXML private TableColumn<ComponenteRegistro, String> columnaDescripcion;

    private ManejadorComponenteDB manejadorComponenteDB;
    private ObservableList<ComponenteRegistro> listaObservable;

    public void initData(String url, String user, String password) {
        manejadorComponenteDB = new ManejadorComponenteDB(url, user, password);
        listaObservable = FXCollections.observableArrayList(manejadorComponenteDB.getComponentesPS());
        tablaComponentes.setItems(listaObservable);

        tablaComponentes.getSelectionModel().selectedItemProperty().addListener((obs, oldSel, newSel)->{
            if (newSel != null) { cargarSeleccionado(newSel); }
        });
        limpiarForm();
    }

    @FXML
    public void initialize(){
        columnaCodigo.setCellValueFactory(new PropertyValueFactory<>("codigo"));
        columnaDescripcion.setCellValueFactory(new PropertyValueFactory<>("descripcion"));
    }

    @FXML
    private void guardarComponente() {
        String codigo = codigoText.getText().toUpperCase();
        String descripcion = descripcionArea.getText();

        if (codigo.isEmpty() || descripcion.isEmpty()) {
            mostrarAlerta("El Código y la Descripción son obligatorios", "Error", Alert.AlertType.ERROR); return;
        }

        ComponenteRegistro componente = new ComponenteRegistro(codigo, descripcion);
        int resultado;

        boolean existe = listaObservable.stream().anyMatch(c -> c.getCodigo().equals(codigo));

        if (existe) {
            resultado = manejadorComponenteDB.actualizarPS(componente);
            mostrarAlerta((resultado > 0) ? "Componente actualizado correctamente" : "No se pudo actualizar.", "Aviso", Alert.AlertType.INFORMATION);
        } else {
            resultado = manejadorComponenteDB.insertarPS(componente);
            mostrarAlerta((resultado > 0) ? "Componente agregado correctamente" : "No se pudo agregar (Código duplicado).", "Aviso", Alert.AlertType.INFORMATION);
        }
        recargarDatos();
    }

    @FXML
    private void eliminarComponente() {
        String codigoEliminar = codigoText.getText();
        if (codigoEliminar.isEmpty()) { mostrarAlerta("Seleccione un Código para eliminar", "Aviso", Alert.AlertType.WARNING); return; }

        manejadorComponenteDB.eliminarPS(codigoEliminar);
        mostrarAlerta("Componente eliminado (si existía)", "Correcto", Alert.AlertType.INFORMATION);
        recargarDatos();
    }

    @FXML
    private void filtrarComponentes() {
        String filtroGeneral = descripcionArea.getText().isEmpty() ? null : descripcionArea.getText();

        if (!codigoText.isDisable() && !codigoText.getText().isEmpty()) {
            filtroGeneral = codigoText.getText();
        }

        listaObservable.setAll(manejadorComponenteDB.getComponentesPorFiltroPS(filtroGeneral));

        if (listaObservable.isEmpty()) {
            mostrarAlerta("No se encontraron componentes con el filtro proporcionado", "Aviso", Alert.AlertType.INFORMATION);
        }
        limpiarForm();
    }

    private void cargarSeleccionado(ComponenteRegistro componente) {
        codigoText.setText(componente.getCodigo());
        codigoText.setDisable(true);
        descripcionArea.setText(componente.getDescripcion());
    }

    @FXML
    private void recargarDatos() {
        listaObservable.setAll(manejadorComponenteDB.getComponentesPS());
        limpiarForm();
    }

    @FXML
    private void limpiarForm() {
        codigoText.setText("");
        codigoText.setDisable(false);
        descripcionArea.clear();
        tablaComponentes.getSelectionModel().clearSelection();
    }

    @FXML
    private void probarConexión() {
        Connection conn = manejadorComponenteDB.abrirConexion();
        if (conn != null) {
            mostrarAlerta("Conexión exitosa!!", "Éxito", Alert.AlertType.INFORMATION);
            manejadorComponenteDB.cerrarConexion(conn);
        } else { mostrarAlerta("No se pudo conectar.", "Error", Alert.AlertType.ERROR); }
    }

    private void mostrarAlerta(String mensaje, String titulo, Alert.AlertType tipo) {
        Alert alerta = new Alert(tipo);
        alerta.setTitle("Aviso");
        alerta.setHeaderText(null);
        alerta.setContentText(mensaje);
        Stage stage = (Stage) tablaComponentes.getScene().getWindow();
        if (stage != null) { alerta.initOwner(stage); }
        alerta.showAndWait();
    }
}