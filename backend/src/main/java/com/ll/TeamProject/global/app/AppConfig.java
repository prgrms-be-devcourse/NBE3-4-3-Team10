package com.ll.TeamProject.global.app;

import org.springframework.context.annotation.Configuration;

@Configuration
public class AppConfig {

    // 프론트엔드 URL 반환
    public static String getSiteFrontUrl() {
        return "http://localhost:3000";
    }

//    // CORS
//    @Bean
//    public WebMvcConfigurer corsConfigurer() {
//        return new WebMvcConfigurer() {
//            @Override
//            public void addCorsMappings(CorsRegistry registry) {
//                registry.addMapping("/**")
//                        .allowedOrigins(getSiteFrontUrl()) // 기존 설정 활용
//                        .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
//                        .allowCredentials(true);
//            }
//        };
//    }
}