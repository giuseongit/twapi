package com.codingchallenge.twapi.requests;

public class TweetRequest extends GenericRequest{
	private String tweet;

	public String getTweet() {
		return tweet;
	}

	public void setTweet(String tweet) {
		this.tweet = tweet;
	}
}
