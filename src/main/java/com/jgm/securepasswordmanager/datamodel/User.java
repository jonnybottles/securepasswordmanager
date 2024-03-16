package com.jgm.securepasswordmanager.datamodel;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class User {
    private String firstName;
    private String lastName;
    private String emailAddress;
    private String userName;
    private String password;
    private List<WebsiteCredential> websiteCredentialList;

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
        this.websiteCredentialList = new ArrayList<>();
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

    public List<WebsiteCredential> getWebsiteCredentialList() {
        return websiteCredentialList;
    }

    public void setWebsiteCredentialList(List<WebsiteCredential> websiteCredentialList) {
        this.websiteCredentialList = websiteCredentialList;
    }


    public boolean addCredential(WebsiteCredential newCred) {
        for (WebsiteCredential existingCred : websiteCredentialList) {
            if (existingCred.equals(newCred)) {
                return false;
            }
        }
        websiteCredentialList.add(newCred);
        return true;
    }

    public boolean removeCredential(WebsiteCredential credToRemove) {
        return websiteCredentialList.remove(credToRemove);
    }

}