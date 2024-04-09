package com.jgm.securepasswordmanager.services;

import com.jgm.securepasswordmanager.utils.DirectoryPath;
import com.jgm.securepasswordmanager.utils.FileUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.io.File;

// Defines the test class for TwoFactorAuthenticationService.
// This class contains unit tests to ensure that the two-factor authentication functionalities work as expected.
class TwoFactorAuthenticationServiceTest {

	private UserDataService theUserDataService; // Instance of UserDataService for user-related operations.

	@BeforeEach
	public void setUp() {
		// Setup method that runs before each test method.
		// It ensures a clean state by deleting existing test directories and recreating them.
		FileUtils.recursiveDelete(DirectoryPath.TEST_USERS_DIRECTORY); // Deletes the user directory recursively.

		// Creates necessary directories if they do not exist for the test environment.
		UserDataService.createDirectoryIfNotExists(DirectoryPath.TEST_USERS_DIRECTORY);
		UserDataService.createDirectoryIfNotExists(DirectoryPath.TEST_LOGS_DIRECTORY);
		LogParserService.setLogFilePath(DirectoryPath.TEST_LOGS_DIRECTORY + "/securepasswordmanager.log"); ;
		UserDataService.setTheUserDataDirectoryPath(DirectoryPath.TEST_USERS_DIRECTORY);
		UserDataService.createDirectoryIfNotExists(DirectoryPath.TEST_QR_CODE_DIRECTORY);

	}

	@AfterEach
	public void tearDown() {
		// Cleanup method that runs after each test method.
		// Deletes the test directories and their contents to ensure no test data is left.
		FileUtils.recursiveDelete(DirectoryPath.TEST_USERS_DIRECTORY);
		FileUtils.recursiveDelete(DirectoryPath.TEST_LOGS_DIRECTORY);
		FileUtils.recursiveDelete(DirectoryPath.TEST_QR_CODE_DIRECTORY);
	}

	@Test
	void testGetGoogleAuthenticatorBarCode() {
		// Test method to verify the Google Authenticator barcode URL generation.
		// Arrange section where the input parameters and expected result are defined.
		String secretKey = "QDWSM3OYBPGTEVSPB5FKVDM3CSNCWHVK";
		String emailAddress = "test@gmail.com";
		String issuer = "Butler Cyber Technologies";
		String expectedBarCode = "otpauth://totp/Butler%20Cyber%20Technologies%3Atest%40gmail.com?secret=QDWSM3OYBPGTEVSPB5FKVDM3CSNCWHVK&issuer=Butler%20Cyber%20Technologies";

		// Act section where the actual barcode is generated.
		String actualBarCode = TwoFactorAuthenticationService.getGoogleAuthenticatorBarCode(secretKey, emailAddress, issuer);

		// Assert section to verify the expected barcode matches the actual barcode.
		assertEquals(expectedBarCode, actualBarCode, "The generated Google Authenticator barcode URL does not match the expected value.");
	}

	@Test
	void testCreateQRCode() throws Exception {
		// Test method to verify QR code generation.
		// Arrange section with barcode data, file path, and dimensions for the QR code.
		String barCodeData =  TwoFactorAuthenticationService.getGoogleAuthenticatorBarCode(
				"QDWSM3OYBPGTEVSPB5FKVDM3CSNCWHVK",
				"xxbutler86xx@gmail.com",
				"Butler Cyber Technologies"
		);
		String filePath = "src/test/resources/test-qr.png";
		int height = 200;
		int width = 200;

		// Act section where the QR code is generated and saved to the specified file path.
		TwoFactorAuthenticationService.createQRCode(barCodeData, filePath, height, width);

		// Assert section to verify that the QR code file exists.
		File file = new File(filePath);
		assertTrue(file.exists(), "QR Code image file was not created.");

	}

	@Test
	void testGetTOTPCode() {
		// Test method to verify the generation of a TOTP code.
		// Arrange section where the secret key is defined.
		String secretKey = "QDWSM3OYBPGTEVSPB5FKVDM3CSNCWHVK";

		// Act section where the TOTP code is generated.
		String otpCode = TwoFactorAuthenticationService.getTOTPCode(secretKey);

		// Assert section to verify the OTP code is not null and is numeric.
		assertNotNull(otpCode, "OTP code should not be null.");
		assertTrue(otpCode.matches("\\d+"), "OTP code should be numeric.");
	}

	@Test
	void testValidateAuthenticationCode() {
		// Test method to verify the validation of a user-provided authentication code.
		// Arrange section where the secret key is set up, and both correct and incorrect user codes are defined.
		String secretKey = "QDWSM3OYBPGTEVSPB5FKVDM3CSNCWHVK";
		String correctUserCode = TwoFactorAuthenticationService.getTOTPCode(secretKey); // Correct TOTP code
		String incorrectUserCode = "123456"; // Example of an incorrect TOTP code

		// Act & Assert section to verify both the validation of the correct code and the rejection of the incorrect code.
		assertTrue(TwoFactorAuthenticationService.validateAuthenticationCode(correctUserCode, secretKey), "Correct user code should validate as true.");
		assertFalse(TwoFactorAuthenticationService.validateAuthenticationCode(incorrectUserCode, secretKey), "Incorrect user code should validate as false.");
	}

}
