package com.jgm.securepasswordmanager.datamodel;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class User {
    private String firstName;
    private String lastName;
    private String emailAddress;
    private String userName;
    private String password;
    // Transient: Gson will ignore this field during serialization/deserialization
    private transient ObservableList<WebsiteCredential> websiteCredentialObservablelList;

    // This list is used only for Gson serialization/deserialization
    private List<WebsiteCredential> websiteCredentialNormalList;

    // No-arg constructor
    public User() {
        // Chain this constructor to the existing one, providing default values
        this("", "", "", "", "");
    }

    public User(String firstName, String lastName, String emailAddress, String userName, String password) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.emailAddress = emailAddress;
        this.userName = userName;
        this.password = password;
        this.websiteCredentialObservablelList = FXCollections.observableArrayList();
        this.websiteCredentialNormalList = new ArrayList<>();
    }



    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmailAddress() {
        return emailAddress;
    }

    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    // This method returns a normal website Credential list, used for writing to Json
    public List<WebsiteCredential> getWebsiteCredentialNormalList() {
        return websiteCredentialNormalList;
    }

    // This method sets the normal website Credential list that is loaded to from the Json users file and
    // copies the normal list to an observable list for runtime use
    public void setWebsiteCredentialNormalList(List<WebsiteCredential> websiteCredentialNormalList) {
        this.websiteCredentialNormalList = websiteCredentialNormalList;
        this.websiteCredentialObservablelList = FXCollections.observableArrayList(websiteCredentialNormalList);
    }


    // Returns the observable credential list, used during runtime operations
    public ObservableList<WebsiteCredential> getWebsiteCredentialObservablelList() {
        return websiteCredentialObservablelList;
    }

    public void setWebsiteCredentialObservablelList(ObservableList<WebsiteCredential> websiteCredentialObservablelList) {
        this.websiteCredentialObservablelList = websiteCredentialObservablelList;
    }


    // Adds a credential to the users observable credential list
    public boolean addCredential(WebsiteCredential newCred) {
        for (WebsiteCredential existingCred : websiteCredentialObservablelList) {
            if (existingCred.equals(newCred)) {
                return false;
            }
        }
        websiteCredentialObservablelList.add(newCred);
        return true;
    }

    // Removes a credential from the users observable credential list
    public boolean removeCredential(WebsiteCredential credToRemove) {
        return websiteCredentialObservablelList.remove(credToRemove);
    }

    // Overrides the default equals method to allow for comparing of two user objects
    // Two users are considered equal if they have the same username
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User that = (User) o;
        return Objects.equals(userName, that.userName);
    }

    // Required to implement the equals override method
    @Override
    public int hashCode() {
        return Objects.hash(userName);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("User:").append("\n")
                .append(" - firstName='").append(firstName).append("\n")
                .append(" - lastName='").append(lastName).append("\n")
                .append(" - emailAddress='").append(emailAddress).append("\n")
                .append(" - userName='").append(userName).append("\n")
                .append(" - password='").append(password).append("\n")
                .append(" - Website Credentials:\n\n");

        // Append each WebsiteCredential's string representation
        int count = 1;
        for (WebsiteCredential credential : websiteCredentialObservablelList) {
            sb.append("Website Credential ").append(count).append(":\n");
            sb.append(credential.toString()).append("\n");
            count +=1;
        }
        return sb.toString();
    }

}