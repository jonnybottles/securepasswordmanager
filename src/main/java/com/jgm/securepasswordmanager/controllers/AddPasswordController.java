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

    private User theLoadedUser;

    public void setUser(User theLoadedUser) {
        this.theLoadedUser = theLoadedUser;
    }

    public WebsiteCredential processResults() {
        String websiteName = websiteNameField.getText().trim();
        String websiteUserName = webSitePasswordField.getText().trim();
        String websitePassword = webSitePasswordField.getText().trim();
        String notes = notesArea.getText().trim();

        WebsiteCredential newWebsiteCredential = new WebsiteCredential(websiteName, websiteUserName, websitePassword, notes);
        theLoadedUser.addCredential(newWebsiteCredential);
        return newWebsiteCredential;
    }

    public void editWebsiteCredential(WebsiteCredential theWebsiteCredential) {
        websiteNameField.setText(theWebsiteCredential.getWebSiteName());
        websiteUserNameField.setText(theWebsiteCredential.getWebSiteUserName());
        webSitePasswordField.setText(theWebsiteCredential.getWebSitePassword());
        notesArea.setText(theWebsiteCredential.getNotes());
    }

    public void updateWebsiteCredential(WebsiteCredential theWebsiteCredential) {
        websiteNameField.setText(theWebsiteCredential.getWebSiteName());
        websiteUserNameField.setText(theWebsiteCredential.getWebSiteUserName());
        webSitePasswordField.setText(theWebsiteCredential.getWebSitePassword());
        notesArea.setText(theWebsiteCredential.getNotes());
    }


}