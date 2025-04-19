//package com.example.progsp1.configurations;
//
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.security.config.annotation.web.builders.HttpSecurity;
//import org.springframework.security.web.SecurityFilterChain;
//import org.springframework.web.bind.annotation.CrossOrigin;
//import org.springframework.web.cors.CorsConfiguration;
//import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
//import org.springframework.web.filter.CorsFilter;
//@Configuration
//public class CORSConfig {
//    @Bean
//    public CorsFilter corsFilter() {
//        CorsConfiguration config = new CorsConfiguration();
//        config.setAllowCredentials(true); // Разрешаем куки
//        config.addAllowedOriginPattern("http://127.0.0.1:5500"); // Используем allowedOriginPatterns для разрешения CORS с конкретных источников
//        config.addAllowedHeader("*"); // Разрешаем любые заголовки
//        config.addAllowedMethod("*"); // Разрешаем любые методы (GET, POST, PUT, DELETE и т.д.)
//
//        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
//        source.registerCorsConfiguration("/**", config); // Применяем ко всем путям
//
//        return new CorsFilter(source);
//    }
//
//    @Bean
//    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
//        http.cors().configurationSource(request -> {
//                    CorsConfiguration config = new CorsConfiguration();
//                    config.setAllowCredentials(true);
//                    config.addAllowedOrigin("http://127.0.0.1:5500");
//                    config.addAllowedHeader("*");
//                    config.addAllowedMethod("*");
//                    return config;
//                }).and().csrf().disable()
//                .authorizeRequests()
//                .antMatchers("/api/orders/create").authenticated()
//                .anyRequest().permitAll();
//        return http.build();
//    }
//
//}
