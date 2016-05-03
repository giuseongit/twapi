package com.codingchallenge.twapi.controllers;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.codingchallenge.twapi.dao.UserDAO;
import com.codingchallenge.twapi.dto.PublicUserDTO;
import com.codingchallenge.twapi.exceptions.InvalidTokenException;
import com.codingchallenge.twapi.exceptions.UserAlreadyFollowedException;
import com.codingchallenge.twapi.exceptions.UserNotFollowedException;
import com.codingchallenge.twapi.exceptions.UserNotFoundException;
import com.codingchallenge.twapi.requests.UserInteractionRequest;
import com.codingchallenge.twapi.responses.GenericResponse;
import com.codingchallenge.twapi.responses.ResponseStatuses;
import com.codingchallenge.twapi.responses.UserListResponse;

/**
 * Class for user actions
 *
 */
@RestController
public class UserController extends BasicController {
	
	/**
	 * "Follow" action
	 * @param request
	 * @return
	 */
	@RequestMapping(
			 headers = { "Content-type=application/json" },
			 method = RequestMethod.POST, value = "/follow")
	 public ResponseEntity<GenericResponse> follow(@RequestBody UserInteractionRequest request){
		String token = request.getToken();
		String userToFollow = request.getUsername();
		
		GenericResponse resp = new GenericResponse();
		
		ResponseEntity<GenericResponse> respEnt;
		
		boolean complete = token != null && userToFollow != null;
		
		if(!complete){
			 // If there are missing fields, an error message is returned
			 resp.setStatus(ResponseStatuses.MISSING_FIELDS);
			 respEnt = ResponseEntity.ok(resp);
		}else{
			try {
				int userId = tokenHelper.validateToken(token);
				
				UserDAO uDao = new UserDAO(jdbcTemplate);
				
				try {
					uDao.follow(userId, userToFollow);
					
					resp.setStatus(ResponseStatuses.OK);
					respEnt = ResponseEntity.ok(resp);
				} catch (UserAlreadyFollowedException e) {
					resp.setStatus(ResponseStatuses.USER_ALREADY_FOLLOWED);
					respEnt = ResponseEntity.ok(resp);
				} catch (UserNotFoundException e) {
					resp.setStatus(ResponseStatuses.USER_NOT_FOUND);
					respEnt = ResponseEntity.ok(resp);
				}
				
			} catch (InvalidTokenException e) {
				resp.setStatus(ResponseStatuses.AUTHENTICATION_REQUIRED);
				respEnt = ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(resp);
			}
		}
		
		
		
		return respEnt;
	}
	
	/**
	 * "Unfollow" action
	 * @param request
	 * @return
	 */
	@RequestMapping(
			 headers = { "Content-type=application/json" },
			 method = RequestMethod.POST, value = "/unfollow")
	 public ResponseEntity<GenericResponse> unfollow(@RequestBody UserInteractionRequest request){
		String token = request.getToken();
		String userToUnFollow = request.getUsername();
		
		GenericResponse resp = new GenericResponse();
		
		ResponseEntity<GenericResponse> respEnt;
		
		boolean complete = token != null && userToUnFollow != null;
		
		if(!complete){
			 // If there are missing fields, an error message is returned
			 resp.setStatus(ResponseStatuses.MISSING_FIELDS);
			 respEnt = ResponseEntity.ok(resp);
		}else{
			try {
				int userId = tokenHelper.validateToken(token);
				
				UserDAO uDao = new UserDAO(jdbcTemplate);
				
				try {
					uDao.unfollow(userId, userToUnFollow);
					
					resp.setStatus(ResponseStatuses.OK);
					respEnt = ResponseEntity.ok(resp);
				} catch (UserNotFollowedException e) {
					resp.setStatus(ResponseStatuses.USER_ALREADY_FOLLOWED);
					respEnt = ResponseEntity.ok(resp);
				} catch (UserNotFoundException e) {
					resp.setStatus(ResponseStatuses.USER_NOT_FOUND);
					respEnt = ResponseEntity.ok(resp);
				}
				
			} catch (InvalidTokenException e) {
				resp.setStatus(ResponseStatuses.AUTHENTICATION_REQUIRED);
				respEnt = ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(resp);
			}
		}
		
		
		return respEnt;
	}
	
	/**
	 * Get a user's followers
	 * @param request
	 * @return
	 */
	@RequestMapping(
			 headers = { "Content-type=application/json" },
			 method = RequestMethod.POST, value = "/followers")
	 public ResponseEntity<UserListResponse> followers(@RequestBody UserInteractionRequest request){
		String token = request.getToken();
		String username = request.getUsername();
		
		UserListResponse resp = new UserListResponse();
		
		ResponseEntity<UserListResponse> respEnt;
		
		try {
			int userId = tokenHelper.validateToken(token);
			
			UserDAO uDao = new UserDAO(jdbcTemplate);
			
			List<PublicUserDTO> followers;
			try {
				followers = uDao.listFollowers(userId, username);
				resp.setStatus(ResponseStatuses.OK);
				resp.setList(followers);
				respEnt = ResponseEntity.ok(resp);
			} catch (UserNotFoundException e) {
				resp.setStatus(ResponseStatuses.USER_NOT_FOUND);
				respEnt = ResponseEntity.ok(resp);
			}
			
		} catch (InvalidTokenException e) {
			resp.setStatus(ResponseStatuses.AUTHENTICATION_REQUIRED);
			respEnt = ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(resp);
		}
		
		return respEnt;
	}
	
	/**
	 * Get the users that the selected user is following
	 * @param request
	 * @return
	 */
	@RequestMapping(
			 headers = { "Content-type=application/json" },
			 method = RequestMethod.POST, value = "/following")
	 public ResponseEntity<UserListResponse> following(@RequestBody UserInteractionRequest request){
		String token = request.getToken();
		String username = request.getUsername();
		
		UserListResponse resp = new UserListResponse();
		
		ResponseEntity<UserListResponse> respEnt;
		
		try {
			int userId = tokenHelper.validateToken(token);
			
			UserDAO uDao = new UserDAO(jdbcTemplate);
			
			List<PublicUserDTO> following;
			try {
				following = uDao.listFollowing(userId, username);
				resp.setStatus(ResponseStatuses.OK);
				resp.setList(following);
				respEnt = ResponseEntity.ok(resp);
			} catch (UserNotFoundException e) {
				resp.setStatus(ResponseStatuses.USER_NOT_FOUND);
				respEnt = ResponseEntity.ok(resp);
			}
			
		} catch (InvalidTokenException e) {
			resp.setStatus(ResponseStatuses.AUTHENTICATION_REQUIRED);
			respEnt = ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(resp);
		}
		
		return respEnt;
	}
}
