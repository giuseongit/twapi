package com.codingchallenge.twapi.responses;

/**
 * Possible response statuses
 * 
 */
public enum ResponseStatuses {
	OK,
	MISSING_FIELDS,
	ERROR,
	WRONG_CREDENTIALS,
	AUTHENTICATION_REQUIRED,
	USER_ALREADY_FOLLOWED,
	USER_NOT_FOLLOWED,
	USER_NOT_FOUND
}
