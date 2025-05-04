package com.example.utils;

import javafx.scene.control.Alert;

public enum AlertType {
    INFORMATIVO(Alert.AlertType.INFORMATION, "info.png"),
    SUCESSO(Alert.AlertType.CONFIRMATION, "success.png"),
    AVISO(Alert.AlertType.WARNING, "warning.png"),
    ERRO(Alert.AlertType.ERROR, "error.png"),
    CONFIRMACAO(Alert.AlertType.CONFIRMATION, "confirm.png");

    private final Alert.AlertType type;
    private final String iconPath;

    AlertType(Alert.AlertType type, String iconPath) {
        this.type = type;
        this.iconPath = iconPath;
    }

    public Alert.AlertType getType() {
        return type;
    }

    public String getIconPath() {
        return iconPath;
    }
}
