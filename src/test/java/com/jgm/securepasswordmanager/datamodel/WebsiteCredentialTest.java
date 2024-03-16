package com.jgm.securepasswordmanager.datamodel;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class WebsiteCredentialTest {

    private WebsiteCredential credential;

    @BeforeEach
    void setUp() {
        credential = new WebsiteCredential("ExampleSite", "User1", "Password123", "My notes");
    }

    @Test
    void getWebSiteName_returnsCorrectName() {
        assertEquals("ExampleSite", credential.getWebSiteName());
    }

    @Test
    void setWebSiteName_updatesNameCorrectly() {
        credential.setWebSiteName("NewSiteName");
        assertEquals("NewSiteName", credential.getWebSiteName());
    }

    // Similarly write tests for other getters and setters

    @Test
    void equals_self_returnsTrue() {
        assertTrue(credential.equals(credential));
    }

    @Test
    void equals_null_returnsFalse() {
        assertFalse(credential.equals(null));
    }

    @Test
    void equals_differentClass_returnsFalse() {
        assertFalse(credential.equals("A String Object"));
    }

    @Test
    void equals_differentCredentials_returnsFalse() {
        WebsiteCredential differentCredential = new WebsiteCredential("OtherSite", "User2", "Password123", "Notes");
        assertFalse(credential.equals(differentCredential));
    }

    @Test
    void equals_sameFields_returnsTrue() {
        WebsiteCredential sameCredential = new WebsiteCredential("ExampleSite", "User1", "Password123", "My notes");
        assertTrue(credential.equals(sameCredential));
    }

    @Test
    void hashCode_consistentWithEquals() {
        WebsiteCredential sameCredential = new WebsiteCredential("ExampleSite", "User1", "Password123", "My notes");
        assertEquals(credential.hashCode(), sameCredential.hashCode());
    }
}
