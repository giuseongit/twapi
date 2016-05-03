package com.codingchallenge.twapi.responses;

import java.util.List;

import com.codingchallenge.twapi.dto.PublicUserDTO;

public class UserListResponse extends GenericResponse {
	private List<PublicUserDTO> list;

	public List<PublicUserDTO> getList() {
		return list;
	}

	public void setList(List<PublicUserDTO> list) {
		this.list = list;
	}
}
