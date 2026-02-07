package com.job.job_studio.config;

import com.job.job_studio.security.UserDetailsServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import java.util.List;

@Configuration
public class SecurityConfig {

    @Autowired
    UserDetailsServiceImpl userDetailsService;

    @Autowired
    AuthTokenFilter authTokenFilter;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                // 1. 关闭 CSRF (前后端分离必须关闭)
                .csrf(csrf -> csrf.disable())

                // 2. 开启 CORS (允许跨域)
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))

                // 3. 设置 Session 为无状态
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                // 4. 配置路径权限
                .authorizeHttpRequests(auth -> auth
                        // === 关键：放行 OPTIONS 请求 (解决 CORS 403 问题) ===
                        .requestMatchers(org.springframework.http.HttpMethod.OPTIONS, "/**").permitAll()

                        // === 关键：放行登录接口 ===
                        // 确保你的 Controller 是 @RequestMapping("/api/auth")
                        .requestMatchers("/api/auth/**").permitAll()

                        // 放行 Swagger 文档
                        .requestMatchers("/swagger-ui/**", "/v3/api-docs/**").permitAll()

                        // 放行静态资源 (如果需要)
                        .requestMatchers("/images/**", "/css/**", "/js/**").permitAll()

                        // 管理员接口权限
                        .requestMatchers("/api/admin/**").hasRole("ADMIN")

                        // 其他接口需认证
                        .anyRequest().authenticated()
                );

        http.authenticationProvider(authenticationProvider());
        http.addFilterBefore(authTokenFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    // === 关键：标准的 CORS 配置源 ===
    @Bean
    public UrlBasedCorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowCredentials(true); // 允许携带 Cookie/凭证

        // 允许的前端域名 (开发环境用 *，生产环境建议指定 http://localhost:5173)
        config.addAllowedOriginPattern("*");

        config.addAllowedHeader("*"); // 允许所有 Header (包括 Authorization)
        config.addAllowedMethod("*"); // 允许所有方法 (POST, GET, OPTIONS...)

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }
}