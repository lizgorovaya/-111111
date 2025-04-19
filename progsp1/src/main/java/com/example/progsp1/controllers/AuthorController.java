package com.example.progsp1.controllers;
import com.example.progsp1.models.Author;
import com.example.progsp1.repositories.AuthorRepository;
import com.example.progsp1.servicies.AuthorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@RestController
@CrossOrigin(origins = "http://127.0.0.1:5500")
@RequestMapping("/api/authors")
public class AuthorController {

    private final AuthorService authorService;
    private final AuthorRepository authorRepository;

    @Autowired
    public AuthorController(AuthorService authorService, AuthorRepository authorRepository) {
        this.authorService = authorService;
        this.authorRepository = authorRepository;
    }

    @PostMapping
    public ResponseEntity<Author> createAuthor(
            @RequestParam("fullName") String fullName,
            @RequestParam("bio") String bio,
            @RequestParam(value = "photo", required = false) MultipartFile photo) {
        try {
            Author author = authorService.saveAuthor(fullName, bio, photo);
            return ResponseEntity.status(HttpStatus.CREATED).body(author);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

//    @GetMapping("/{id}")
//    public ResponseEntity<Author> getAuthor(@PathVariable Long id) {
//        try {
//            Author author = authorService.getAuthorById(id);
//            return ResponseEntity.ok(author);
//        } catch (Exception e) {
//            return ResponseEntity.notFound().build();
//        }
//    }
    @GetMapping("/{id}")
    public ResponseEntity<Author> getAuthorById(@PathVariable("id") Long id) {
        Optional<Author> author = authorService.findAuthorById(id); // Предполагается, что у вас есть AuthorService
        return author.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }


    @GetMapping
    public ResponseEntity<List<Author>> getAllAuthors() {
        List<Author> authors = authorService.getAllAuthors();
        return ResponseEntity.ok(authors);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAuthor(@PathVariable Long id) {
        authorService.deleteAuthorById(id); // Удаляет автора и связанные книги
        return ResponseEntity.noContent().build();
    }

//    @GetMapping("/authors/{id}")
//    public ResponseEntity<Author> getAuthorByIdFromRepository(@PathVariable Long id) {
//        Optional<Author> author = authorRepository.findById(id);
//        return author.map(ResponseEntity::ok)
//                .orElse(ResponseEntity.notFound().build());
//    }
}
