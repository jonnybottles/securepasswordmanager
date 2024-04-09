package com.jgm.securepasswordmanager.utils;

public class DirectoryPath {
	public static final String USERS_DIRECTORY = "data/user_data";
	public static final String USERS_MASTER_KEY = "data/user_master_keys";
	public static final String QR_CODE_DIRECTORY = "data/qr_codes";
	public static final String LOGS_DIRECTORY = "data/logs";

	public static final String TEST_USERS_DIRECTORY = "test_data/user_data";
	public static final String TEST_USERS_MASTER_KEY = "test_data/user_master_keys";
	public static final String TEST_QR_CODE_DIRECTORY = "test_data/qr_codes";
	public static final String TEST_LOGS_DIRECTORY = "test_data/logs";

	private String path;

	public DirectoryPath(String path) {
		this.path = path;
	}

	public String getPath() {
		return this.path;
	}
}
