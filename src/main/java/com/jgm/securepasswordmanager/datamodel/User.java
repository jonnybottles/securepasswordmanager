package com.jgm.securepasswordmanager.datamodel;

import java.util.ArrayList;
import java.util.List;

public class User {
    private String firstName;
    private String lastName;
    private String userName;
    private String password;
    private List<WebsiteCredential> websiteCredentialList;

    public User(String firstName, String lastName, String userName, String password) {
        this.firstName = firstName;
        this.lastName = lastName;
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
}
