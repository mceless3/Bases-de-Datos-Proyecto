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

public class CRUDPacientesController {

    @FXML private TextField idText;
    @FXML private TextField nombreText;
    @FXML private TextField apellidoText;
    @FXML private TextField hospitalNumberText;
    @FXML private TextField weightText;
    @FXML private DatePicker fechaNacimientoPicker;
    @FXML private ComboBox<String> generoComboBox;

    @FXML private TableView<Paciente> tablaPacientes;
    @FXML private TableColumn<Paciente, Integer> columnaId;
    @FXML private TableColumn<Paciente, String> columnaNombre;
    @FXML private TableColumn<Paciente, String> columnaApellido;
    @FXML private TableColumn<Paciente, String> columnaNumHospital;
    @FXML private TableColumn<Paciente, Double> columnaPeso;
    @FXML private TableColumn<Paciente, LocalDate> columnaFechaNac;
    @FXML private TableColumn<Paciente, String> columnaGenero;

    private ManejadorPacienteDB manejadorPacientesDB;
    private ObservableList<Paciente> listaObservable;
    private final ObservableList<String> generos = FXCollections.observableArrayList("M", "F", "O");

    public void initData(String url, String user, String password) {
        manejadorPacientesDB = new ManejadorPacienteDB(url, user, password);
        listaObservable = FXCollections.observableArrayList(manejadorPacientesDB.getPacientesPS());
        tablaPacientes.setItems(listaObservable);

        if (generoComboBox != null) {
            generoComboBox.setItems(generos);
        }

        tablaPacientes.getSelectionModel().selectedItemProperty().addListener((obs, oldSel, newSel)->{
            if (newSel != null) { cargarSeleccionado(newSel); }
        });

        limpiarForm();
    }

    @FXML
    public void initialize(){
        columnaId.setCellValueFactory(new PropertyValueFactory<>("id"));
        columnaNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        columnaApellido.setCellValueFactory(new PropertyValueFactory<>("apellido"));
        columnaNumHospital.setCellValueFactory(new PropertyValueFactory<>("hospitalNumber"));
        columnaPeso.setCellValueFactory(new PropertyValueFactory<>("peso"));
        columnaFechaNac.setCellValueFactory(new PropertyValueFactory<>("fechaNacimiento"));
        columnaGenero.setCellValueFactory(new PropertyValueFactory<>("genero"));
    }

    @FXML
    private void guardarPaciente() {
        String nombre = nombreText.getText(), apellido = apellidoText.getText(), hospitalNumber = hospitalNumberText.getText();
        String pesoStr = weightText.getText();
        LocalDate fechaNac = fechaNacimientoPicker.getValue();
        String genero = generoComboBox.getValue();

        if (nombre.isEmpty() || apellido.isEmpty() || hospitalNumber.isEmpty() || fechaNac == null || genero == null || genero.isEmpty()) {
            mostrarAlerta("Nombre, Apellido, Nro. Hospital, Fecha y Género son obligatorios", "Error", Alert.AlertType.ERROR);
            return;
        }

        double peso;
        try {
            peso = Double.parseDouble(pesoStr);
        } catch (NumberFormatException e) {
            mostrarAlerta("El peso debe ser un número válido", "Error", Alert.AlertType.ERROR);
            return;
        }

        int idGuardar = Integer.parseInt(idText.getText());
        Paciente paciente = new Paciente(idGuardar, nombre, apellido, hospitalNumber, peso, fechaNac, genero);

        int resultado = (idGuardar == 0) ? manejadorPacientesDB.insertarPS(paciente) : manejadorPacientesDB.actualizarPS(paciente);

        if (resultado > 0) {
            mostrarAlerta((idGuardar == 0) ? "Paciente agregado correctamente. ID: " + resultado : "Paciente actualizado correctamente.", "Correcto", Alert.AlertType.INFORMATION);
        } else {
            mostrarAlerta("No se pudo guardar el paciente.", "Error", Alert.AlertType.ERROR);
        }
        recargarDatos();
    }

