package com.codingchallenge.twapi.dao;

import java.util.List;

import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import com.codingchallenge.twapi.dto.PublicUserDTO;
import com.codingchallenge.twapi.dto.UserDTO;
import com.codingchallenge.twapi.exceptions.UserAlreadyFollowedException;
import com.codingchallenge.twapi.exceptions.UserNotFollowedException;
import com.codingchallenge.twapi.exceptions.UserNotFoundException;
import com.codingchallenge.twapi.mappers.PublicUserMapper;
import com.codingchallenge.twapi.mappers.UserMapper;
import com.mysql.jdbc.exceptions.jdbc4.MySQLIntegrityConstraintViolationException;

public class UserDAO {
	private NamedParameterJdbcTemplate jdbcTemplate;
	
	public UserDAO(NamedParameterJdbcTemplate jdbcTemplate){
		this.jdbcTemplate = jdbcTemplate;
	}
	
	/**
	 * Create a new user
	 * 
	 * @param username
	 * @param name
	 * @param surname
	 * @param email
	 * @param password
	 * @throws MySQLIntegrityConstraintViolationException
	 */
	public void create(String username, String name, String surname, String email, String password) throws MySQLIntegrityConstraintViolationException{
		String sql = "INSERT INTO User(username, name, surname, email, password) VALUES(:username, :name, :surname, :email, :password)";
		MapSqlParameterSource params = new MapSqlParameterSource();
		params.addValue("username", username);
		params.addValue("name", name);
		params.addValue("surname", surname);
		params.addValue("email", email);
		params.addValue("password", DigestUtils.sha1Hex(password));
		jdbcTemplate.update(sql, params);
	}
	
	/**
	 * Given an id, returns the Object representing the user's record on the database
	 * @param id
	 * @return
	 */
	public UserDTO getUser(int id){
		String sql = "SELECT * FROM User WHERE id = :id";
		MapSqlParameterSource params = new MapSqlParameterSource();
		params.addValue("id", id);
		UserDTO user = (UserDTO) jdbcTemplate.queryForObject(sql, params, new UserMapper());
		return user;
	}
	
	
	/**
	 * Modify the record on the database with the new information stored in the object
	 * @param user
	 */
	public void update(UserDTO user){
		String sql = "UPDATE User SET username = :username, name = :name, surname = :surname, email = :email, password = :password WHERE id = :id";
		MapSqlParameterSource params = new MapSqlParameterSource();
		params.addValue("id", user.getId());
		params.addValue("username", user.getUsername());
		params.addValue("name", user.getName());
		params.addValue("surname", user.getSurname());
		params.addValue("email", user.getEmail());
		params.addValue("password", user.getPassword());
		jdbcTemplate.update(sql, params);
	}
	
	/**
	 * Given the username/password pair, this method retrieves the corrisponding object representing the user or, if
	 * incorrect credentials are given, returns null.
	 * 
	 * @param username
	 * @param password
	 * @return
	 * @throws UserNotFoundException 
	 */
	public UserDTO getUserFromCredentials(String username, String password) throws UserNotFoundException{
		String sql = "SELECT * FROM User WHERE username = :username AND password = :password";
		MapSqlParameterSource params = new MapSqlParameterSource();
		params.addValue("username", username);
		params.addValue("password", DigestUtils.sha1Hex(password));
		UserDTO user = null;
		try{
			user = (UserDTO) jdbcTemplate.queryForObject(sql, params, new UserMapper());
		}catch(EmptyResultDataAccessException e){
			throw new UserNotFoundException();
		}
		return user;
	}
	
