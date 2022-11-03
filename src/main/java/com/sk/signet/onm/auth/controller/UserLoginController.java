package com.sk.signet.onm.auth.controller;

import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sk.signet.onm.auth.service.JwtService;
import com.sk.signet.onm.auth.service.UserLoginService;
import com.sk.signet.onm.common.exception.UnauthorizedException;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Tag(name="사용자 인증", description="사용자 인증 관련 API입니다.")
@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
@Slf4j
public class UserLoginController {

	@Autowired
	private JwtService jwtService;
	@Autowired
	private UserLoginService userLoginService;
	
	@PostMapping(value = "/login")
	public ResponseEntity<Map<String, Object>> loginCheck(@RequestBody Map<String, Object> data,
			  HttpServletRequest request,
			  HttpServletResponse response) throws UnknownHostException {
		
		Map<String, Object> rcvData=new HashMap<>();
		
		try {
			/**
			 * TEST Data
			 * {
				  "userId": "INSOFT1",
				  "userPassword": "InSOFT1!@#$"
				}
			 */
			Map<String, Object> loginMap = userLoginService.login(data);
			if(loginMap != null) {
				// TODO: 나중에 로그인 이력저장 주석을 제거하자!
				// 로그인 이력저장
//				userLoginService.insertLoginHist(loginMap);
				
				// JWT 토큰 생성 
				String token = jwtService.create("data", loginMap, "user");
				response.setHeader("Access-Control-Expose-Headers", HttpHeaders.AUTHORIZATION);
				response.setHeader(HttpHeaders.AUTHORIZATION, token);
				rcvData.put("data","success");
			} else {
				rcvData.put("data","fail");
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return new ResponseEntity<Map<String, Object>>(rcvData, HttpStatus.OK);
	}
	
	@PostMapping(value = "/authCheck")
	public ResponseEntity<Map<String, Object>> authCheck(@RequestBody Map<String, Object> data,
				HttpServletRequest request,
				HttpServletResponse response) throws UnknownHostException {
		
		Map<String, Object> rcvData=new HashMap<>();
		
		log.info("onm-auth userAuthCheck reqData : {}", data);
		
		try {
			String token = request.getHeader(HttpHeaders.AUTHORIZATION);
			
			if(null != token) {
				log.info("get token: {}", token);
				if(Pattern.matches("^Bearer .*", token)) {
					token = token.replaceAll("^Bearer( )*", "");
					log.info("token replace: {}", token);
				}
				
				if(jwtService.isUsable(token) && jwtService.getExpToken(token)){
					
					String newToken = jwtService.refreshToken(token);
					response.setHeader("Access-Control-Expose-Headers", HttpHeaders.AUTHORIZATION);
					response.setHeader(HttpHeaders.AUTHORIZATION ,newToken); //요청 웹브라우즈에 새로 생성한 token 되돌려줌.
					rcvData.put("data","success");
				}else{
					rcvData.put("data","Unauthorized");
//					throw new UnauthorizedException();
				}
			} else {
				rcvData.put("data","fail");
			}
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return new ResponseEntity<Map<String, Object>>(rcvData, HttpStatus.OK);
	}
}
