package com.example.crudjavafx;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.IOException;

public class LoginApplication extends Application {
    @Override
    public void start(Stage primaryStage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(LoginApplication.class.getResource("/com/example/crudjavafx/login-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 500, 350);
        primaryStage.setTitle("Login a la Base de Datos del Hospital");
        primaryStage.getIcons().add(new Image(getClass().getResourceAsStream("/iconoBD.png")));
        primaryStage.setScene(scene);
        primaryStage.show();

        LoginController controller = fxmlLoader.getController();
        controller.setPrimaryStage(primaryStage);
    }

    public static void main(String[] args) {
        launch();
    }
}