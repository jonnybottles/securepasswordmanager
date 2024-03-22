package com.jgm.securepasswordmanager.controllers;

import com.jgm.securepasswordmanager.datamodel.User;
import com.jgm.securepasswordmanager.datamodel.WebsiteCredential;
import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

import java.time.LocalDate;

public class AddPasswordController {

    @FXML
    private TextField websiteNameField;

    @FXML
    private TextField websiteUserNameField;

    @FXML
    private TextField webSitePasswordField;

    @FXML
    private TextArea notesArea;


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


    public void editWebsiteCredential(WebsiteCredential theWebsiteCredential) {
        websiteNameField.setText(theWebsiteCredential.getWebSiteName());
        websiteUserNameField.setText(theWebsiteCredential.getWebSiteUserName());
        webSitePasswordField.setText(theWebsiteCredential.getWebSitePassword());
        notesArea.setText(theWebsiteCredential.getNotes());
    }

    public WebsiteCredential updateWebsiteCredential(WebsiteCredential theWebsiteCredential) {
        websiteNameField.setText(theWebsiteCredential.getWebSiteName());
        websiteUserNameField.setText(theWebsiteCredential.getWebSiteUserName());
        webSitePasswordField.setText(theWebsiteCredential.getWebSitePassword());
        notesArea.setText(theWebsiteCredential.getNotes());



        theWebsiteCredential.setWebSiteName(websiteNameField.getText());
        theWebsiteCredential.setWebSiteUserName(websiteUserNameField.getText());
        theWebsiteCredential.setWebSitePassword(webSitePasswordField.getText());
        theWebsiteCredential.setNotes(notesArea.getText());

        return theWebsiteCredential;

    }


}