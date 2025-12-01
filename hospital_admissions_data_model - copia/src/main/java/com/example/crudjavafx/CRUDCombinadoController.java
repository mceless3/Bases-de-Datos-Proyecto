package com.example.crudjavafx;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;

public class CRUDCombinadoController {

    @FXML private CRUDFacturaController moduloFacturasController;
    @FXML private CRUDItemFacturaController moduloItemsController;

    public void initData(String url, String user, String password) {
        if (moduloFacturasController != null && moduloItemsController != null) {
            moduloFacturasController.initData(url, user, password);
            moduloItemsController.initData(url, user, password);
        } else {
            System.out.println("Error: Los controladores internos son nulos.");
        }
    }
}