package com.codingchallenge.twapi.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import com.codingchallenge.twapi.authentication.TokenHelper;

/**
 * Base class for controllers, it has the fields automatically initialized by spring
 *
 */
public class BasicController {
	@Autowired
	protected NamedParameterJdbcTemplate jdbcTemplate;
	@Autowired
	protected TokenHelper tokenHelper;
}
