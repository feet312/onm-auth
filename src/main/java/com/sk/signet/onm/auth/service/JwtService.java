package com.sk.signet.onm.auth.service;

import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.sk.signet.onm.common.exception.UnauthorizedException;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.extern.slf4j.Slf4j;
 
@Slf4j
@Service("jwtService")
public class JwtService {

	@Value("${jwt.secret}")
    private String SECRET;  // "sksignet_onm_service"
	
	@Value("${jwt.expiredTime}")
    private Long expiredTime;  // 3600000 millisecond 

	 
	public <T> String create(String key, T data, String subject){
		String jwt = Jwts.builder()
						 .setHeaderParam("typ", "JWT")
						 .setHeaderParam("regDate", System.currentTimeMillis())
						 .setSubject(subject)
						 .claim(key, data)
						 .setExpiration(new Date(System.currentTimeMillis()+ 1 * expiredTime)) //30분
						 .signWith(SignatureAlgorithm.HS256, SECRET)
						 .compact();
		return jwt;
	}
	
	
	private byte[] generateKey(){
		byte[] key = null;
		try {
			key = SECRET.getBytes("UTF-8");
		} catch (UnsupportedEncodingException e) {
			if(log.isInfoEnabled()){
				e.printStackTrace();
			}else{
				log.error("Making JWT Key Error ::: {}", e.getMessage());
			}
		}
		
		return key;
	}
	
	
	public String refreshToken(String jwt) {
		try{
//		    Claims claims = Jwts.parser().setSigningKey(this.generateKey()).parseClaimsJws(jwt).getBody();
		    Claims claims = Jwts.parser().setSigningKey(SECRET).parseClaimsJws(jwt).getBody();
			
			log.info("ID: " + claims.getId());
			log.info("Subject: " + claims.getSubject());
			log.info("memberinfo: " + claims.get("data").toString());
			log.info("Expiration: " + claims.getExpiration());
			
			String subject = claims.getSubject();
			Map<String,Object> data=(Map<String,Object>)claims.get("data");
	
			String token = this.create("data", data, subject);			
			claims.clear(); //기존 생성 토큰 제거(무조건 신규 생성 함) 
			return token;
			
		}catch (Exception e) {
			
			if(log.isInfoEnabled()){
				e.printStackTrace();
			}else{
				log.error(e.getMessage());
			}
			throw new UnauthorizedException();			 
		}
	}
	

	public boolean isUsable(String jwt) {
		try{
		    Jws<Claims> claims = Jwts.parser().setSigningKey(SECRET).parseClaimsJws(jwt);
			return true;
			
		}catch (Exception e) {
			
			if(log.isInfoEnabled()){
				e.printStackTrace();
			}else{
				log.error(e.getMessage());
			}
			throw new UnauthorizedException();			 
		}
	}
		
	
	public Map<String, Object> get(String key) {
		HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
		String jwt = request.getHeader(HttpHeaders.AUTHORIZATION);
				
		Jws<Claims> claims = null;
		try {
			claims = Jwts.parser().setSigningKey(SECRET.getBytes("UTF-8")).parseClaimsJws(jwt);
		} catch (Exception e) {
			if(log.isInfoEnabled()){
				e.printStackTrace();
			}else{
				log.error(e.getMessage());
			}
			throw new UnauthorizedException();
			
			/*개발환경
			Map<String,Object> testMap = new HashMap<>();
			testMap.put("memberId", 2);
			return testMap;*/
		}
		@SuppressWarnings("unchecked")
		Map<String, Object> value = (LinkedHashMap<String, Object>)claims.getBody().get(key);
		return value;
	}
	
	public Map<String, Object> csGet(String key,String jwt) {
		Jws<Claims> claims = null;
		try {
			claims = Jwts.parser().setSigningKey(SECRET.getBytes("UTF-8")).parseClaimsJws(jwt);
		} catch (Exception e) {
			if(log.isInfoEnabled()){
				e.printStackTrace();
			}else{
				log.error(e.getMessage());
			}
			throw new UnauthorizedException();
		}
		@SuppressWarnings("unchecked")
		Map<String, Object> value = (LinkedHashMap<String, Object>)claims.getBody().get(key);
		return value;
	}
	
	public boolean getExpToken(String jwt) {
		try {
		    Jws<Claims> claims = Jwts.parser().setSigningKey(SECRET).parseClaimsJws(jwt);
			Date exp=claims.getBody().getExpiration();
			Date now = new Date();
		
			if(exp.after(now)) {
				return true;
			}
			return false;
		}catch(Exception ext) {
			log.info("exception expire..");
			
			ext.printStackTrace();
			return false;
		}
	}
	
	
	//** 사용자 정보 추가 하시오..
	public String getCommanyId() {
		return (String)this.get("userInfo").get("companyId");
	}

	public String getUserId() {
		// TODO Auto-generated method stub
		return  (String)this.get("userInfo").get("userId");
	}
	
	
	
}
