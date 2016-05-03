package com.codingchallenge.twapi.mappers;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import com.codingchallenge.twapi.dto.TweetDTO;

public class TweetMapper implements RowMapper{

	@Override
	public TweetDTO mapRow(ResultSet set, int row) throws SQLException {
		TweetDTO tweet = new TweetDTO();
		
		tweet.setUsername(set.getString("username"));
		tweet.setName(set.getString("name")+" "+set.getString("surname"));
		tweet.setDate(set.getString("date"));
		tweet.setText(set.getString("text"));
		
		return tweet;
	}

}
