package com.jgm.securepasswordmanager.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class PasswordManagerController {
    @FXML
    private Label welcomeText;

    @FXML
    protected void onHelloButtonClick() {
        welcomeText.setText("Welcome to JavaFX Application!");
    }
}