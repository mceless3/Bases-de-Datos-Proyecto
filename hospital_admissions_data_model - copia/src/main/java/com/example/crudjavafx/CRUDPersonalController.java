package com.example.crudjavafx;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.stage.Stage;

import java.sql.Connection;
import java.time.LocalDate;
import java.util.ArrayList;

public class CRUDPersonalController {

    @FXML private TextField nombreText;
    @FXML private TextField apellidoText;
    @FXML private TextField puestoText;
    @FXML private DatePicker fechaNacimientoPicker;
    @FXML private ComboBox<String> categoriaComboBox;
    @FXML private ComboBox<String> generoComboBox;
    @FXML private ComboBox<String> estadoActivoComboBox;
    @FXML private CheckBox activoCheckBox;

    @FXML private TableView<Personal> tablaPersonal;
    @FXML private TableColumn<Personal, Integer> columnaId;
    @FXML private TableColumn<Personal, String> columnaNombre;
    @FXML private TableColumn<Personal, String> columnaApellido;
    @FXML private TableColumn<Personal, String> columnaPuesto;
    @FXML private TableColumn<Personal, String> columnaCategoria;
    @FXML private TableColumn<Personal, String> columnaGenero;
    @FXML private TableColumn<Personal, LocalDate> columnaFechaNac;
    @FXML private TableColumn<Personal, Boolean> columnaActivo;

    private ManejadorPersonalDB manejadorPersonalDB;
    private ObservableList<Personal> listaObservable;
    private final ObservableList<String> generos = FXCollections.observableArrayList("M", "F", "O");


