module com.example.crudjavafx {
    requires javafx.controls;
    requires javafx.fxml;

    requires java.sql;
    requires javafx.graphics;

    opens com.example.crudjavafx to javafx.fxml;
    exports com.example.crudjavafx;
}