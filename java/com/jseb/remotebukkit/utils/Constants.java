package com.jseb.remotebukkit.utils;

public class Constants {
	// Errors
	public static final int ERROR_AUTHENTICATION = 0;
	public static final int ERROR_INCORRECT_PIN = 1;
	public static final int ERROR_UNKNOWN_REQUEST = 2;
	public static final int ERROR_PLAYER_NOT_FOUND = 3;

	// Communication Types
	public static final int REQUEST_RESPONSE = 0;
	public static final int REQUEST_AUTHENTICATION = 1;
	public static final int REQUEST_DATA_REQUEST = 2;
	public static final int REQUEST_COMMAND_REQUEST = 3;
	public static final int REQUEST_PLAYER_INFO = 4;
	public static final int REQUEST_ADMIN_MAIL = 5;
	public static final int REQUEST_WRITE_ADMIN_MAIL = 6;

	// Misc.
	public static final String RESULT_SUCCESS = "success";
	public static final String RESULT_FAILURE = "fail";
}