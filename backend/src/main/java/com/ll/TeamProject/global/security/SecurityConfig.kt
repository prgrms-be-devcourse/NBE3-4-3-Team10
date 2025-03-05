package com.ll.TeamProject.global.security

import com.ll.TeamProject.global.app.AppConfig
import com.ll.TeamProject.global.exceptions.CustomAccessDeniedHandler
import com.ll.TeamProject.global.exceptions.CustomAuthenticationEntryPoint
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configurers.*
import org.springframework.security.config.annotation.web.configurers.oauth2.client.OAuth2LoginConfigurer
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter
import org.springframework.web.cors.CorsConfiguration
import org.springframework.web.cors.UrlBasedCorsConfigurationSource
import java.util.*

@Configuration
class SecurityConfig(
    private val customAuthenticationFilter: CustomAuthenticationFilter,
    private val customOAuth2AuthenticationSuccessHandler: CustomOAuth2AuthenticationSuccessHandler,
    private val authenticationEntryPoint: CustomAuthenticationEntryPoint,
    private val accessDeniedHandler: CustomAccessDeniedHandler
) {
    @Bean
    @Throws(Exception::class)
    fun baseSecurityFilterChain(http: HttpSecurity): SecurityFilterChain {
        http
            .authorizeHttpRequests {
                    authorizeRequests:
                    AuthorizeHttpRequestsConfigurer<HttpSecurity>.AuthorizationManagerRequestMatcherRegistry ->
                authorizeRequests
                    .requestMatchers("/h2-console/**")
                    .permitAll()

                    .requestMatchers(
                        "/api/admin/login",
                        "/api/admin/logout",
                        "/api/admin/verification-codes",
                        "/api/admin/verification-codes/verify",
                        "/api/admin/{username}/password"
                    ).permitAll()

                    .requestMatchers("/api/admin/**")
                    .hasRole("ADMIN")

                    .requestMatchers("/login")
                    .permitAll()

                    .requestMatchers("/api/**")
                    .authenticated()

                    .requestMatchers("/static/**", "/images/**", "/css/**", "/js/**")
                    .permitAll()

                    .anyRequest()
                    .permitAll()
            }
            .headers { headers: HeadersConfigurer<HttpSecurity> ->
                headers.frameOptions { frameOptions: HeadersConfigurer<HttpSecurity>.FrameOptionsConfig ->
                    frameOptions.sameOrigin()
                }
            }
            .csrf { csrf: CsrfConfigurer<HttpSecurity> -> csrf.disable() }
            .oauth2Login { oauth2: OAuth2LoginConfigurer<HttpSecurity> ->
                oauth2.successHandler(customOAuth2AuthenticationSuccessHandler)
            }
            .addFilterBefore(customAuthenticationFilter, UsernamePasswordAuthenticationFilter::class.java)
            .exceptionHandling { exceptionHandling: ExceptionHandlingConfigurer<HttpSecurity> ->
                exceptionHandling
                    .authenticationEntryPoint(authenticationEntryPoint)
                    .accessDeniedHandler(accessDeniedHandler)
            }
            .sessionManagement { session: SessionManagementConfigurer<HttpSecurity> ->
                session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            }

        return http.build()
    }

    @Bean
    fun corsConfigurationSource(): UrlBasedCorsConfigurationSource {
        val configuration = CorsConfiguration()
        configuration.setAllowedOriginPatterns(listOf(AppConfig.getSiteFrontUrl()))
        configuration.allowedMethods = listOf("GET", "POST", "PUT", "DELETE", "PATCH")
        configuration.allowCredentials = true
        configuration.allowedHeaders = listOf("*")
        val source = UrlBasedCorsConfigurationSource()
        source.registerCorsConfiguration("/**", configuration)
        return source
    }
}
