package com.jgm.securepasswordmanager.services;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class EncryptionServiceTest {

	// Test encrypting and decrypting a string returns the original string
	@Test
	public void testEncryptDecrypt() {
		String originalString = "This is a secret message!";
		String secretKey = "mySecretKey";
		String salt = "mySalt";

		// Encrypt the original string
		String encryptedString = EncryptionService.encrypt(originalString, secretKey, salt);
		Assertions.assertNotNull(encryptedString, "Encrypted string should not be null");

		// Decrypt the encrypted string
		String decryptedString = EncryptionService.decrypt(encryptedString, secretKey, salt);
		Assertions.assertEquals(originalString, decryptedString, "Decrypted string should match the original");
	}

	// Test that encrypted string is different from the original
	@Test
	public void testEncryptionChangesString() {
		String originalString = "This is another secret message!";
		String secretKey = "anotherSecretKey";
		String salt = "anotherSalt";

		// Encrypt the original string
		String encryptedString = EncryptionService.encrypt(originalString, secretKey, salt);
		Assertions.assertNotEquals(originalString, encryptedString, "Encrypted string should not match the original");
	}

}
