package com.codingchallenge.twapi.responses;

public class AuthenticationResponse extends GenericResponse {
	private String message;

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}
}
