package com.jgm.securepasswordmanager.services;

import com.jgm.securepasswordmanager.utils.DirectoryPath;
import com.jgm.securepasswordmanager.utils.FileUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.io.File;

class TwoFactorAuthenticationServiceTest {

	private UserDataService theUserDataService;

	@BeforeEach
	public void setUp() {
		// Initialize UserDataService for each test
		// Ensure the test directory is clean before each test
		FileUtils.recursiveDelete(DirectoryPath.TEST_USERS_DIRECTORY);

		UserDataService.createDirectoryIfNotExists(DirectoryPath.TEST_USERS_DIRECTORY);
		UserDataService.createDirectoryIfNotExists(DirectoryPath.TEST_LOGS_DIRECTORY);
		LogParserService.setLogFilePath(DirectoryPath.TEST_LOGS_DIRECTORY + "/securepasswordmanager.log"); ;
		UserDataService.setTheUserDataDirectoryPath(DirectoryPath.TEST_USERS_DIRECTORY);
		UserDataService.createDirectoryIfNotExists(DirectoryPath.TEST_QR_CODE_DIRECTORY);

	}

	@AfterEach
	public void tearDown() {
		// Clean up after each test by deleting the test directory and its contents
		FileUtils.recursiveDelete(DirectoryPath.TEST_USERS_DIRECTORY);
		FileUtils.recursiveDelete(DirectoryPath.TEST_LOGS_DIRECTORY);
		FileUtils.recursiveDelete(DirectoryPath.TEST_QR_CODE_DIRECTORY);
	}

	@Test
	void testGetGoogleAuthenticatorBarCode() {
		// Arrange
		String secretKey = "QDWSM3OYBPGTEVSPB5FKVDM3CSNCWHVK";
		String emailAddress = "test@gmail.com";
		String issuer = "Butler Cyber Technologies";
		String expectedBarCode = "otpauth://totp/Butler%20Cyber%20Technologies%3Atest%40gmail.com?secret=QDWSM3OYBPGTEVSPB5FKVDM3CSNCWHVK&issuer=Butler%20Cyber%20Technologies";



		String actualBarCode = TwoFactorAuthenticationService.getGoogleAuthenticatorBarCode(secretKey, emailAddress, issuer);

		// Assert
		assertEquals(expectedBarCode, actualBarCode, "The generated Google Authenticator barcode URL does not match the expected value.");
	}

	@Test
	void testCreateQRCode() throws Exception {

		// Arrange
		String barCodeData =  TwoFactorAuthenticationService.getGoogleAuthenticatorBarCode(
				"QDWSM3OYBPGTEVSPB5FKVDM3CSNCWHVK",
				"xxbutler86xx@gmail.com",
				"Butler Cyber Technologies"
		);
		String filePath = "src/test/resources/test-qr.png";
		int height = 200;
		int width = 200;

		// Act
		TwoFactorAuthenticationService.createQRCode(barCodeData, filePath, height, width);

		// Assert
		File file = new File(filePath);
		assertTrue(file.exists(), "QR Code image file was not created.");

	}

	@Test
	void testGetTOTPCode() {
		// Arrange
		String secretKey = "QDWSM3OYBPGTEVSPB5FKVDM3CSNCWHVK";


		// Act
		String otpCode = TwoFactorAuthenticationService.getTOTPCode(secretKey);

		// Assert
		assertNotNull(otpCode, "OTP code should not be null.");
		assertTrue(otpCode.matches("\\d+"), "OTP code should be numeric.");
	}

	@Test
	void testValidateAuthenticationCode() {
		// Arrange
		String secretKey = "QDWSM3OYBPGTEVSPB5FKVDM3CSNCWHVK";
		// This will need to be the actual TOTP code generated at the time of testing
		String correctUserCode = TwoFactorAuthenticationService.getTOTPCode(secretKey);
		String incorrectUserCode = "123456";

		// Act & Assert
		assertTrue(TwoFactorAuthenticationService.validateAuthenticationCode(correctUserCode, secretKey), "Correct user code should validate as true.");
		assertFalse(TwoFactorAuthenticationService.validateAuthenticationCode(incorrectUserCode, secretKey), "Incorrect user code should validate as false.");
	}

}
