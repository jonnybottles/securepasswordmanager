package com.jgm.securepasswordmanager.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

public class CreateNewAccountController {

    @FXML
    private TextField firstNameField;

    @FXML
    private TextField lastNameField;

    @FXML
    private TextField emailAddressField;

    @FXML
    private TextField usernameField;

    @FXML
    private TextField passwordField;

    @FXML
    private TextField confirmPasswordField;


    @FXML
    private void handleCancelButtonClicked() {
        System.out.println("Cancel Button Clicked");
    }

    @FXML
    private void handleRegisterButtonClicked() {
        System.out.println("Register Button Clicked");

    }


}
