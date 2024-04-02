package com.jgm.securepasswordmanager.services;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;

class TwoFactorAuthenticationServiceTest {

	@Test
	void testGetGoogleAuthenticatorBarCode() {
		// Arrange
		String secretKey = "QDWSM3OYBPGTEVSPB5FKVDM3CSNCWHVK";
		String emailAddress = "test@gmail.com";
		String issuer = "Butler Cyber Technologies";
		String expectedBarCode = "otpauth://totp/Butler%20Cyber%20Technologies%3Atest%40gmail.com?secret=QDWSM3OYBPGTEVSPB5FKVDM3CSNCWHVK&issuer=Butler%20Cyber%20Technologies";

		// Act
		String actualBarCode = TwoFactorAuthenticationService.getGoogleAuthenticatorBarCode(secretKey, emailAddress, issuer);

		// Assert
		assertEquals(expectedBarCode, actualBarCode, "The generated Google Authenticator barcode URL does not match the expected value.");
	}

	@Test
	void testCreateQRCode() throws Exception {
		// Arrange
		String barCodeData = TwoFactorAuthenticationService.getGoogleAuthenticatorBarCode(
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

		// Cleanup
		Files.delete(Paths.get(filePath));
	}
}
