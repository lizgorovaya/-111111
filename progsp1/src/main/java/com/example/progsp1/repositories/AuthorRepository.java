package com.example.progsp1.repositories;

import com.example.progsp1.models.Author;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AuthorRepository extends JpaRepository<Author, Long> {

}

