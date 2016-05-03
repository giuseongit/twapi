package com.codingchallenge.twapi.responses;

/**
 * Base class for all responses
 * 
 */
public class GenericResponse {
	private ResponseStatuses status;

	public ResponseStatuses getStatus() {
		return status;
	}

	public void setStatus(ResponseStatuses status) {
		this.status = status;
	}
	
}
