package com.codingchallenge.twapi.controllers;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.codingchallenge.twapi.dao.TweetDAO;
import com.codingchallenge.twapi.dto.TweetDTO;
import com.codingchallenge.twapi.exceptions.InvalidTokenException;
import com.codingchallenge.twapi.requests.TweetListRequest;
import com.codingchallenge.twapi.requests.TweetRequest;
import com.codingchallenge.twapi.responses.GenericResponse;
import com.codingchallenge.twapi.responses.ResponseStatuses;
import com.codingchallenge.twapi.responses.TweetListResponse;

/**
 * Class referred to tweets action
 *
 */
@RestController
public class TweetController extends BasicController {
	
	/**
	 * Posts a tweet
	 * @param request
	 * @return
	 */
	@RequestMapping(
			 headers = { "Content-type=application/json" },
			 method = RequestMethod.POST, value = "/tweet")
	 public ResponseEntity<GenericResponse> tweet(@RequestBody TweetRequest request){
		String token = request.getToken();
		String tweet = request.getTweet();
		
		GenericResponse resp = new GenericResponse();
		
		ResponseEntity<GenericResponse> respEnt;
		
		boolean complete = token != null && tweet != null;
		
		if(!complete){
			 // If there are missing fields, an error message is returned
			 resp.setStatus(ResponseStatuses.MISSING_FIELDS);
			 respEnt = ResponseEntity.ok(resp);
		}else{
			try {
				int userId = tokenHelper.validateToken(token);
				
				TweetDAO tDao = new TweetDAO(jdbcTemplate);
				
				tDao.create(tweet, userId);
					
				resp.setStatus(ResponseStatuses.OK);
				respEnt = ResponseEntity.ok(resp);
				
			} catch (InvalidTokenException e) {
				resp.setStatus(ResponseStatuses.AUTHENTICATION_REQUIRED);
				respEnt = ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(resp);
			}
		}
		
		
		return respEnt;
	}
	
	/**
	 * Gets a list of tweets
	 * @param request
	 * @return
	 */
	@RequestMapping(
			 headers = { "Content-type=application/json" },
			 method = RequestMethod.POST, value = "/get-tweets")
	 public ResponseEntity<TweetListResponse> getTwets(@RequestBody TweetListRequest request){
		String token = request.getToken();
		String search = request.getSearch();
		
		TweetListResponse resp = new TweetListResponse();
		
		ResponseEntity<TweetListResponse> respEnt;
		
		try {
			int userId = tokenHelper.validateToken(token);
			
			TweetDAO tDao = new TweetDAO(jdbcTemplate);
			
			List<TweetDTO> tweets = tDao.getTweets(userId, search);
				
			resp.setStatus(ResponseStatuses.OK);
			resp.setList(tweets);
			respEnt = ResponseEntity.ok(resp);
			
		} catch (InvalidTokenException e) {
			resp.setStatus(ResponseStatuses.AUTHENTICATION_REQUIRED);
			respEnt = ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(resp);
		}
		
		return respEnt;
	}
}
