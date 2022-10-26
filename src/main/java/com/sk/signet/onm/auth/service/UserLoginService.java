package com.sk.signet.onm.auth.service;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sk.signet.onm.mapper.LoginMapper;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service("userLoginService")
@SuppressWarnings({ "unchecked", "rawtypes", "unused" })
public class UserLoginService {

	@Autowired
	private LoginMapper mapper;
	
	public Map<String, Object> login( Map param) throws Exception {
		
		try {
			Map<String, Object> loginMap = mapper.userLogin(param);

			return loginMap;
		} catch(Exception e) {
			log.error(" userLoginService Exception===============>" + e);
			return null;
		}
	}
	
	//로그인 추적이력생성
	@Transactional
	public void insertLoginHist(Map param) throws Exception {
	
		mapper.insertUserLoginHist(param);
	}    

    //로그아웃 이력업데이트 2021.12.20 김도형
	public void updateLogoutHist(Map param) throws Exception {
	
		mapper.updateUserLogoutHist(param);
	}
}
