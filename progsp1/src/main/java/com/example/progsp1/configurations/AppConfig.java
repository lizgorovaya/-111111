package com.example.progsp1.configurations;

import com.example.progsp1.models.Book;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class AppConfig {

    @Bean
    public Map<Book, Integer> orderCounts() {
        // Верните карту, например
        return new HashMap<>();
    }

    @Bean
    public String chartTitle() {
        return "Book Statistics";
    }

    @Bean
    public Map<Book, Integer> reviewCounts() {
        // Верните карту
        return new HashMap<>();
    }
}

