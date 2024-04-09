// Package declaration aligns with the application's package structure.
package com.jgm.securepasswordmanager.controllers;

// Import statements for required classes and packages.
import com.jgm.securepasswordmanager.datamodel.WebsiteCredential;
import com.jgm.securepasswordmanager.services.PasswordGeneratorService;
import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

// Controller class for the "Add Password" view in the Secure Password Manager application.
public class AddPasswordController {

    // FXML annotations to bind UI components from the FXML file.
    @FXML
    private TextField websiteNameField;

    @FXML
    private TextField websiteUserNameField;

    @FXML
    private TextField webSitePasswordField;

    @FXML
    private ComboBox<Integer> lengthComboBox;
    @FXML
    private ComboBox<Integer> upperCaseComboBox;
    @FXML
    private ComboBox<Integer> lowerCaseComboBox;
    @FXML
    private ComboBox<Integer> numbersComboBox;
    @FXML
    private ComboBox<Integer> specialCharsComboBox;

    @FXML
    private TextArea notesArea;

    // Method to initialize the controller. It's called after the FXML fields are injected.
    @FXML
    public void initialize() {
        initializeComboBoxes(); // Initializes combo box values.
        addListenersToComboBoxes(); // Adds listeners to combo boxes for dynamic behavior.
    }

    // Event handler for the "Generate Password" button click.
    @FXML
    public void handleGeneratePasswordButtonClicked() {
        // Retrieves values from combo boxes to use in password generation.
        int length = lengthComboBox.getValue();
        int upper = upperCaseComboBox.getValue();
        int lower = lowerCaseComboBox.getValue();
        int numbers = numbersComboBox.getValue();
        int specialChars = specialCharsComboBox.getValue();
        // Generates a password based on the specified criteria.
        String generatedPassword = PasswordGeneratorService.generatePassword(length, upper, lower, numbers, specialChars);
        webSitePasswordField.setText(generatedPassword); // Sets the generated password in the password field.
    }

    // Processes the data entered in the form and creates a WebsiteCredential object.
    public WebsiteCredential processResults() {
        // Trims input values and creates a new WebsiteCredential object.
        String websiteName = websiteNameField.getText().trim();
        String websiteUserName = websiteUserNameField.getText().trim();
        String websitePassword = webSitePasswordField.getText().trim();
        String notes = notesArea.getText().trim();

        return new WebsiteCredential(websiteName, websiteUserName, websitePassword, notes);
    }

    // Prepopulates the form fields with the data from an existing WebsiteCredential object.
    public void prepopulateFields(WebsiteCredential credential) {
        websiteNameField.setText(credential.getWebSiteName());
        websiteUserNameField.setText(credential.getWebSiteUserName());
        webSitePasswordField.setText(credential.getWebSitePassword());
        notesArea.setText(credential.getNotes());
    }

    // Initializes the combo boxes with their respective values and sets default selections.
    private void initializeComboBoxes() {
        // Sets values for the password length combo box and default selection.
        lengthComboBox.getItems().addAll(IntStream.rangeClosed(8, 16).boxed().collect(Collectors.toList()));
        lengthComboBox.setValue(8); // Default value set to 8

        // Sets values for the character type combo boxes and default selections.
        List<Integer> options = Arrays.asList(2, 3, 4);
        upperCaseComboBox.getItems().addAll(options);
        lowerCaseComboBox.getItems().addAll(options);
        numbersComboBox.getItems().addAll(options);
        specialCharsComboBox.getItems().addAll(options);

        upperCaseComboBox.setValue(2); // Default values set to 2 for all character type combo boxes.
        lowerCaseComboBox.setValue(2);
        numbersComboBox.setValue(2);
        specialCharsComboBox.setValue(2);
    }

    // Adds listeners to combo boxes to adjust the minimum length based on character type selections.
    private void addListenersToComboBoxes() {
        ChangeListener<Integer> changeListener = (observable, oldValue, newValue) -> adjustLengthComboBoxMinimum();
        upperCaseComboBox.valueProperty().addListener(changeListener);
        lowerCaseComboBox.valueProperty().addListener(changeListener);
        numbersComboBox.valueProperty().addListener(changeListener);
        specialCharsComboBox.valueProperty().addListener(changeListener);
    }

    // Dynamically adjusts the minimum selectable length in the length combo box based on other selections.
    private void adjustLengthComboBoxMinimum() {
        int upperCaseCount = upperCaseComboBox.getValue() == null ? 0 : upperCaseComboBox.getValue();
        int lowerCaseCount = lowerCaseComboBox.getValue() == null ? 0 : lowerCaseComboBox.getValue();
        int numbersCount = numbersComboBox.getValue() == null ? 0 : numbersComboBox.getValue();
        int specialCharsCount = specialCharsComboBox.getValue() == null ? 0 : specialCharsComboBox.getValue();

        // Calculates the sum of the minimum required characters to set the minimum length.
        int sum = upperCaseCount + lowerCaseCount + numbersCount + specialCharsCount;

        // Updates the length combo box items and selection based on the calculated sum.
        lengthComboBox.setItems(IntStream.rangeClosed(sum, 16).boxed().collect(Collectors.toCollection(FXCollections::observableArrayList)));
        if (lengthComboBox.getValue() == null || lengthComboBox.getValue() < sum) {
            lengthComboBox.setValue(sum); // Ensures the length selection meets the minimum requirements.
        }
    }
}
