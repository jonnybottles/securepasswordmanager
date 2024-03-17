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


//    // Gson uses this to serialize/deserialize
//    public List<WebsiteCredential> getWebsiteCredentialNormalList() {
//        return new ArrayList<>(websiteCredentialObservablelList);
//    }
//
//    public void setWebsiteCredentialNormalList(List<WebsiteCredential> websiteCredentialNormalList) {
//        this.websiteCredentialObservablelList = FXCollections.observableArrayList(websiteCredentialNormalList);
//    }

    public List<WebsiteCredential> getWebsiteCredentialNormalList() {
        return websiteCredentialNormalList; // This should return the normal list, not the observable list
    }

    public void setWebsiteCredentialNormalList(List<WebsiteCredential> websiteCredentialNormalList) {
        this.websiteCredentialNormalList = websiteCredentialNormalList; // This should set the normal list, not convert it to an observable list
        this.websiteCredentialObservablelList = FXCollections.observableArrayList(websiteCredentialNormalList); // Convert the normal list to an observable list for runtime use
    }


    // Runtime methods use this
    public ObservableList<WebsiteCredential> getWebsiteCredentialObservablelList() {
        return websiteCredentialObservablelList;
    }

    public void setWebsiteCredentialObservablelList(ObservableList<WebsiteCredential> websiteCredentialObservablelList) {
        this.websiteCredentialObservablelList = websiteCredentialObservablelList;
    }


    public boolean addCredential(WebsiteCredential newCred) {
        for (WebsiteCredential existingCred : websiteCredentialObservablelList) {
            if (existingCred.equals(newCred)) {
                return false;
            }
        }
        websiteCredentialObservablelList.add(newCred);
        return true;
    }

    public boolean removeCredential(WebsiteCredential credToRemove) {
        return websiteCredentialObservablelList.remove(credToRemove);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User that = (User) o;
        return Objects.equals(userName, that.userName);
    }

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