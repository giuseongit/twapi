package com.codingchallenge.twapi.mappers;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import com.codingchallenge.twapi.dto.PublicUserDTO;

public class PublicUserMapper implements RowMapper{
	
	@Override
	public PublicUserDTO mapRow(ResultSet set, int ro) throws SQLException {
		PublicUserDTO user = new PublicUserDTO();
		
		user.setUsername(set.getString("username"));
		user.setName(set.getString("name")+" "+set.getString("surname"));
		
		return user;
	}
}
