package com.codingchallenge.twapi.authentication;

import org.jose4j.jwk.RsaJsonWebKey;
import org.jose4j.jwk.RsaJwkGenerator;
import org.jose4j.jws.AlgorithmIdentifiers;
import org.jose4j.jws.JsonWebSignature;
import org.jose4j.jwt.JwtClaims;
import org.jose4j.jwt.MalformedClaimException;
import org.jose4j.jwt.consumer.InvalidJwtException;
import org.jose4j.jwt.consumer.JwtConsumer;
import org.jose4j.jwt.consumer.JwtConsumerBuilder;
import org.jose4j.lang.JoseException;
import org.springframework.stereotype.Component;

import com.codingchallenge.twapi.exceptions.InvalidTokenException;

/**
 * Class for token handling
 * 
 */
@Component
public class TokenHelper {
	
	private static final String KEY_ID = "key"; // Secret key for the token
	private static final String ISSUER = "CodingChallengeApi"; // Token issuer
	private static final float EXPIRATION_MINUTES = (60 * 24); // 1 day
	
	private RsaJsonWebKey key;
	private JwtClaims claims;
	
	public TokenHelper() throws JoseException{
		key = RsaJwkGenerator.generateJwk(2048);
		key.setKeyId(KEY_ID);
	}
	
	public String generateToken(int idToSave) throws JoseException{
		claims = new JwtClaims();
		
		claims.setIssuer(ISSUER);
		claims.setExpirationTimeMinutesInTheFuture(EXPIRATION_MINUTES);
		claims.setSubject(""+idToSave); // Save the user id into the token
		
		JsonWebSignature jws = new JsonWebSignature();
		
		jws.setPayload(claims.toJson());
		jws.setKey(key.getPrivateKey());
		
		jws.setKeyIdHeaderValue(key.getKeyId());
		jws.setAlgorithmHeaderValue(AlgorithmIdentifiers.RSA_USING_SHA256);
		
		String token = jws.getCompactSerialization(); // Get the token string
		return token;
	}
	
	public int validateToken(String token) throws InvalidTokenException{
		JwtConsumer consumer = new JwtConsumerBuilder()
	            .setRequireExpirationTime()
	            .setExpectedIssuer(ISSUER)
	            .setVerificationKey(key.getKey())
	            .build();
		
		try {
			claims = consumer.processToClaims(token);
			int receivedId;
			receivedId = Integer.parseInt(claims.getSubject());
			
			return receivedId;
		} catch (NumberFormatException | MalformedClaimException | InvalidJwtException e) {
			throw new InvalidTokenException();
		}
	}
}
