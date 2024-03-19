package com.jgm.securepasswordmanager.services;

import com.jgm.securepasswordmanager.datamodel.User;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class AuthenticationService {

    private List<User> theLoadedUsers;
    private UserDataService theUserDataService;

    public AuthenticationService() {
        this.theLoadedUsers = new ArrayList<User>();
        this.theUserDataService = new UserDataService();
    }

    public User login(String userName, String password) {
        User tempUser = new User();
        tempUser.setUserName(userName);
        tempUser.setPassword(password);
        populateUserList();

        for (User theUser : theLoadedUsers) {
            if (theUser.equals(tempUser)) {
                return theUser;
            }
        }

        return null;
    }

    public boolean saveUser(User theUser) {
        if(theUserDataService.writeUserToFile(theUser)) {
            populateUserList();
            return true;
        }
        return false;
    }


    public boolean populateUserList() {
        // Attempt to load the list of users from file.
        List<User> tempUserList = theUserDataService.loadUsersFromFile();

        // Check if the list is null which means loading was unsuccessful
        if (tempUserList == null || tempUserList.isEmpty()) {
            // Clear theLoadedUsers to reset the list due to load failure
            theLoadedUsers.clear();
            return false;
        }

        // Clear the existing list to avoid duplicate entries if this method is called multiple times.
        theLoadedUsers.clear();

        // Add all loaded users to theLoadedUsers list.
        theLoadedUsers.addAll(tempUserList);

        return true;
    }



}
