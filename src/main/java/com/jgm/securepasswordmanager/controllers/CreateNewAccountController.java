package com.jgm.securepasswordmanager.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

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
    private void handleCancelButtonClicked(ActionEvent event) {
        try {
            // Load the FXML file for the Login view
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/jgm/securepasswordmanager/login.fxml"));
            Parent root = loader.load();

            // Optionally, get the LoginController if you need to interact with it
            LoginController loginController = loader.getController();

            // Get the current stage (window) using the event's source
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();

            // Set the scene to the stage with the loaded FXML
            Scene scene = new Scene(root);
            stage.setScene(scene);

            // Optional: Set stage properties or show dialogs if needed
            stage.setTitle("Secure Password Manager Login");
            stage.show();

        } catch (Exception e) {
            e.printStackTrace(); // Handle the exception in a way that's appropriate for your application
        }
    }

    @FXML
    private void handleRegisterButtonClicked() {
        System.out.println("Register Button Clicked");

    }


}
