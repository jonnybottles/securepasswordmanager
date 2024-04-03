package com.jgm.securepasswordmanager.services;

import com.google.zxing.WriterException;
import com.jgm.securepasswordmanager.datamodel.User;
import com.jgm.securepasswordmanager.utils.DirectoryPath;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class AuthenticationService {

    private List<User> theLoadedUsers;
    private UserDataService theUserDataService;
    private TwoFactorAuthenticationService twoFactorAuthenticationService;
    public AuthenticationService() {
        this.theLoadedUsers = new ArrayList<User>();
        this.theUserDataService = new UserDataService();
        this.twoFactorAuthenticationService = new TwoFactorAuthenticationService();
    }

    public User login(String userName, String password) {
        User tempUser = new User();
        tempUser.setUserName(userName);
        tempUser.setPassword(password);
        populateUserList();

        for (User theUser : theLoadedUsers) {
//            System.out.println("User Name: " + theUser.getUserName()  + "\n" + "Password:" + theUser.getPassword() +"\n");
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

    public boolean generateQRCode(User theUser, String qRCodePath) {
        String barCode = twoFactorAuthenticationService.getGoogleAuthenticatorBarCode("QDWSM3OYBPGTEVSPB5FKVDM3CSNCWHVK",
                theUser.getEmailAddress(), "Butler Cyber Technologies");

        try {
            twoFactorAuthenticationService.createQRCode(barCode, qRCodePath, 200, 200);
            return true;
        } catch (WriterException | IOException e) {
            System.out.println(e);
            return false;
        }

    }

    public boolean registerTwoFactorAuthentication(String userCode, String secretKey) {
		return twoFactorAuthenticationService.validateAuthenticationCode(userCode, secretKey);
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
