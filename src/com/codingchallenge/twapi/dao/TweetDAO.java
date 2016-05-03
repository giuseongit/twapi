package com.codingchallenge.twapi.dao;

import java.util.List; 

import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import com.codingchallenge.twapi.dto.TweetDTO;
import com.codingchallenge.twapi.mappers.TweetMapper;

public class TweetDAO {
	private NamedParameterJdbcTemplate jdbcTemplate;
	
	public TweetDAO(NamedParameterJdbcTemplate jdbcTemplate){
		this.jdbcTemplate = jdbcTemplate;
	}
	
	/**
	 * Create a new tweet
	 * 
	 * @param text The text of the tweet
	 * @param userId the id of th user who is tweeting
	 */
	public void create(String text, int userId){
		String sql = "INSERT INTO Tweet(date, text, user_id) VALUES(NOW(), :text, :user)";
		MapSqlParameterSource params = new MapSqlParameterSource();
		params.addValue("text", text);
		params.addValue("user", userId);
		jdbcTemplate.update(sql, params);
	}
	
	/**
	 * Given a userId and a string to search, this method returns all the tweets from the selected user
	 * and the users followed by the selected one
	 * 
	 * @param userId The id of the user
	 * @param search A search string, it can be null
	 * @return	A list of tweets
	 */
	public List<TweetDTO> getTweets(int userId, String search){
		String sql = "SELECT * FROM Tweet t, User u WHERE t.user_id = u.id AND (u.id IN (SELECT u.id FROM User u, Follows f WHERE u.id = f.followed AND f.follower = :id) OR u.id = :id)";
		if(search != null){
			sql += " AND t.text LIKE :search";
		}
		sql += " ORDER BY t.date DESC";
		MapSqlParameterSource params = new MapSqlParameterSource();
		params.addValue("id", userId);
		if(search != null){
			params.addValue("search", "%"+search+"%");
		}
		List<TweetDTO> tweets = (List<TweetDTO>) jdbcTemplate.query(sql, params, new TweetMapper());
		return tweets;
	}
}











