package com.example.crudjavafx;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.stage.Stage;

import java.sql.Connection;
import java.time.LocalDate;

public class CRUDRelacionDireccionController {

    @FXML private TextField idEntidadText;
    @FXML private TextField idDireccionText;
    @FXML private DatePicker fechaInicioPicker;
    @FXML private DatePicker fechaFinPicker;

    @FXML private TableView<RelacionDireccion> tablaRelaciones;
    @FXML private TableColumn<RelacionDireccion, Integer> columnaIdEntidad;
    @FXML private TableColumn<RelacionDireccion, Integer> columnaIdDireccion;
    @FXML private TableColumn<RelacionDireccion, LocalDate> columnaFechaInicio;
    @FXML private TableColumn<RelacionDireccion, LocalDate> columnaFechaFin;

    private ManejadorRelacionDireccionDB manejadorRelacionDB;
    private ObservableList<RelacionDireccion> listaObservable;

    public void initData(String url, String user, String password) {
        manejadorRelacionDB = new ManejadorRelacionDireccionDB(url, user, password);
        listaObservable = FXCollections.observableArrayList(manejadorRelacionDB.getTodasRelacionesPaciente());
        tablaRelaciones.setItems(listaObservable);

        tablaRelaciones.getSelectionModel().selectedItemProperty().addListener((obs, oldSel, newSel)->{
            if (newSel != null) { cargarSeleccionado(newSel); }
        });

        idEntidadText.setText("");
        limpiarForm();
    }

    @FXML
    public void initialize(){
        columnaIdEntidad.setCellValueFactory(new PropertyValueFactory<>("idEntidad"));
        columnaIdDireccion.setCellValueFactory(new PropertyValueFactory<>("idDireccion"));
        columnaFechaInicio.setCellValueFactory(new PropertyValueFactory<>("fechaInicio"));
        columnaFechaFin.setCellValueFactory(new PropertyValueFactory<>("fechaFin"));
    }

    @FXML
    private void guardarRelacion() {
        int idEntidad, idDireccion;
        LocalDate fechaInicio = fechaInicioPicker.getValue();

        try {
            idEntidad = Integer.parseInt(idEntidadText.getText());
            idDireccion = Integer.parseInt(idDireccionText.getText());
            if (idEntidad <= 0 || idDireccion <= 0 || fechaInicio == null) { throw new NumberFormatException(); }
        } catch (NumberFormatException e) {
            mostrarAlerta("ID de Entidad, ID de Dirección y Fecha de Inicio son obligatorios", "Error", Alert.AlertType.ERROR); return;
        }

        LocalDate fechaFin = fechaFinPicker.getValue();
        if (fechaFin != null && fechaFin.isBefore(fechaInicio)) {
            mostrarAlerta("La Fecha de Fin no puede ser anterior a la Fecha de Inicio.", "Error", Alert.AlertType.ERROR); return;
        }

        RelacionDireccion relacion = new RelacionDireccion(idEntidad, idDireccion, fechaInicio, fechaFin);
        int resultado;

        resultado = manejadorRelacionDB.actualizarRelacionPaciente(relacion);

        if (resultado > 0) {
            mostrarAlerta("Relación actualizada (Fecha de Fin modificada)", "Correcto", Alert.AlertType.INFORMATION);
        } else {
            resultado = manejadorRelacionDB.insertarRelacionPaciente(relacion);

            if (resultado > 0) {
                mostrarAlerta("Nueva Relación agregada correctamente", "Correcto", Alert.AlertType.INFORMATION);
            } else {
                mostrarAlerta("No se pudo guardar (Clave duplicada o IDs no existen)", "Error", Alert.AlertType.ERROR);
            }
        }

        recargarDatos();
    }

    @FXML
    private void eliminarRelacion() {
        RelacionDireccion seleccionada = tablaRelaciones.getSelectionModel().getSelectedItem();

        if (seleccionada == null) { mostrarAlerta("Debe seleccionar una relación de la tabla para eliminar", "Aviso", Alert.AlertType.WARNING); return; }

        manejadorRelacionDB.eliminarRelacionPaciente(seleccionada.getIdEntidad(), seleccionada.getIdDireccion(), seleccionada.getFechaInicio());
        mostrarAlerta("Relación de dirección eliminada", "Correcto", Alert.AlertType.INFORMATION);
        recargarDatos();
    }

    @FXML
    private void filtrarPorEntidad() {
        int idEntidadFiltro;
        try {
            idEntidadFiltro = Integer.parseInt(idEntidadText.getText());
            if (idEntidadFiltro <= 0) throw new NumberFormatException();
        } catch (NumberFormatException e) {
            mostrarAlerta("Ingrese un ID de Entidad (Paciente/Staff) válido para filtrar", "Error", Alert.AlertType.ERROR); return;
        }

        listaObservable.setAll(manejadorRelacionDB.getRelacionesPaciente(idEntidadFiltro));

        if (listaObservable.isEmpty()) { mostrarAlerta("No se encontraron direcciones para el ID " + idEntidadFiltro, "Aviso", Alert.AlertType.INFORMATION); }
        limpiarForm();
    }

    private void cargarSeleccionado(RelacionDireccion relacion) {
        idEntidadText.setText(String.valueOf(relacion.getIdEntidad()));
        idEntidadText.setDisable(true);
        idDireccionText.setText(String.valueOf(relacion.getIdDireccion()));
        idDireccionText.setDisable(true);
        fechaInicioPicker.setValue(relacion.getFechaInicio());
        fechaInicioPicker.setDisable(true);

        fechaFinPicker.setValue(relacion.getFechaFin());
        fechaFinPicker.setDisable(false);
    }

    @FXML
    private void recargarDatos() {
        listaObservable.setAll(manejadorRelacionDB.getTodasRelacionesPaciente());
        limpiarForm();
    }

    @FXML
    public void limpiarForm() {
        idEntidadText.clear();
        idEntidadText.setDisable(false);
        idDireccionText.clear();
        idDireccionText.setDisable(false);
        fechaInicioPicker.setValue(null);
        fechaInicioPicker.setDisable(false);
        fechaFinPicker.setValue(null);
        fechaFinPicker.setDisable(false);
        tablaRelaciones.getSelectionModel().clearSelection();
    }

    @FXML
    private void probarConexión() {
        Connection conn = manejadorRelacionDB.abrirConexion();
        if (conn != null) {
            mostrarAlerta("Conexión exitosa!!", "Éxito", Alert.AlertType.INFORMATION);
            manejadorRelacionDB.cerrarConexion(conn);
        } else { mostrarAlerta("No se pudo conectar.", "Error", Alert.AlertType.ERROR); }
    }

    private void mostrarAlerta(String mensaje, String titulo, Alert.AlertType tipo) {
        Alert alerta = new Alert(tipo);
        alerta.setTitle("Aviso");
        alerta.setHeaderText(null);
        alerta.setContentText(mensaje);
        Stage stage = (Stage) tablaRelaciones.getScene().getWindow();
        if (stage != null) { alerta.initOwner(stage); }
        alerta.showAndWait();
    }
}