	/**
	 * Stores on the database the "follow" relationship
	 * 
	 * @param userId The follower (user) id
	 * @param username The username of the one who's been followed
	 * @throws UserAlreadyFollowedException This means that there is already a followinf relationship between the users
	 * @throws UserNotFoundException
	 */
	public void follow(int userId, String username) throws UserAlreadyFollowedException, UserNotFoundException{
		String sql = "SELECT * FROM User WHERE username = :username"; // query to retrieve the id from the gien username
		MapSqlParameterSource params = new MapSqlParameterSource();
		params.addValue("username", username);
		
		UserDTO toFollow = null;
		
		try{
			toFollow = (UserDTO) jdbcTemplate.queryForObject(sql, params, new UserMapper());
		}catch(EmptyResultDataAccessException e){
			throw new UserNotFoundException();
		}
		
		
		sql = "SELECT count(*) FROM Follows WHERE follower = :follower AND followed = :followed"; // Query to check if there's already a relationship between the users 
		params = new MapSqlParameterSource();
		params.addValue("follower", userId);
		params.addValue("followed", toFollow.getId());
		
		int count = jdbcTemplate.queryForObject(sql, params, Integer.class);
		
		if(count > 0){
			throw new UserAlreadyFollowedException();
		}else{
			sql = "INSERT INTO Follows (follower, followed) VALUES (:follower, :followed)";
			jdbcTemplate.update(sql, params);
		}
	}
	
	/**
	 * Deletes from database the "follow" relationship
	 * 
	 * @param userId
	 * @param username
	 * @throws UserNotFoundException
	 * @throws UserNotFollowedException
	 */
	public void unfollow(int userId, String username) throws UserNotFoundException, UserNotFollowedException{
		String sql = "SELECT * FROM User WHERE username = :username";
		MapSqlParameterSource params = new MapSqlParameterSource();
		params.addValue("username", username);
		
		UserDTO toUnFollow = null;
		
		try{
			toUnFollow = (UserDTO) jdbcTemplate.queryForObject(sql, params, new UserMapper());
		}catch(EmptyResultDataAccessException e){
			throw new UserNotFoundException();
		}
		
		sql = "SELECT count(*) FROM Follows WHERE follower = :follower AND followed = :followed";
		params = new MapSqlParameterSource();
		params.addValue("follower", userId);
		params.addValue("followed", toUnFollow.getId());
		
		int count = jdbcTemplate.queryForObject(sql, params, Integer.class);
		
		if(count == 0){
			throw new UserNotFollowedException();
		}else{
			sql = "DELETE FROM Follows WHERE follower = :follower AND followed = :followed";
			jdbcTemplate.update(sql, params);
		}
	}
	
	/**
	 * Get a list of the people that follow the user
	 * @param userId
	 * @return
	 * @throws UserNotFoundException 
	 */
	public List<PublicUserDTO> listFollowers(int userId, String username) throws UserNotFoundException{
		int id = userId;
		if(username != null){
			String sql = "SELECT * FROM User WHERE username = :username";
			MapSqlParameterSource params = new MapSqlParameterSource();
			params.addValue("username", username);
			
			UserDTO user = null;
			
			try{
				user = (UserDTO) jdbcTemplate.queryForObject(sql, params, new UserMapper());
				id = user.getId();
			}catch(EmptyResultDataAccessException e){
				throw new UserNotFoundException();
			}
		}
		
		String sql = "SELECT User.* FROM User, Follows WHERE User.id = Follows.follower AND Follows.followed = :id";
		MapSqlParameterSource params = new MapSqlParameterSource();
		params.addValue("id", id);
		List<PublicUserDTO> users = (List<PublicUserDTO>) jdbcTemplate.query(sql, params, new PublicUserMapper());
		return users;
	}
	
	/**
	 * Get a list of the people a user is following
	 * @param userId
	 * @return
	 * @throws UserNotFoundException 
	 */
	public List<PublicUserDTO> listFollowing(int userId, String username) throws UserNotFoundException{
		int id = userId;
		if(username != null){
			String sql = "SELECT * FROM User WHERE username = :username";
			MapSqlParameterSource params = new MapSqlParameterSource();
			params.addValue("username", username);
			
			UserDTO user = null;
			
			try{
				user = (UserDTO) jdbcTemplate.queryForObject(sql, params, new UserMapper());
				id = user.getId();
			}catch(EmptyResultDataAccessException e){
				throw new UserNotFoundException();
			}
		}
		
		String sql = "SELECT User.* FROM User, Follows WHERE User.id = Follows.followed AND Follows.follower = :id";
		MapSqlParameterSource params = new MapSqlParameterSource();
		params.addValue("id", id);
		List<PublicUserDTO> users = (List<PublicUserDTO>) jdbcTemplate.query(sql, params, new PublicUserMapper());
		return users;
	}
}







