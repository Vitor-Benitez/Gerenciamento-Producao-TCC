module com.example {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;  // Se estiver usando JDBC

    opens com.example to javafx.fxml;
    opens com.example.controllers to javafx.fxml;

    exports com.example;
    exports com.example.controllers;
    exports com.example.models;
}
