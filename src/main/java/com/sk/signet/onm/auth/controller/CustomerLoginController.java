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

import com.sk.signet.onm.auth.service.CustomerLoginService;
import com.sk.signet.onm.auth.service.JwtService;
import com.sk.signet.onm.auth.service.UserLoginService;
import com.sk.signet.onm.common.exception.UnauthorizedException;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Tag(name="고객 인증", description="고객 인증 관련 API입니다.")
@RestController
@RequestMapping("/customer")
@RequiredArgsConstructor
@Slf4j
public class CustomerLoginController {

	@Autowired
	private JwtService jwtService;
	@Autowired
	private CustomerLoginService customerLoginService;
	
	private static final String HEADER_AUTH = HttpHeaders.AUTHORIZATION;

	@PostMapping(value = "/login")
	public ResponseEntity<Map<String, Object>> login(@RequestBody Map<String, Object> data,
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
			Map<String, Object> loginMap = customerLoginService.login(data);
			if(loginMap != null) {
				// 로그인 이력저장
				customerLoginService.insertLoginHist(loginMap);
				
				// JWT 토큰 생성 
				String token = jwtService.create("data", loginMap, "customer");
				response.setHeader("Access-Control-Expose-Headers", "Authorization");
				response.setHeader("Authorization", token);
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
		
		try {
			String token = request.getHeader(HEADER_AUTH);
			
			if(Pattern.matches("^Bearer .*", token)) {
				token = token.replaceAll("^Bearer( )*", "");
			}
						
			if(token != null && jwtService.isUsable(token) && jwtService.getExpToken(token)){
				
				String newToken=jwtService.refreshToken(token);
				response.setHeader("Access-Control-Expose-Headers", HEADER_AUTH);
				response.setHeader(HEADER_AUTH,newToken); //요청 웹브라우즈에 새로 생성한 token 되돌려줌.
			}else{
				throw new UnauthorizedException();
			}
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return new ResponseEntity<Map<String, Object>>(rcvData, HttpStatus.OK);
	}
}
