package com.codingchallenge.twapi.mappers;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import com.codingchallenge.twapi.dto.UserDTO;

public class UserMapper implements RowMapper{

	@Override
	public UserDTO mapRow(ResultSet set, int ro) throws SQLException {
		UserDTO user = new UserDTO();
		
		user.setId(set.getInt("id"));
		user.setUsername(set.getString("username"));
		user.setName(set.getString("name"));
		user.setSurname(set.getString("surname"));
		user.setEmail(set.getString("email"));
		user.setPassword(set.getString("password"));
		
		return user;
	}

}
