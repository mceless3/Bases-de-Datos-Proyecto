package com.example.crudjavafx;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class LoginController implements Initializable {

    private Stage primaryStage;

    @FXML private TextField txtUrl;
    @FXML private TextField txtUsuario;
    @FXML private PasswordField txtPassword;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        txtUrl.setText("jdbc:postgresql://localhost:5432/hospital_db");
    }

    public void setPrimaryStage(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }

    @FXML
    protected void ingresar(){
        String urlConexion = txtUrl.getText();
        String usuario = txtUsuario.getText();
        String password = txtPassword.getText();

        if (usuario.isEmpty() || password.isEmpty() || urlConexion.isEmpty()) {
            mostrarAlerta("Todos los campos deben llenarse", "Datos incompletos", Alert.AlertType.INFORMATION);
            return;
        }

        try {
            ManejadorPacienteDB test = new ManejadorPacienteDB(urlConexion, usuario, password);
            if (!test.probarConexion()) {
                mostrarAlerta("No se pudo conectar a la base de datos. \n Verifica la red o los datos de conexión.", "Error de Conexión", Alert.AlertType.ERROR);
                return;
            }

            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("Menu-view.fxml"));
            Scene mainScene = new Scene(fxmlLoader.load(), 500, 600);

            MenuController menuController = fxmlLoader.getController();
            Stage menuStage = new Stage();
            menuStage.setScene(mainScene);

            menuController.initData(menuStage, urlConexion, usuario, password);

            menuStage.setX(primaryStage.getX());
            menuStage.setY(primaryStage.getY());

            menuStage.show();
            primaryStage.hide();

        } catch (IOException e) {
            e.printStackTrace();
            mostrarAlerta("Error al cargar la interfaz principal.", "Error de Aplicación", Alert.AlertType.ERROR);
        }
    }

    private void mostrarAlerta(String mensaje, String titulo, Alert.AlertType tipo) {
        Alert alerta = new Alert(tipo);
        alerta.setTitle("Aviso");
        alerta.setHeaderText(null);
        alerta.setContentText(mensaje);

        Stage stage = (Stage) primaryStage;
        if (stage != null) {
            alerta.initOwner(stage);
        }
    }
}