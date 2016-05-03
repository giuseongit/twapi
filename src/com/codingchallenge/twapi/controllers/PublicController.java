package com.codingchallenge.twapi.controllers;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jose4j.lang.JoseException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.codingchallenge.twapi.dao.UserDAO;
import com.codingchallenge.twapi.dto.UserDTO;
import com.codingchallenge.twapi.exceptions.UserNotFoundException;
import com.codingchallenge.twapi.requests.LoginRequest;
import com.codingchallenge.twapi.requests.RegisterRequest;
import com.codingchallenge.twapi.responses.AuthenticationResponse;
import com.codingchallenge.twapi.responses.ResponseStatuses;
import com.mysql.jdbc.exceptions.jdbc4.MySQLIntegrityConstraintViolationException;

/**
 * Controller which handles the public actions that do not require an access token (registration and login)
 *
 */
@RestController
public class PublicController extends BasicController{
	 
	/**
	 * Register a user
	 * @param request
	 * @return
	 */
	 @RequestMapping(
			 headers = { "Content-type=application/json" },
			 method = RequestMethod.POST, value = "/register")
	 public AuthenticationResponse register(@RequestBody RegisterRequest request){
		 String username = request.getUsername();
		 String name = request.getName();
		 String surname = request.getSurname();
		 String email = request.getEmail();
		 String password = request.getPassword();
		 
		 // Check if some fields are missing
		 boolean complete = username != null && name != null && surname != null && email != null && password != null;
		 
		 AuthenticationResponse resp = new AuthenticationResponse();
		 
		 if(!complete){
			 // If there are missing fields, an error message is returned
			 resp.setStatus(ResponseStatuses.MISSING_FIELDS);
		 }else{
			 UserDAO uDao = new UserDAO(jdbcTemplate);
			 
			 // As there are two unique keys in the users table (username and email)
			 // this regex gets the duplicate column's name
			 Pattern p = Pattern.compile(".*Duplicate entry '.*' for key '([a-z]*)'");
			 
			 try{
				 uDao.create(username, name, surname, email, password);
				 resp.setStatus(ResponseStatuses.OK);
			 }catch(DuplicateKeyException e){
				 Matcher m = p.matcher(e.getMessage());
				 ResponseStatuses status = ResponseStatuses.ERROR;
				 if(m.find()){
					 resp.setMessage("Error, given "+m.group(1)+" already exist!");
				 }
				 resp.setStatus(status);
			 } catch (MySQLIntegrityConstraintViolationException e) {
				 ResponseStatuses status = ResponseStatuses.ERROR;
				 resp.setStatus(status);
			}
		 }
		 
		 return resp;
	 }
	 
	 /**
	  * Login handler
	  * @param request
	  * @return
	  * @throws JoseException
	  */
	 @RequestMapping(
			 headers = { "Content-type=application/json" },
			 method = RequestMethod.POST, value = "/login")
	 public AuthenticationResponse register(@RequestBody LoginRequest request) throws JoseException{
		 String username = request.getUsername();
		 String password = request.getPassword();
		 
		 boolean complete = username != null && password != null;
		 
		 AuthenticationResponse resp = new AuthenticationResponse();
		 
		 if(!complete){
			 resp.setStatus(ResponseStatuses.MISSING_FIELDS);
		 }else{
			 UserDAO uDao = new UserDAO(jdbcTemplate);
			 
			 try{
				 UserDTO user = uDao.getUserFromCredentials(username, password);
				 
				 String token = tokenHelper.generateToken(user.getId());
				 resp.setStatus(ResponseStatuses.OK);
				 resp.setMessage(token);
			 }catch(UserNotFoundException e){
				 resp.setStatus(ResponseStatuses.WRONG_CREDENTIALS);
			 }
		 }
		 
		 return resp;
	 }
	 
}
