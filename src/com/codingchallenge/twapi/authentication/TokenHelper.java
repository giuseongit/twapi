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

@Component
public class TokenHelper {
	
	private static final String TOKEN_KEY_ID = "key";
	private static final String TOKEN_ISSUER = "CodingChallengeApi";
	private static final float TOKEN_EXPIRATION_MINUTES = (60 * 24);
	
	public TokenHelper(){}
	
	public String generateToken(int idToSave) throws JoseException{
		JwtClaims jwtClaims = generateJwtClaims( idToSave );
		JsonWebSignature jsonWebSignature = generateJsonWebSignature( jwtClaims );
		String serializedWebSignature = serializeJsonWebSignature( jsonWebSignature );
		return serializedWebSignature;
	}

	private JwtClaims generateJwtClaims( int idToSave ) throws JoseException{
		JwtClaims jwtClaims = new JwtClaims();
		jwtClaims.setIssuer( TOKEN_ISSUER );
		jwtClaims.setExpirationTimeMinutesInTheFuture( TOKEN_EXPIRATION_MINUTES );
		jwtClaims.setSubject( (string)idToSave );
		return jwtClaims;
	}

	private JsonWebSignature generateJsonWebSignature( JwtClaims jwtClaims ) throws JoseException{
		RsaJsonWebKey jsonWebKey = generateJsonWebKey();
		JsonWebSignature jsonWebSignature = new JsonWebSignature();
		jsonWebSignature.setPayload( jwtClaims.toJson() );
		jsonWebSignature.setKey( jsonWebKey.getPrivateKey() );
		jsonWebSignature.setKeyIdHeaderValue( jsonWebKey.getKeyId() );
		jsonWebSignature.setAlgorithmHeaderValue( AlgorithmIdentifiers.RSA_USING_SHA256 );
		return jsonWebSignature;
	}

	private RsaJsonWebKey generateJsonWebKey() throws JoseException{
		RsaJsonWebKey jsonWebKey = RsaJwkGenerator.generateJwk(2048);
		jsonWebKey.setKeyId( TOKEN_KEY_ID );
		return jsonWebKey;
	}

	private String serializeJsonWebSignature( JsonWebSignature jsonWebSignature ){
		return jsonWebSignature.getCompactSerialization();
	}
	
	public int validateToken( String webSignature ) throws InvalidTokenException{
		RsaJsonWebKey jsonWebKey = generateJsonWebKey();

		JwtConsumer jwtConsumer = new JwtConsumerBuilder()
			.setRequireExpirationTime()
			.setExpectedIssuer( TOKEN_ISSUER )
			.setVerificationKey( jsonWebKey.getKey() )
			.build();
		
		try {
			JwtClaims jwtConsumerClaims = jwtConsumer.processToClaims( webSignature );
			int receivedId = Integer.parseInt( jwtConsumerClaims.getSubject() );
			return receivedId;
		} catch (NumberFormatException | MalformedClaimException | InvalidJwtException e) {
			throw new InvalidTokenException();
		}
	}
}
