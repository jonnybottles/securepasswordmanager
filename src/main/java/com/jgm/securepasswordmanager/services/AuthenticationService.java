package com.jgm.securepasswordmanager.services;

import com.google.zxing.WriterException;
import com.jgm.securepasswordmanager.datamodel.User;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

// AuthenticationService handles authentication processes, including login, user registration,
// QR code generation for two-factor authentication, and user list management.
public class AuthenticationService {

    // A list to hold loaded users from storage for authentication purposes.
    private List<User> theLoadedUsers;

    // Constructor initializes the list of users.
    public AuthenticationService() {
        this.theLoadedUsers = new ArrayList<>();
    }

    // Attempts to authenticate a user based on a username and password.
    // Returns the authenticated User object if successful, null otherwise.
    public User login(String userName, String password) {
        // Create a temporary user for comparison purposes.
        User tempUser = new User();
        tempUser.setUserName(userName);
        tempUser.setPassword(password);

        // Ensure the user list is populated from the data source.
        populateUserList();

        // Iterate through the loaded users to find a match.
        for (User theUser : theLoadedUsers) {
            if (theUser.equals(tempUser)) {
                // Return the matched user.
                return theUser;
            }
        }

        // Return null if no match is found.
        return null;
    }

    // Saves a user's data to storage, returns true if successful.
    public boolean saveUser(User theUser) {
        // Attempt to write the user to file.
        if(UserDataService.writeUserToFile(theUser)) {
            // Re-populate the user list to include the newly saved user.
            populateUserList();
            return true;
        }
        return false;
    }

    // Generates a QR code for two-factor authentication for a given user, saving it to the specified path.
    // Returns true if the QR code was successfully generated.
    public boolean generateQRCode(User theUser, String qRCodePath) {
        // Retrieve the user's secret key and generate a barcode URL.
        String secretKey = theUser.getSecretKeyFor2FABarcode();
        String barCode = TwoFactorAuthenticationService.getGoogleAuthenticatorBarCode(secretKey,
                theUser.getEmailAddress(), "Secure Password Manager");

        try {
            // Attempt to create and save the QR code to file.
            TwoFactorAuthenticationService.createQRCode(barCode, qRCodePath, 200, 200);
            return true;
        } catch (WriterException | IOException e) {
            // Print the exception and return false if QR code generation fails.
            System.out.println(e);
            return false;
        }
    }

    // Validates a user-entered code for two-factor authentication against the expected secret key.
    // Returns true if the code is valid.
    public boolean registerTwoFactorAuthentication(String userCode, String secretKey) {
        return TwoFactorAuthenticationService.validateAuthenticationCode(userCode, secretKey);
    }

    // Populates the list of users from the data source. Returns true if at least one user was loaded successfully.
    public boolean populateUserList() {
        // Attempt to load the list of users from file.
        List<User> tempUserList = UserDataService.loadUsersFromFile();

        // Check if the list is null or empty, indicating loading was unsuccessful.
        if (tempUserList == null || tempUserList.isEmpty()) {
            // Clear theLoadedUsers to reset the list due to load failure.
            theLoadedUsers.clear();
            return false;
        }

        // Clear the existing list to avoid duplicate entries.
        theLoadedUsers.clear();

        // Add all loaded users to theLoadedUsers list.
        theLoadedUsers.addAll(tempUserList);

        return true;
    }
}
