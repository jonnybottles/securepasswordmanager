package com.jgm.securepasswordmanager.controllers;

import com.jgm.securepasswordmanager.datamodel.User;
import com.jgm.securepasswordmanager.datamodel.WebsiteCredential;
import com.jgm.securepasswordmanager.services.PasswordGeneratorService;
import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class AddPasswordController {

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


    @FXML
    public void initialize() {
        initializeComboBoxes();
        addListenersToComboBoxes();
    }

    @FXML
    public void handleGeneratePasswordButtonClicked() {

        int length = lengthComboBox.getValue();
        int upper = upperCaseComboBox.getValue();
        int lower = lowerCaseComboBox.getValue();
        int numbers = numbersComboBox.getValue();
        int specialChars = specialCharsComboBox.getValue();
        String generatedPassword = PasswordGeneratorService.generatePassword(length, upper, lower, numbers, specialChars);
    	webSitePasswordField.setText(generatedPassword);

    }

    public WebsiteCredential processResults() {
        String websiteName = websiteNameField.getText().trim();
        String websiteUserName = websiteUserNameField.getText().trim();
        String websitePassword = webSitePasswordField.getText().trim();
        String notes = notesArea.getText().trim();

        WebsiteCredential newWebsiteCredential = new WebsiteCredential(websiteName, websiteUserName, websitePassword, notes);
        return newWebsiteCredential;
    }

    public void prepopulateFields(WebsiteCredential credential) {
        websiteNameField.setText(credential.getWebSiteName());
        websiteUserNameField.setText(credential.getWebSiteUserName());
        webSitePasswordField.setText(credential.getWebSitePassword());
        notesArea.setText(credential.getNotes());
    }


    private void initializeComboBoxes() {
        // Initialize Length ComboBox with values from 8 to 16
        lengthComboBox.getItems().addAll(IntStream.rangeClosed(8, 16).boxed().collect(Collectors.toList()));
        lengthComboBox.setValue(8); // Default value set to 8

        // Initialize other ComboBoxes with values from 2 to 4
        List<Integer> options = Arrays.asList(2, 3, 4);
        upperCaseComboBox.getItems().addAll(options);
        lowerCaseComboBox.getItems().addAll(options);
        numbersComboBox.getItems().addAll(options);
        specialCharsComboBox.getItems().addAll(options);

        // Set default value to 2 for all
        upperCaseComboBox.setValue(2);
        lowerCaseComboBox.setValue(2);
        numbersComboBox.setValue(2);
        specialCharsComboBox.setValue(2);


    }

    private void addListenersToComboBoxes() {
        ChangeListener<Integer> changeListener = (observable, oldValue, newValue) -> adjustLengthComboBoxMinimum();
        upperCaseComboBox.valueProperty().addListener(changeListener);
        lowerCaseComboBox.valueProperty().addListener(changeListener);
        numbersComboBox.valueProperty().addListener(changeListener);
        specialCharsComboBox.valueProperty().addListener(changeListener);
    }

    // Adjusts the minimum range of the length combo box based on the sum of the other combo boxes
    private void adjustLengthComboBoxMinimum() {
        // Ensure we have valid values before we sum them up.
        int upperCaseCount = upperCaseComboBox.getValue() == null ? 0 : upperCaseComboBox.getValue();
        int lowerCaseCount = lowerCaseComboBox.getValue() == null ? 0 : lowerCaseComboBox.getValue();
        int numbersCount = numbersComboBox.getValue() == null ? 0 : numbersComboBox.getValue();
        int specialCharsCount = specialCharsComboBox.getValue() == null ? 0 : specialCharsComboBox.getValue();

        int sum = upperCaseCount + lowerCaseCount + numbersCount + specialCharsCount;

        // Set the range of valid lengths based on the sum of the other combo boxes.
        lengthComboBox.setItems(IntStream.rangeClosed(sum, 16).boxed().collect(Collectors.toCollection(FXCollections::observableArrayList)));

        // If the current value is less than the new minimum, update the value to the new minimum
        if (lengthComboBox.getValue() == null || lengthComboBox.getValue() < sum) {
            lengthComboBox.setValue(sum);
        }
    }




}