package com.jgm.securepasswordmanager.controllers;

import com.jgm.securepasswordmanager.datamodel.User;
import com.jgm.securepasswordmanager.services.AuthenticationService;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;

public class LoginController {

    @FXML
    private TextField userNameField;

    @FXML
    private TextField passwordField;

    @FXML
    private Button loginButton;

    private AuthenticationService theAuthenticationService;



    public void initialize() {
        theAuthenticationService = new AuthenticationService();

    }

    @FXML
    protected void handleRegisterButtonClicked(ActionEvent event) {
        try {
            // Load the FXML file for the Login view
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/jgm/securepasswordmanager/create_new_account.fxml"));
            Parent root = loader.load();

            // Optionally, get the LoginController if you need to interact with it
            CreateNewAccountController theCreateNewAccountController = loader.getController();

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
    protected void handleLoginButtonClicked() {
        String username = userNameField.getText().trim();
        String password = passwordField.getText().trim();

        User theLoadedUser = theAuthenticationService.login(username, password);

        if (theLoadedUser != null) {
            try {
                // Load the password_manager.fxml file
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/jgm/securepasswordmanager/password_manager.fxml"));
                Parent root = loader.load();

                // Retrieve the PasswordManagerController
                PasswordManagerController passwordManagerController = loader.getController();

                // Set the loaded user in the PasswordManagerController
                passwordManagerController.setUser(theLoadedUser);

                // Get the current stage (from a component in the current scene, e.g., the login button)
                Stage stage = (Stage) loginButton.getScene().getWindow();

                // Set the scene to the stage with the loaded FXML
                stage.setScene(new Scene(root));
                stage.show();

            } catch (IOException e) {
                e.printStackTrace();
                // Handle the exception, maybe show an error dialog to the user.
            }
        } else {
            System.out.println("Failed to login user");
        }
    }


    @FXML
    protected void handleForgotPasswordButtonClicked() {
        System.out.println("Forgot Password Button Clicked");
    }
}