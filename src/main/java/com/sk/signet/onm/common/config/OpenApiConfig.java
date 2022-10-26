package com.sk.signet.onm.common.config;

import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

/**
 * Config for Swagger Open API v3.x  
 * @packagename : com.sk.signet.onm.common.config
 * @filename 	: OpenApiConfig.java 
 * @since 		: 2022.10.04 
 * @description : 
 * =================================================================
 * Date				Author			Version			Note			
 * -----------------------------------------------------------------
 * 2022.10.04 		Heo, Sehwan		1.0				최초 생성
 * -----------------------------------------------------------------
 */
@Component
public class OpenApiConfig {
	
	@Value("${openapi.title}")
	String title;
	
	@Value("${openapi.description}")
	String description;
	
	@Value("${openapi.termsOfService}")
	String termsOfService;
	
	@Value("${openapi.contact.name}")
	String contactName;
	
	@Value("${openapi.contact.url}")
	String contactUrl;
	
	@Value("${openapi.contact.email}")
	String contactEmail;
	
	@Value("${openapi.license.name}")
	String licenseName;
	
	@Value("${openapi.license.url}")
	String licenseUrl;
	
	@Value("v1.0")
	String appVersion;
	
	@Bean
    public OpenAPI openAPI(@Value("v0.1") String appVersion) {

//		final String securitySchemeName = "Authorization";
		final String securitySchemeName = "bearer";
		
		
        Info info = new Info().title(title).version(appVersion)
                .description(description)
                .termsOfService(termsOfService)
                .contact(new Contact().name(contactName).url(contactUrl).email(contactEmail))
                .license(new License().name(licenseName).url(licenseUrl));
        
        // API 요청헤더에 인증정보 포함
        SecurityRequirement securityRequirement = new SecurityRequirement().addList(securitySchemeName);
        // SecuritySchemes 등록
        Components components = new Components()
                .addSecuritySchemes(securitySchemeName, new SecurityScheme()
                				.name(securitySchemeName)
                                .type(SecurityScheme.Type.HTTP) // HTTP 방식
                                .scheme("bearer")
                                .bearerFormat("JWT")); // 토큰 형식을 지정하는 임의의 문자(Optional)
        

        return new OpenAPI()
                .components(components)
                .addSecurityItem(securityRequirement)
                .info(info);
    }
}
