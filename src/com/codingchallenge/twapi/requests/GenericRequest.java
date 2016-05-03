package com.codingchallenge.twapi.requests;

/**
 * Base class for all the requests except for the public ones, which have specific classes.
 * 
 */
public class GenericRequest {
	private String token;

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}
}
