package com.example.crudjavafx;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.stage.Stage;

import java.sql.Connection;
import java.time.LocalDate;

public class CRUDEstanciaController {

    @FXML private TextField idPacienteText;
    @FXML private TextField idHabitacionText;
    @FXML private DatePicker fechaInicioPicker;
    @FXML private DatePicker fechaFinPicker;

    @FXML private TableView<Estancia> tablaEstancias;
    @FXML private TableColumn<Estancia, Integer> columnaIdPaciente;
    @FXML private TableColumn<Estancia, String> columnaIdHabitacion;
    @FXML private TableColumn<Estancia, LocalDate> columnaFechaInicio;
    @FXML private TableColumn<Estancia, LocalDate> columnaFechaFin;

    private ManejadorEstanciaDB manejadorEstanciaDB;
    private ObservableList<Estancia> listaObservable;

    public void initData(String url, String user, String password) {
        manejadorEstanciaDB = new ManejadorEstanciaDB(url, user, password);
        listaObservable = FXCollections.observableArrayList(manejadorEstanciaDB.getEstanciasPS());
        tablaEstancias.setItems(listaObservable);

        tablaEstancias.getSelectionModel().selectedItemProperty().addListener((obs, oldSel, newSel)->{
            if (newSel != null) { cargarSeleccionado(newSel); }
        });

        limpiarForm();
    }

    @FXML
    public void initialize(){
        columnaIdPaciente.setCellValueFactory(new PropertyValueFactory<>("idPaciente"));
        columnaIdHabitacion.setCellValueFactory(new PropertyValueFactory<>("idHabitacion"));
        columnaFechaInicio.setCellValueFactory(new PropertyValueFactory<>("fechaInicio"));
        columnaFechaFin.setCellValueFactory(new PropertyValueFactory<>("fechaFin"));
    }

    @FXML
    private void guardarEstancia() {
        int idPaciente;
        String idHabitacion;
        LocalDate fechaInicio = fechaInicioPicker.getValue();

        try {
            idPaciente = Integer.parseInt(idPacienteText.getText());
            idHabitacion = idHabitacionText.getText();
            if (idPaciente <= 0 || idHabitacion.isEmpty() || fechaInicio == null) { throw new NumberFormatException(); }
        } catch (NumberFormatException e) {
            mostrarAlerta("ID Paciente, Habitación y Fecha de Inicio son obligatorios", "Error", Alert.AlertType.ERROR); return;
        }

        LocalDate fechaFin = fechaFinPicker.getValue();

        if (fechaFin != null && fechaFin.isBefore(fechaInicio)) {
            mostrarAlerta("La Fecha de Fin no puede ser anterior a la Fecha de Inicio.", "Error", Alert.AlertType.ERROR); return;
        }

        Estancia estancia = new Estancia(idPaciente, idHabitacion, fechaInicio, fechaFin);
        int resultado;

        resultado = manejadorEstanciaDB.actualizarPS(estancia);

        if (resultado > 0) {
            mostrarAlerta("Estancia actualizada (Fecha de Fin modificada)", "Correcto", Alert.AlertType.INFORMATION);
        } else {
            resultado = manejadorEstanciaDB.insertarPS(estancia);

            if (resultado > 0) {
                mostrarAlerta("Nueva Estancia agregada correctamente", "Correcto", Alert.AlertType.INFORMATION);
            } else {
                mostrarAlerta("No se pudo guardar la estancia (Clave duplicada o ID Paciente/Habitación inválido)", "Error", Alert.AlertType.ERROR);
            }
        }

        recargarDatos();
    }

    @FXML
    private void eliminarEstancia() {
        Estancia seleccionada = tablaEstancias.getSelectionModel().getSelectedItem();

        if (seleccionada == null) { mostrarAlerta("Debe seleccionar una estancia de la tabla para eliminar", "Aviso", Alert.AlertType.WARNING); return; }

        manejadorEstanciaDB.eliminarPS(seleccionada.getIdPaciente(), seleccionada.getIdHabitacion(), seleccionada.getFechaInicio());
        mostrarAlerta("Estancia eliminada", "Correcto", Alert.AlertType.INFORMATION);
        recargarDatos();
    }

    @FXML
    private void filtrarEstancias() {
        int idPacienteFiltro;
        try {
            idPacienteFiltro = Integer.parseInt(idPacienteText.getText());
            if (idPacienteFiltro <= 0) throw new NumberFormatException();
        } catch (NumberFormatException e) {
            mostrarAlerta("Ingrese un ID de Paciente válido para filtrar", "Error", Alert.AlertType.ERROR); return;
        }

        listaObservable.setAll(manejadorEstanciaDB.getEstanciasPorPacientePS(idPacienteFiltro));

        if (listaObservable.isEmpty()) { mostrarAlerta("No se encontraron estancias para el Paciente ID " + idPacienteFiltro, "Aviso", Alert.AlertType.INFORMATION); }
        limpiarForm();
    }

    private void cargarSeleccionado(Estancia estancia) {
        idPacienteText.setText(String.valueOf(estancia.getIdPaciente()));
        idPacienteText.setDisable(true);
        idHabitacionText.setText(estancia.getIdHabitacion());
        idHabitacionText.setDisable(true);
        fechaInicioPicker.setValue(estancia.getFechaInicio());
        fechaInicioPicker.setDisable(true);

        fechaFinPicker.setValue(estancia.getFechaFin());
        fechaFinPicker.setDisable(false);
    }

    @FXML
    private void recargarDatos() {
        listaObservable.setAll(manejadorEstanciaDB.getEstanciasPS());
        limpiarForm();
    }

    @FXML
    private void limpiarForm() {
        idPacienteText.clear();
        idPacienteText.setDisable(false);
        idHabitacionText.clear();
        idHabitacionText.setDisable(false);
        fechaInicioPicker.setValue(null);
        fechaInicioPicker.setDisable(false);
        fechaFinPicker.setValue(null);
        fechaFinPicker.setDisable(false);
        tablaEstancias.getSelectionModel().clearSelection();
    }

    private void mostrarAlerta(String mensaje, String titulo, Alert.AlertType tipo) {
        Alert alerta = new Alert(tipo);
        alerta.setTitle("Aviso");
        alerta.setHeaderText(null);
        alerta.setContentText(mensaje);
        Stage stage = (Stage) tablaEstancias.getScene().getWindow();
        if (stage != null) { alerta.initOwner(stage); }
        alerta.showAndWait();
    }
}