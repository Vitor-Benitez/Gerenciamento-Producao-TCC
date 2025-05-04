package com.example.utils;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.util.Optional;

public class AlertUtils {
    public static void showAlert(AlertType type, String titulo, String mensagem) {
        Alert alert = new Alert(type.getType());
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensagem);
        
        // Adicionar ícone personalizado
        Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();
        try {
            stage.getIcons().add(new Image(AlertUtils.class.getResourceAsStream("/" + type.getIconPath())));
        } catch (Exception e) {
            // Log de erro se não encontrar ícone
            logError(e);
        }
        
        alert.showAndWait();
    }

    public static boolean showConfirmation(String titulo, String mensagem) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensagem);
        
        Optional<ButtonType> result = alert.showAndWait();
        return result.isPresent() && result.get() == ButtonType.OK;
    }

    public static void logError(Exception e) {
        try (PrintWriter writer = new PrintWriter(new FileWriter("error.log", true))) {
            writer.println(LocalDateTime.now() + " - " + e.getMessage());
            e.printStackTrace(writer);
        } catch (IOException ioException) {
            ioException.printStackTrace();
        }
    }
}
