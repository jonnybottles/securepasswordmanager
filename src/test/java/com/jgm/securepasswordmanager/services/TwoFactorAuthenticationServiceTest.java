package com.jgm.securepasswordmanager.services;

import com.jgm.securepasswordmanager.controllers.TwoFactorVerificationController;
import com.jgm.securepasswordmanager.utils.DirectoryPath;
import com.jgm.securepasswordmanager.utils.FileUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;

class TwoFactorAuthenticationServiceTest {

	private UserDataService theUserDataService;

	@BeforeEach
	public void setUp() {
		// Initialize UserDataService for each test
		theUserDataService = new UserDataService();
		// Ensure the test directory is clean before each test
		FileUtils.recursiveDelete(DirectoryPath.USERS_DIRECTORY);

		theUserDataService.createAllProgramDirectories();
	}

	@AfterEach
	public void tearDown() {
		// Clean up after each test by deleting the test directory and its contents
		FileUtils.recursiveDelete(DirectoryPath.USERS_DIRECTORY);
	}

	@Test
	void testGetGoogleAuthenticatorBarCode() {
		// Arrange
		String secretKey = "QDWSM3OYBPGTEVSPB5FKVDM3CSNCWHVK";
		String emailAddress = "test@gmail.com";
		String issuer = "Butler Cyber Technologies";
		String expectedBarCode = "otpauth://totp/Butler%20Cyber%20Technologies%3Atest%40gmail.com?secret=QDWSM3OYBPGTEVSPB5FKVDM3CSNCWHVK&issuer=Butler%20Cyber%20Technologies";

		// Act
		TwoFactorAuthenticationService theTwoFactorAuthentiationService = new TwoFactorAuthenticationService();

		String actualBarCode = theTwoFactorAuthentiationService.getGoogleAuthenticatorBarCode(secretKey, emailAddress, issuer);

		// Assert
		assertEquals(expectedBarCode, actualBarCode, "The generated Google Authenticator barcode URL does not match the expected value.");
	}

	@Test
	void testCreateQRCode() throws Exception {
		TwoFactorAuthenticationService theTwoFactorAuthentiationService = new TwoFactorAuthenticationService();

		// Arrange
		String barCodeData =  theTwoFactorAuthentiationService.getGoogleAuthenticatorBarCode(
				"QDWSM3OYBPGTEVSPB5FKVDM3CSNCWHVK",
				"xxbutler86xx@gmail.com",
				"Butler Cyber Technologies"
		);
		String filePath = "src/test/resources/test-qr.png";
		int height = 200;
		int width = 200;

		// Act
		theTwoFactorAuthentiationService.createQRCode(barCodeData, filePath, height, width);

		// Assert
		File file = new File(filePath);
		assertTrue(file.exists(), "QR Code image file was not created.");

		// Cleanup
//		Files.delete(Paths.get(filePath));
	}

	@Test
	void testGetTOTPCode() {
		// Arrange
		String secretKey = "QDWSM3OYBPGTEVSPB5FKVDM3CSNCWHVK";
		TwoFactorAuthenticationService service = new TwoFactorAuthenticationService();

		// Act
		String otpCode = service.getTOTPCode(secretKey);

		// Assert
		assertNotNull(otpCode, "OTP code should not be null.");
		assertTrue(otpCode.matches("\\d+"), "OTP code should be numeric.");
	}

	@Test
	void testValidateAuthenticationCode() {
		// Arrange
		TwoFactorAuthenticationService service = new TwoFactorAuthenticationService();
		String secretKey = "QDWSM3OYBPGTEVSPB5FKVDM3CSNCWHVK";
		// This will need to be the actual TOTP code generated at the time of testing
		String correctUserCode = service.getTOTPCode(secretKey);
		String incorrectUserCode = "123456";

		// Act & Assert
		assertTrue(service.validateAuthenticationCode(correctUserCode, secretKey), "Correct user code should validate as true.");
		assertFalse(service.validateAuthenticationCode(incorrectUserCode, secretKey), "Incorrect user code should validate as false.");
	}

}
