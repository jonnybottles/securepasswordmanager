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
    private String secretKeyFor2FABarcode;
    private boolean hasRegisteredTwoFactorAuthentication;
    private String masterPassword;
    private boolean hasCreatedMasterPassword;


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

    public boolean setFirstName(String firstName) {
        if (!firstName.isEmpty()) {
            this.firstName = firstName;
            return true;
        }
        return false;
    }

    public String getLastName() {
        return lastName;
    }

    public boolean setLastName(String lastName) {
        if (!lastName.isEmpty()) {
            this.lastName = lastName;
            return true;
        }
        return false;
    }

    public String getEmailAddress() {
        return emailAddress;
    }

    // Validates email with basic criteria: local-part@domain
    // - Local-part: alphanumeric, including _+&*-., not starting or ending with a dot
    // - Domain: alphanumeric, including hyphens, followed by a period and a TLD (2-7 letters)
    // Examples:
    // Valid: example.email@domain.com
    // Invalid: .email@domain..com

    public boolean setEmailAddress(String emailAddress) {
        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
        if (emailAddress.matches(emailRegex)) {
            this.emailAddress = emailAddress;
            return true;
        }
        return false;
    }
    public String getUserName() {
        return userName;
    }

    public boolean setUserName(String userName) {
        if (!userName.isEmpty()) {
            this.userName = userName;
            return true;
        }
        return false;
    }


    public String getPassword() {
        return password;
    }

    public boolean setPassword(String password) {
        if (!password.isEmpty()) {
            this.password = password;
            return true;
        }
        return false;
    }

    public boolean getHasRegisteredTwoFactorAuthentication() {
        return hasRegisteredTwoFactorAuthentication;
    }

    public void setHasRegisteredTwoFactorAuthentication(boolean hasRegisteredTwoFactorAuthentication) {
        this.hasRegisteredTwoFactorAuthentication = hasRegisteredTwoFactorAuthentication;
    }

    public String getSecretKeyFor2FABarcode() {
        return secretKeyFor2FABarcode;
    }

    public void setSecretKeyFor2FABarcode(String secretKeyFor2FABarcode) {
        this.secretKeyFor2FABarcode = secretKeyFor2FABarcode;
    }

    public String getMasterPassword() {
        return masterPassword;
    }

    public void setMasterPassword(String masterPassword) {
        this.masterPassword = masterPassword;
    }

    public boolean getHasCreatedMasterPassword() {
        return hasCreatedMasterPassword;
    }

    public void setHasCreatedMasterPassword(boolean hasCreatedMasterPassword) {
        this.hasCreatedMasterPassword = hasCreatedMasterPassword;
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
        User user = (User) o;
        return Objects.equals(userName, user.userName) &&
                Objects.equals(password, user.password);
    }


    // Required to implement the equals override method
    @Override
    public int hashCode() {
        return Objects.hash(userName, password);
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