    public void initData(String url, String user, String password) {
        manejadorPersonalDB = new ManejadorPersonalDB(url, user, password);

        if (categoriaComboBox != null) {
            categoriaComboBox.setItems(FXCollections.observableArrayList(manejadorPersonalDB.obtenerCategorias()));
        }

        if (generoComboBox != null) {
            generoComboBox.setItems(generos);
        }

        if (estadoActivoComboBox != null) {
            ObservableList<String> estados = FXCollections.observableArrayList("Todos", "Activo", "Inactivo");
            estadoActivoComboBox.setItems(estados);
            estadoActivoComboBox.setValue("Todos");
        }

        listaObservable = FXCollections.observableArrayList(manejadorPersonalDB.getPersonalPS());
        tablaPersonal.setItems(listaObservable);

        tablaPersonal.getSelectionModel().selectedItemProperty().addListener((obs, oldSel, newSel)->{
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
        columnaId.setCellValueFactory(new PropertyValueFactory<>("id"));
        columnaNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        columnaApellido.setCellValueFactory(new PropertyValueFactory<>("apellido"));
        columnaPuesto.setCellValueFactory(new PropertyValueFactory<>("puesto"));
        columnaCategoria.setCellValueFactory(new PropertyValueFactory<>("categoria"));
        columnaGenero.setCellValueFactory(new PropertyValueFactory<>("genero"));
        columnaFechaNac.setCellValueFactory(new PropertyValueFactory<>("fechaNacimiento"));
        columnaActivo.setCellValueFactory(new PropertyValueFactory<>("activo"));
    }

    @FXML
    private void guardarPersonal() {
        String nombre = nombreText.getText(), apellido = apellidoText.getText(), puesto = puestoText.getText();
        String categoria = categoriaComboBox.getValue(), genero = generoComboBox.getValue();
        LocalDate fechaNac = fechaNacimientoPicker.getValue();
        boolean activo = activoCheckBox.isSelected();

        if (nombre.isEmpty() || apellido.isEmpty() || puesto.isEmpty() || categoria == null || fechaNac == null || genero == null) {
            mostrarAlerta("Todos los campos marcados son obligatorios.", "Error", Alert.AlertType.ERROR); return;
        }

        Personal seleccionado = tablaPersonal.getSelectionModel().getSelectedItem();
        int idGuardar = (seleccionado != null) ? seleccionado.getId() : 0;

        Personal personal = new Personal(idGuardar, nombre, apellido, puesto, categoria, genero, fechaNac, activo);

        int resultado;

        if (idGuardar != 0) {
            resultado = manejadorPersonalDB.actualizarPS(personal);
            mostrarAlerta((resultado > 0) ? "Personal actualizado correctamente." : "No se pudo actualizar.", "Correcto", Alert.AlertType.INFORMATION);
        } else {
            resultado = manejadorPersonalDB.insertarPS(personal);
            mostrarAlerta((resultado > 0) ? "Personal agregado correctamente. ID: " + resultado : "No se pudo agregar.", "Correcto", Alert.AlertType.INFORMATION);
        }

        recargarDatos();
    }

    @FXML
    private void eliminarPersonal() {
        Personal seleccionado = tablaPersonal.getSelectionModel().getSelectedItem();

        if (seleccionado == null) {
            mostrarAlerta("Seleccione un personal de la tabla para eliminar", "Aviso", Alert.AlertType.WARNING);
            return;
        }

        int idEliminar = seleccionado.getId();

        if (idEliminar > 0) {
            manejadorPersonalDB.eliminarPS(idEliminar);
            mostrarAlerta("Personal eliminado (ID: " + idEliminar + ")", "Correcto", Alert.AlertType.INFORMATION);
            recargarDatos();
        }
    }

    @FXML
    private void filtrarPersonal() {
        String nombreFiltro = nombreText.getText().isEmpty() ? null : nombreText.getText();
        String apellidoFiltro = apellidoText.getText().isEmpty() ? null : apellidoText.getText();
        String puestoFiltro = puestoText.getText().isEmpty() ? null : puestoText.getText();
        String categoriaFiltro = categoriaComboBox.getValue();
        String generoFiltro = generoComboBox.getValue();
        LocalDate fechaNacFiltro = fechaNacimientoPicker.getValue();
        String estadoSeleccionado = estadoActivoComboBox.getValue();
        Boolean activoFiltro = null;

        if (estadoSeleccionado != null && !estadoSeleccionado.equals("Todos")) {
            if (estadoSeleccionado.equals("Activo")) {
                activoFiltro = true;
            } else if (estadoSeleccionado.equals("Inactivo")) {
                activoFiltro = false;
            }
        }

        listaObservable.setAll(manejadorPersonalDB.getPersonalPorFiltroPS(nombreFiltro, apellidoFiltro, puestoFiltro, categoriaFiltro, fechaNacFiltro, generoFiltro, activoFiltro));

        if (listaObservable.isEmpty()) {
            mostrarAlerta("No se encontró personal con los filtros proporcionados", "Aviso", Alert.AlertType.INFORMATION);
        }
        limpiarForm();
    }

    private void cargarSeleccionado(Personal personal) {
        nombreText.setText(personal.getNombre());
        apellidoText.setText(personal.getApellido());
        puestoText.setText(personal.getPuesto());
        fechaNacimientoPicker.setValue(personal.getFechaNacimiento());
        categoriaComboBox.setValue(personal.getCategoria());
        generoComboBox.setValue(personal.getGenero());
        activoCheckBox.setSelected(personal.isActivo());
    }

    @FXML
    private void recargarDatos() {
        listaObservable.setAll(manejadorPersonalDB.getPersonalPS());
        limpiarForm();
    }

    @FXML
    private void limpiarForm() {
        nombreText.clear();
        apellidoText.clear();
        puestoText.clear();
        fechaNacimientoPicker.setValue(null);
        categoriaComboBox.setValue(null);
        generoComboBox.setValue(null);
        activoCheckBox.setSelected(true);
        estadoActivoComboBox.setValue("Todos");
        tablaPersonal.getSelectionModel().clearSelection();
    }

    private void mostrarAlerta(String mensaje, String titulo, Alert.AlertType tipo) {
        Alert alerta = new Alert(tipo);
        alerta.setTitle(titulo);
        alerta.setHeaderText(null);
        alerta.setContentText(mensaje);
        Stage stage = (Stage) tablaPersonal.getScene().getWindow();
        if (stage != null) { alerta.initOwner(stage); }
        alerta.showAndWait();
    }
}