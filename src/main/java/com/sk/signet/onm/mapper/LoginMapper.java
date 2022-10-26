package com.sk.signet.onm.mapper;

import java.util.Map;

import org.mybatis.spring.annotation.MapperScan;

@MapperScan
@SuppressWarnings("rawtypes")
public interface LoginMapper {

	Map<String, Object> userLogin(Map param) throws Exception;

	void insertUserLoginHist(Map param);

	void updateUserLogoutHist(Map param);
	
	Map<String, Object> customerLogin(Map param) throws Exception;

	void insertCustomerLoginHist(Map param);

	void updateCustomerLogoutHist(Map param);
}
