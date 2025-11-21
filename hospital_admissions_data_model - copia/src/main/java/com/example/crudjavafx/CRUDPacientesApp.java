package com.example.crudjavafx;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import java.io.IOException;

public class CRUDPacientesApp extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(LoginApplication.class.getResource("/com/example/crudjavafx/login-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 320, 240);

        primaryStage.setTitle("Login");
        primaryStage.getIcons().add(new Image(getClass().getResourceAsStream("/iconoPngDesarrollo.png"))); // Asegúrate de que la ruta sea correcta
        primaryStage.setScene(scene);
        primaryStage.show();

        LoginController controller = fxmlLoader.getController();
        controller.setPrimaryStage(primaryStage);
    }
}