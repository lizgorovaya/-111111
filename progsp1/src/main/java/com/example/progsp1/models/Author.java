package com.example.progsp1.models;
import lombok.Data;

import javax.persistence.*;

@Entity
@Data
public class Author {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String fullName; // ФИО автора

    @Column(columnDefinition = "TEXT")
    @Lob
    private String bio; // Биография автора

    @Lob
    private byte[] photo; // Фотография автора, хранимая как массив байтов
}

