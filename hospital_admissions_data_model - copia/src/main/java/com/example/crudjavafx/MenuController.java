package com.example.crudjavafx;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Alert;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import java.io.IOException;

public class MenuController {

    private Stage menuStage;
    private String urlConexion;
    private String usuario;
    private String password;

    private final String FXML_PACIENTES = "CRUDPacientes-view.fxml";
    private final String FXML_PERSONAL = "CRUDPersonal-view.fxml";
    private final String FXML_DIRECCIONES = "CRUDDireccion-view.fxml";
    private final String FXML_FACTURAS = "CombinacionFacturas.fxml";
    private final String FXML_COMPONENTES = "CRUDComponente-view.fxml";
    private final String FXML_ESTANCIAS = "CRUDEstancia-view.fxml";
    private final String FXML_RELACIONES_DIR = "CRUDRelacionDireccion-view.fxml";

    // Metodo llamado por LoginController para inicializar el menú
    public void initData(Stage stage, String url, String user, String password) {
        this.menuStage = stage;
        this.urlConexion = url;
        this.usuario = user;
        this.password = password;
        menuStage.setTitle("Menú Principal - Sistema Hospitalario");
    }

    @FXML
    private void abrirCRUD(javafx.event.ActionEvent event) {
        Button sourceButton = (Button) event.getSource();
        String fxmlFile = null;
        String title = null;

        if (sourceButton.getId().equals("btnPacientes")) {
            fxmlFile = FXML_PACIENTES;
            title = "Módulo de Pacientes";
        } else if (sourceButton.getId().equals("btnPersonal")) {
            fxmlFile = FXML_PERSONAL;
            title = "Módulo de Staff/Personal";
        } else if (sourceButton.getId().equals("btnDirecciones")) {
            fxmlFile = FXML_DIRECCIONES;
            title = "Módulo de Direcciones";
        } else if (sourceButton.getId().equals("btnFacturas")) {
            fxmlFile = FXML_FACTURAS;
            title = "Módulo de Facturación";
        } else if (sourceButton.getId().equals("btnComponentes")) {
            fxmlFile = FXML_COMPONENTES;
            title = "Catálogo de Componentes";
        } else if (sourceButton.getId().equals("btnEstancias")) {
            fxmlFile = FXML_ESTANCIAS;
            title = "Gestión de Estancias (Rooms)";
        } else if (sourceButton.getId().equals("btnRelacionesDir")) {
            fxmlFile = FXML_RELACIONES_DIR;
            title = "Gestión de Historial de Direcciones";
        }

        if (fxmlFile != null) {
            abrirVentana(fxmlFile, title);
        }
    }

    // Metodo para cargar cualquier FXML y pasar la conexión
    private void abrirVentana(String fxmlFile, String title) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(fxmlFile));
            Stage crudStage = new Stage();

            crudStage.setScene(new Scene(fxmlLoader.load()));
            crudStage.setTitle(title);

            Object controller = fxmlLoader.getController();

            switch (fxmlFile) {
                case FXML_PACIENTES:
                    ((CRUDPacientesController) controller).initData(urlConexion, usuario, password);
                    break;
                case FXML_PERSONAL:
                    ((CRUDPersonalController) controller).initData(urlConexion, usuario, password);
                    break;
                case FXML_DIRECCIONES:
                    ((CRUDDireccionController) controller).initData(urlConexion, usuario, password);
                    break;
                case FXML_FACTURAS:
                    ((CRUDCombinadoController) controller).initData(urlConexion, usuario, password);                    break;
                case FXML_COMPONENTES:
                    ((CRUDComponenteController) controller).initData(urlConexion, usuario, password);
                    break;
                case FXML_ESTANCIAS:
                    ((CRUDEstanciaController) controller).initData(urlConexion, usuario, password);
                    break;
                case FXML_RELACIONES_DIR:
                    ((CRUDRelacionDireccionController) controller).initData(urlConexion, usuario, password);
                    break;
                default:
                    System.out.println("Error: Controlador no reconocido para el archivo " + fxmlFile);
            }

            crudStage.setOnCloseRequest((WindowEvent event) -> {
                menuStage.show();
            });
            menuStage.hide();
            crudStage.show();

        } catch (IOException e) {
            e.printStackTrace();
            mostrarAlerta("Error al cargar la ventana: " + title + ". Verifique el nombre del FXML: " + fxmlFile, "Error de Carga", Alert.AlertType.ERROR);
        }
    }

    private void mostrarAlerta(String mensaje, String titulo, Alert.AlertType tipo) {
        Alert alerta = new Alert(tipo);
        alerta.setTitle("Aviso");
        alerta.setHeaderText(null);
        alerta.setContentText(mensaje);
        alerta.initOwner(menuStage);
        alerta.showAndWait();
    }
}