package com.jgm.securepasswordmanager.services;

import java.io.File;

public class UserDataService {


    public UserDataService() {
    }

    // Creates the user_data directory if it doesn't already exist
    public boolean createUserDataDirectory() {

        // Obtain the path to the users current working directory / where the program was executed from.
        String currentPath = System.getProperty("user.dir");

        // Creates user_data directory path by concating current path
        // with OS specific separator (e.g. / or \) and the user data directory
        File userDataDirectory = new File(currentPath + File.separator + "user_data");

        if (!userDataDirectory.exists()) {
            try {
                boolean wasCreated = userDataDirectory.mkdirs();
                if (!wasCreated) {
                    return false;
                }
            } catch (SecurityException e) {
                return false;
            }
        }

        return true;
    }



}
