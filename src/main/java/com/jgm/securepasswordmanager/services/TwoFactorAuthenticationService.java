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

public class TwoFactorAuthenticationService {



	public String getGoogleAuthenticatorBarCode(String secretKey, String emailAddress, String issuer) {
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

	public String getTOTPCode(String secretKey) {
		Base32 base32 = new Base32();
		byte[] bytes = base32.decode(secretKey);
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
