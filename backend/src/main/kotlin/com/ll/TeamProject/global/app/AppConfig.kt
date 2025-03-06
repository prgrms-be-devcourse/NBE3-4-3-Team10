package com.ll.TeamProject.global.app

import org.springframework.context.annotation.Configuration

@Configuration
class AppConfig {

    companion object {
        // 프론트엔드 URL 반환
        fun getSiteFrontUrl(): String {
            return "http://localhost:3000"
        }
    }

//    // CORS 설정 예제 (필요 시 활성화)
//    @Bean
//    fun corsConfigurer(): WebMvcConfigurer {
//        return object : WebMvcConfigurer {
//            override fun addCorsMappings(registry: CorsRegistry) {
//                registry.addMapping("/**")
//                    .allowedOrigins(getSiteFrontUrl()) // 기존 설정 활용
//                    .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
//                    .allowCredentials(true)
//            }
//        }
//    }
}
