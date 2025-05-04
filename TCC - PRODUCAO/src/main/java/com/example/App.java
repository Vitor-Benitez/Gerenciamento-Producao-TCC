package com.example;

import java.io.IOException;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class App extends Application {

    @SuppressWarnings("exports")
    @Override
    public void start(Stage primaryStage) {
        try {
            // Carrega o FXML da tela inicial
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("/fxml/Tela.fxml"));
            Parent root = loader.load();
            
            // Configura a cena
            Scene scene = new Scene(root);
            
            // Configura o palco (janela)
            primaryStage.setScene(scene);
            primaryStage.setTitle("Sistema de Gestão Industrial");
            primaryStage.setWidth(1520);
            primaryStage.setHeight(800);
            primaryStage.centerOnScreen();

            // Mostra a janela
            primaryStage.show();

        } catch(IOException e) {
            // Adicione um log ou mensagem de erro mais detalhada
            System.err.println("Erro ao carregar FXML: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
