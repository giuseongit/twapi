package com.codingchallenge.twapi.requests;

public class TweetListRequest extends GenericRequest{
	private String search;

	public String getSearch() {
		return search;
	}

	public void setSearch(String search) {
		this.search = search;
	}
}
