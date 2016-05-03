package com.codingchallenge.twapi.responses;

import java.util.List;

import com.codingchallenge.twapi.dto.TweetDTO;

public class TweetListResponse extends GenericResponse {
	private List<TweetDTO> list;

	public List<TweetDTO> getList() {
		return list;
	}

	public void setList(List<TweetDTO> list) {
		this.list = list;
	}
}