    @FXML
    private void eliminarPaciente() {
        int idEliminar;
        try { idEliminar = Integer.parseInt(idText.getText()); }
        catch (NumberFormatException e) { mostrarAlerta("Seleccione un ID válido para eliminar", "Aviso", Alert.AlertType.WARNING); return; }

        if (idEliminar > 0) {
            manejadorPacientesDB.eliminarPS(idEliminar);
            mostrarAlerta("Paciente eliminado (si el ID existía)", "Correcto", Alert.AlertType.INFORMATION);
            recargarDatos();
        }
    }

    @FXML
    private void filtrarPacientes(){
        String nombreFiltro = nombreText.getText().isEmpty() ? null : nombreText.getText();
        String apellidoFiltro = apellidoText.getText().isEmpty() ? null : apellidoText.getText();
        String hospitalNumberFiltro = hospitalNumberText.getText().isEmpty() ? null : hospitalNumberText.getText();
        String generoFiltro = generoComboBox.getValue();

        LocalDate fechaNacFiltro = fechaNacimientoPicker.getValue();
        Double pesoFiltro = null;

        if (!weightText.getText().isEmpty()) {
            try { pesoFiltro = Double.parseDouble(weightText.getText()); }
            catch (NumberFormatException e) { mostrarAlerta("El peso debe ser un número válido ", "Error", Alert.AlertType.ERROR); return; }
        }

        listaObservable.setAll(manejadorPacientesDB.getPacientesPorFiltroPS(nombreFiltro, apellidoFiltro, hospitalNumberFiltro, pesoFiltro, fechaNacFiltro, generoFiltro));

        if (listaObservable.isEmpty()) {
            mostrarAlerta("No se encontraron pacientes con los filtros proporcionados", "Aviso", Alert.AlertType.INFORMATION);
        }
        limpiarForm();
    }

    private void cargarSeleccionado(Paciente paciente) {
        idText.setText(String.valueOf(paciente.getId()));
        nombreText.setText(paciente.getNombre());
        apellidoText.setText(paciente.getApellido());
        hospitalNumberText.setText(paciente.getHospitalNumber());
        weightText.setText(String.valueOf(paciente.getPeso()));
        fechaNacimientoPicker.setValue(paciente.getFechaNacimiento());
        generoComboBox.setValue(paciente.getGenero());
    }

    @FXML
    private void recargarDatos() {
        listaObservable.setAll(manejadorPacientesDB.getPacientesPS());
        limpiarForm();
    }

    @FXML
    private void limpiarForm() {
        idText.setText(String.valueOf(0));
        nombreText.clear();
        apellidoText.clear();
        hospitalNumberText.clear();
        weightText.clear();
        fechaNacimientoPicker.setValue(null);
        generoComboBox.setValue(null);
        tablaPacientes.getSelectionModel().clearSelection();
    }

    @FXML
    private void probarConexión() {
        Connection conn = manejadorPacientesDB.abrirConexion();
        if (conn != null) {
            mostrarAlerta("Conexión exitosa!!", "Éxito", Alert.AlertType.INFORMATION);
            manejadorPacientesDB.cerrarConexion(conn);
            mostrarAlerta("Se cerró la conexión", "Éxito", Alert.AlertType.INFORMATION);
        } else {
            mostrarAlerta("No se pudo conectar a la base de datos", "Error", Alert.AlertType.ERROR);
        }
    }

    private void mostrarAlerta(String mensaje, String titulo, Alert.AlertType tipo) {
        Alert alerta = new Alert(tipo);
        alerta.setTitle("Aviso");
        alerta.setHeaderText(null);
        alerta.setContentText(mensaje);
        Stage stage = (Stage) tablaPacientes.getScene().getWindow();
        if (stage != null) { alerta.initOwner(stage); }
        alerta.showAndWait();
    }
}