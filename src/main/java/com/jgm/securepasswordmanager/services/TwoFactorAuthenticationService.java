package com.jgm.securepasswordmanager.services;


import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
//import de.taimos.totp.TOTP;
import de.taimos.totp.TOTP;
import org.apache.commons.codec.binary.Base32;
import org.apache.commons.codec.binary.Hex;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.security.SecureRandom;

public class TwoFactorAuthenticationService {

	private static final Base32 theBase32 = new Base32();



	public String getGoogleAuthenticatorBarCode(String secretKey, String emailAddress, String issuer) {
//		byte[] bytes = secretKey.getBytes();
//		String base32EncodedSecretKey = theBase32.encodeToString(bytes);
		try {
			return "otpauth://totp/"
					+ URLEncoder.encode(issuer + ":" + emailAddress, "UTF-8").replace("+", "%20")
					+ "?secret=" + URLEncoder.encode(secretKey, "UTF-8").replace("+", "%20")
					+ "&issuer=" + URLEncoder.encode(issuer, "UTF-8").replace("+", "%20");
		} catch (UnsupportedEncodingException e) {
			throw new IllegalStateException(e);
		}
	}

	public void createQRCode(String barCodeData, String filePath, int height, int width)
			throws WriterException, IOException {
		BitMatrix matrix = new MultiFormatWriter().encode(barCodeData, BarcodeFormat.QR_CODE,
				width, height);
		try (FileOutputStream out = new FileOutputStream(filePath)) {
			MatrixToImageWriter.writeToStream(matrix, "png", out);
		}
	}

	public static String generateSecretKey() {
		SecureRandom random = new SecureRandom();
		byte[] bytes = new byte[10]; // 80 bits
		random.nextBytes(bytes);
		Base32 base32 = new Base32();
		String secretKey = base32.encodeToString(bytes).replace("=", ""); // Remove optional padding
		return secretKey;
	}

	public String getTOTPCode(String secretKey) {
//		Base32 base32 = new Base32();
		byte[] bytes = theBase32.decode(secretKey);
		String hexKey = Hex.encodeHexString(bytes);
		return TOTP.getOTP(hexKey);
	}

	public boolean validateAuthenticationCode(String userCode, String secretKey) {
		if (userCode.equals(getTOTPCode(secretKey))) {
			return true;
		}
		return false;
	}

}
