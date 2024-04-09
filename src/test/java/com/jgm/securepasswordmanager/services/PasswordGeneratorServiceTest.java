package com.jgm.securepasswordmanager.services;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class PasswordGeneratorServiceTest {

	// Test generating a password with specific requirements
	@Test
	public void testGeneratePasswordWithSpecificRequirements() {
		int length = 10;
		int numUpperCase = 2;
		int numLowerCase = 2;
		int numNumbers = 3;
		int numSpecialChar = 3;

		String password = PasswordGeneratorService.generatePassword(length, numUpperCase, numLowerCase, numNumbers, numSpecialChar);

		// Assert the password matches the requested length
		Assertions.assertEquals(length, password.length());

		// Assert the password contains the correct number of each character type
		Assertions.assertTrue(password.matches(".*[A-Z].*[A-Z].*"));
		Assertions.assertTrue(password.matches(".*[a-z].*[a-z].*"));
		Assertions.assertTrue(password.matches(".*\\d.*\\d.*\\d.*"));
		Assertions.assertTrue(password.matches(".*[!@#$%^&*()_+].*[!@#$%^&*()_+].*[!@#$%^&*()_+].*"));
	}

	// Test generating a password with invalid length
	@Test
	public void testGeneratePasswordWithInvalidLength() {
		int length = 5; // This is too short based on the sum of the other requirements
		int numUpperCase = 2;
		int numLowerCase = 2;
		int numNumbers = 2;
		int numSpecialChar = 2;

		Assertions.assertThrows(IllegalArgumentException.class, () -> {
			PasswordGeneratorService.generatePassword(length, numUpperCase, numLowerCase, numNumbers, numSpecialChar);
		});
	}

}
