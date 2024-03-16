package com.jgm.securepasswordmanager.services;

import com.jgm.securepasswordmanager.datamodel.User;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.List;

public class UserDataService {
    private final String USERS_DIRECTORY_PATH = "user_data";
    private EncryptionService theEncryptionService;
    private Gson theGson;

    public UserDataService() {
        this.theEncryptionService = new EncryptionService();
        this.theGson = new GsonBuilder().setPrettyPrinting().create();
    }

    // Creates the user_data directory if it doesn't already exist
    public boolean createUserDataDirectory() {

        // Obtain the path to the users current working directory / where the program was executed from.
        String currentPath = System.getProperty("user.dir");

        // Creates user_data directory path by concating current path
        // with OS specific separator (e.g. / or \) and the user data directory
        File userDataDirectory = new File(currentPath + File.separator + USERS_DIRECTORY_PATH);

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

    public boolean writeUserToFile(User user) {
        if (!createUserDataDirectory()) {
            return false;
        }

        String userDataFilename = USERS_DIRECTORY_PATH + File.separator + user.getUserName() + ".json";
        try (Writer writer = new FileWriter(userDataFilename)) {
            theGson.toJson(user, writer);
            return true;

        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }

    }

//    public List<User> loadUsersFromFile() {
//
//    }





}
