package com.example.crudjavafx;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.stage.Stage;

import java.sql.Connection;

public class CRUDDireccionController {

    @FXML private TextField idText;
    @FXML private TextField linea1Text;
    @FXML private TextField linea2Text;
    @FXML private TextField ciudadText;
    @FXML private TextField codigoPostalText;
    @FXML private TextField paisText;
    @FXML private CheckBox principalCheckBox;

    @FXML private TableView<Direccion> tablaDirecciones;
    @FXML private TableColumn<Direccion, Integer> columnaId;
    @FXML private TableColumn<Direccion, String> columnaLinea1;
    @FXML private TableColumn<Direccion, String> columnaLinea2;
    @FXML private TableColumn<Direccion, String> columnaCiudad;
    @FXML private TableColumn<Direccion, String> columnaPostal;
    @FXML private TableColumn<Direccion, String> columnaPais;
    @FXML private TableColumn<Direccion, Boolean> columnaPrincipal;

    private ManejadorDireccionDB manejadorDireccionDB;
    private ObservableList<Direccion> listaObservable;

    // Metodo llamado al abrir la ventana
    public void initData(String url, String user, String password) {
        manejadorDireccionDB = new ManejadorDireccionDB(url, user, password);
        listaObservable = FXCollections.observableArrayList(manejadorDireccionDB.getDireccionesPS());
        tablaDirecciones.setItems(listaObservable);

        tablaDirecciones.getSelectionModel().selectedItemProperty().addListener((obs, oldSel, newSel)->{
            if (newSel != null) {
                cargarSeleccionado(newSel);
            }
        });

        limpiarForm();
    }

    @FXML
    public void initialize(){
        columnaId.setCellValueFactory(new PropertyValueFactory<>("id"));
        columnaLinea1.setCellValueFactory(new PropertyValueFactory<>("linea1"));
        columnaLinea2.setCellValueFactory(new PropertyValueFactory<>("linea2"));
        columnaCiudad.setCellValueFactory(new PropertyValueFactory<>("ciudad"));
        columnaPostal.setCellValueFactory(new PropertyValueFactory<>("codigoPostal"));
        columnaPais.setCellValueFactory(new PropertyValueFactory<>("pais"));
        columnaPrincipal.setCellValueFactory(new PropertyValueFactory<>("esPrincipal"));
    }

    @FXML
    private void guardarDireccion() {
        String linea1 = linea1Text.getText();
        String linea2 = linea2Text.getText();
        String ciudad = ciudadText.getText();
        String codigoPostal = codigoPostalText.getText();
        String pais = paisText.getText();
        boolean esPrincipal = principalCheckBox.isSelected();

        if (linea1.isEmpty() || ciudad.isEmpty() || codigoPostal.isEmpty() || pais.isEmpty()) {
            mostrarAlerta("Línea 1, Ciudad, Código Postal y País son obligatorios", "Error", Alert.AlertType.ERROR);
            return;
        }

        int idGuardar = Integer.parseInt(idText.getText());
        Direccion direccion = new Direccion(idGuardar, linea1, linea2, ciudad, codigoPostal, pais, esPrincipal);

        int resultado;
        if (idGuardar == 0) {
            resultado = manejadorDireccionDB.insertarPS(direccion);
            if (resultado > 0) {
                mostrarAlerta("Dirección agregada correctamente. ID: " + resultado, "Correcto", Alert.AlertType.INFORMATION);
            } else {
                mostrarAlerta("No se pudo agregar la dirección", "Error", Alert.AlertType.ERROR);
            }
        } else {
            resultado = manejadorDireccionDB.actualizarPS(direccion);
            if (resultado > 0) {
                mostrarAlerta("Dirección actualizada correctamente", "Correcto", Alert.AlertType.INFORMATION);
            } else {
                mostrarAlerta("No se pudo actualizar la dirección", "Error", Alert.AlertType.ERROR);
            }
        }
        recargarDatos();
    }

    @FXML
    private void eliminarDireccion() {
        int idEliminar;
        try {
            idEliminar = Integer.parseInt(idText.getText());
        } catch (NumberFormatException e) {
            mostrarAlerta("Seleccione un ID válido para eliminar", "Aviso", Alert.AlertType.WARNING);
            return;
        }

        if (idEliminar > 0) {
            manejadorDireccionDB.eliminarPS(idEliminar);
            mostrarAlerta("Dirección eliminada (si el ID existía)", "Correcto", Alert.AlertType.INFORMATION);
            recargarDatos();
        } else {
            mostrarAlerta("Seleccione un ID válido ", "Aviso", Alert.AlertType.INFORMATION);
        }
    }

    @FXML
    private void filtrarDirecciones() {
        String linea1Filtro = linea1Text.getText().isEmpty() ? null : linea1Text.getText();
        String linea2Filtro = linea2Text.getText().isEmpty() ? null : linea2Text.getText();
        String ciudadFiltro = ciudadText.getText().isEmpty() ? null : ciudadText.getText();
        String codigoPostalFiltro = codigoPostalText.getText().isEmpty() ? null : codigoPostalText.getText();
        String paisFiltro = paisText.getText().isEmpty() ? null : paisText.getText();

        listaObservable.setAll(manejadorDireccionDB.getDireccionesPorFiltroPS(
                linea1Filtro,
                linea2Filtro,
                ciudadFiltro,
                codigoPostalFiltro,
                paisFiltro
        ));

        if (listaObservable.isEmpty()) {
            mostrarAlerta("No se encontraron direcciones con los filtros proporcionados", "Aviso", Alert.AlertType.INFORMATION);
        }
        limpiarForm();
    }

    @FXML
    private void cargarSeleccionado(Direccion direccion) {
        idText.setText(String.valueOf(direccion.getId()));
        linea1Text.setText(direccion.getLinea1());
        linea2Text.setText(direccion.getLinea2());
        ciudadText.setText(direccion.getCiudad());
        codigoPostalText.setText(direccion.getCodigoPostal());
        paisText.setText(direccion.getPais());
        principalCheckBox.setSelected(direccion.isEsPrincipal());
    }

    @FXML
    private void recargarDatos() {
        listaObservable.setAll(manejadorDireccionDB.getDireccionesPS());
        limpiarForm();
    }

    private void limpiarForm() {
        idText.setText(String.valueOf(0));
        linea1Text.clear();
        linea2Text.clear();
        ciudadText.clear();
        codigoPostalText.clear();
        paisText.clear();
        principalCheckBox.setSelected(false);
        tablaDirecciones.getSelectionModel().clearSelection();
    }

    @FXML
    private void probarConexión() {
        Connection conn = manejadorDireccionDB.abrirConexion();

        if (conn != null) {
            mostrarAlerta("Conexión exitosa!!", "Éxito",Alert.AlertType.INFORMATION);
            manejadorDireccionDB.cerrarConexion(conn);
            mostrarAlerta("Se cerró la conexión", "Éxito",Alert.AlertType.INFORMATION);
        } else {
            mostrarAlerta("No se pudo conectar a la base de datos", "Error",Alert.AlertType.ERROR);
        }
    }

    private void mostrarAlerta(String mensaje, String titulo, Alert.AlertType tipo) {
        Alert alerta = new Alert(tipo);
        alerta.setTitle("Aviso");
        alerta.setHeaderText(null);
        alerta.setContentText(mensaje);

        Stage stage = (Stage) tablaDirecciones.getScene().getWindow();
        if (stage != null) {
            alerta.initOwner(stage);
        }

        alerta.showAndWait();
    }
}
